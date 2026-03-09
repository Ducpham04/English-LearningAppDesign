package com.example.appenglish.modules.test.service;

import com.example.appenglish.entity.*;
import com.example.appenglish.enums.QuestionType;
import com.example.appenglish.modules.test.dto.*;
import com.example.appenglish.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TestService {

    private final TestRepository testRepository;
    private final TestQuestionRepository testQuestionRepository;
    private final TestOptionRepository testOptionRepository;
    private final TestAttemptRepository testAttemptRepository;
    private final TestAnswerRepository testAnswerRepository;
    private final com.example.appenglish.repository.CurriculumRepository curriculumRepository;
    private final com.example.appenglish.repository.LessonRepository lessonRepository;

    // ---------- Student: list tests với thống kê attempt ----------
    public List<TestDto> listForStudent(String userId) {
        List<Test> tests = testRepository.findAll();
        List<TestDto> result = new ArrayList<>();
        for (Test t : tests) {
            result.add(toDtoWithAttempts(t, userId));
        }
        return result;
    }

    // ---------- Student: lấy bài test để làm (không có đáp án đúng) ----------
    public TestTakeDto getForTaking(Long testId) {
        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new RuntimeException("Test not found: " + testId));
        List<TestQuestion> questions = testQuestionRepository.findByTest_IdOrderByOrderIndexAscIdAsc(testId);
        int totalPoints = questions.stream().mapToInt(q -> q.getPoints() != null ? q.getPoints() : 1).sum();
        List<QuestionTakeDto> questionDtos = new ArrayList<>();
        for (TestQuestion q : questions) {
            List<OptionTakeDto> options = new ArrayList<>();
            if (q.getType() == QuestionType.MCQ) {
                List<TestOption> opts = testOptionRepository.findByQuestion_IdOrderByIdAsc(q.getId());
                for (TestOption o : opts) {
                    options.add(OptionTakeDto.builder().id(o.getId()).content(o.getContent()).build());
                }
            }
            questionDtos.add(QuestionTakeDto.builder()
                    .id(q.getId())
                    .content(q.getContent())
                    .type(q.getType().name())
                    .points(q.getPoints() != null ? q.getPoints() : 1)
                    .orderIndex(q.getOrderIndex())
                    .options(options)
                    .build());
        }
        return TestTakeDto.builder()
                .id(test.getId())
                .title(test.getTitle())
                .durationMinutes(test.getDuration())
                .passScore(test.getPassScore())
                .totalPoints(totalPoints)
                .questions(questionDtos)
                .build();
    }

    // ---------- Student: nộp bài và chấm điểm ----------
    @Transactional
    public AttemptResultDto submitAttempt(Long testId, String userId, SubmitAttemptRequest request) {
        Test test = testRepository.findById(testId)
                .orElseThrow(() -> new RuntimeException("Test not found: " + testId));
        User user = new User();
        user.setId(userId);

        int maxAttempts = test.getMaxAttempts() != null ? test.getMaxAttempts() : Integer.MAX_VALUE;
        int currentCount = testAttemptRepository.countByTest_IdAndUser_Id(testId, userId);
        if (currentCount >= maxAttempts) {
            throw new RuntimeException("Bạn đã hết số lần làm bài cho bài test này.");
        }

        List<TestQuestion> questions = testQuestionRepository.findByTest_IdOrderByOrderIndexAscIdAsc(testId);
        Map<Long, TestQuestion> questionMap = questions.stream().collect(Collectors.toMap(TestQuestion::getId, q -> q));
        Map<Long, AnswerItem> answerMap = new HashMap<>();
        if (request.getAnswers() != null) {
            for (AnswerItem a : request.getAnswers()) {
                answerMap.put(a.getQuestionId(), a);
            }
        }

        int totalScore = 0;
        int totalPoints = 0;
        int correctCount = 0;
        List<AnswerResultItem> breakdown = new ArrayList<>();

        TestAttempt attempt = new TestAttempt();
        attempt.setTest(test);
        attempt.setUser(user);
        attempt.setSubmittedAt(LocalDateTime.now());
        attempt.setTotalScore(0);
        attempt = testAttemptRepository.save(attempt);

        for (TestQuestion q : questions) {
            int points = q.getPoints() != null ? q.getPoints() : 1;
            totalPoints += points;
            AnswerItem userAnswer = answerMap.get(q.getId());
            boolean correct = false;
            int earned = 0;
            if (userAnswer != null && userAnswer.getUserAnswer() != null && !userAnswer.getUserAnswer().isBlank()) {
                correct = isAnswerCorrect(q, userAnswer.getUserAnswer());
                if (correct) {
                    earned = points;
                    correctCount++;
                }
            }
            totalScore += earned;
            breakdown.add(new AnswerResultItem(q.getId(), correct, earned, points));

            TestAnswer ta = new TestAnswer();
            ta.setAttempt(attempt);
            ta.setQuestion(q);
            ta.setUserAnswer(userAnswer != null ? userAnswer.getUserAnswer() : null);
            ta.setIsCorrect(correct);
            ta.setScoreEarned(earned);
            testAnswerRepository.save(ta);
        }

        attempt.setTotalScore(totalScore);
        testAttemptRepository.save(attempt);

        int passScore = test.getPassScore() != null ? test.getPassScore() : 70;
        boolean passed = totalPoints > 0 && (totalScore * 100 / totalPoints) >= passScore;

        return AttemptResultDto.builder()
                .attemptId(attempt.getId())
                .testId(testId)
                .totalScore(totalScore)
                .totalPoints(totalPoints)
                .passScore(passScore)
                .passed(passed)
                .correctCount(correctCount)
                .totalQuestions(questions.size())
                .submittedAt(attempt.getSubmittedAt().atZone(java.time.ZoneId.systemDefault()).toInstant())
                .breakdown(breakdown)
                .build();
    }

    private boolean isAnswerCorrect(TestQuestion q, String userAnswer) {
        if (q.getCorrectSchema() == null) return false;
        if (q.getType() == QuestionType.MCQ) {
            Object co = q.getCorrectSchema().get("correct_option_id");
            if (co == null) return false;
            long correctId = co instanceof Number ? ((Number) co).longValue() : Long.parseLong(co.toString());
            try {
                long userOptionId = Long.parseLong(userAnswer.trim());
                return userOptionId == correctId;
            } catch (NumberFormatException e) {
                return false;
            }
        }
        if (q.getType() == QuestionType.FILL_BLANK) {
            Object acc = q.getCorrectSchema().get("accepted");
            if (acc instanceof Collection) {
                String u = userAnswer.trim();
                for (Object o : (Collection<?>) acc) {
                    if (o != null && o.toString().trim().equalsIgnoreCase(u)) return true;
                }
            }
            return false;
        }
        // SPEAKING: không tự động chấm
        return false;
    }

    private TestDto toDtoWithAttempts(Test t, String userId) {
        int attemptCount = 0;
        Integer bestScore = null;
        Instant lastAttemptAt = null;
        String status = "not-started";
        if (userId != null) {
            attemptCount = testAttemptRepository.countByTest_IdAndUser_Id(t.getId(), userId);
            Optional<TestAttempt> best = testAttemptRepository.findFirstByTest_IdAndUser_IdOrderByTotalScoreDesc(t.getId(), userId);
            List<TestAttempt> attempts = testAttemptRepository.findByTest_IdAndUser_IdOrderBySubmittedAtDesc(t.getId(), userId);
            if (!attempts.isEmpty()) {
                lastAttemptAt = attempts.get(0).getSubmittedAt().atZone(java.time.ZoneId.systemDefault()).toInstant();
                bestScore = best.map(TestAttempt::getTotalScore).orElse(null);
                if (t.getMaxAttempts() != null && attemptCount >= t.getMaxAttempts()) {
                    status = "completed";
                } else {
                    status = "in-progress";
                }
            }
        }
        List<TestQuestion> qList = testQuestionRepository.findByTest_IdOrderByOrderIndexAscIdAsc(t.getId());
        int questionCount = qList.size();
        int totalPoints = qList.stream().mapToInt(q -> q.getPoints() != null ? q.getPoints() : 1).sum();
        if (userId != null && bestScore != null && t.getPassScore() != null && totalPoints > 0) {
            int pct = bestScore * 100 / totalPoints;
            if (pct >= t.getPassScore()) status = "completed";
        }

        return TestDto.builder()
                .id(t.getId())
                .title(t.getTitle())
                .durationMinutes(t.getDuration())
                .passScore(t.getPassScore())
                .maxAttempts(t.getMaxAttempts())
                .deadline(t.getDeadline())
                .isFaceRequired(t.getIsFaceRequired())
                .courseId(t.getCourse() != null ? t.getCourse().getId() : null)
                .courseTitle(t.getCourse() != null ? t.getCourse().getTitle() : null)
                .lessonId(t.getLesson() != null ? t.getLesson().getId() : null)
                .questionCount(questionCount)
                .totalPoints(totalPoints)
                .attemptCount(attemptCount)
                .bestScore(bestScore)
                .lastAttemptAt(lastAttemptAt)
                .status(status)
                .build();
    }

    // ---------- Admin/Teacher: CRUD Test ----------
    public List<TestDto> listAll() {
        return testRepository.findAll().stream()
                .map(t -> toDtoWithAttempts(t, null))
                .collect(Collectors.toList());
    }

    public TestDto getById(Long id) {
        Test t = testRepository.findById(id).orElseThrow(() -> new RuntimeException("Test not found: " + id));
        return toDtoWithAttempts(t, null);
    }

    @Transactional
    public TestDto create(TestCreateRequest req) {
        Test test = new Test();
        test.setTitle(req.getTitle());
        test.setDuration(req.getDurationMinutes());
        test.setPassScore(req.getPassScore());
        test.setMaxAttempts(req.getMaxAttempts());
        test.setDeadline(req.getDeadline());
        test.setIsFaceRequired(req.getIsFaceRequired() != null && req.getIsFaceRequired());
        if (req.getCourseId() != null) {
            curriculumRepository.findById(req.getCourseId()).ifPresent(test::setCourse);
        }
        if (req.getLessonId() != null) {
            lessonRepository.findById(req.getLessonId()).ifPresent(test::setLesson);
        }
        test = testRepository.save(test);
        return toDtoWithAttempts(test, null);
    }

    @Transactional
    public TestDto update(Long id, TestCreateRequest req) {
        Test test = testRepository.findById(id).orElseThrow(() -> new RuntimeException("Test not found: " + id));
        if (req.getTitle() != null) test.setTitle(req.getTitle());
        if (req.getDurationMinutes() != null) test.setDuration(req.getDurationMinutes());
        if (req.getPassScore() != null) test.setPassScore(req.getPassScore());
        if (req.getMaxAttempts() != null) test.setMaxAttempts(req.getMaxAttempts());
        if (req.getDeadline() != null) test.setDeadline(req.getDeadline());
        if (req.getIsFaceRequired() != null) test.setIsFaceRequired(req.getIsFaceRequired());
        if (req.getCourseId() != null) {
            curriculumRepository.findById(req.getCourseId()).ifPresent(test::setCourse);
        } else {
            test.setCourse(null);
        }
        if (req.getLessonId() != null) {
            lessonRepository.findById(req.getLessonId()).ifPresent(test::setLesson);
        } else {
            test.setLesson(null);
        }
        test = testRepository.save(test);
        return toDtoWithAttempts(test, null);
    }

    @Transactional
    public void delete(Long id) {
        if (!testRepository.existsById(id)) throw new RuntimeException("Test not found: " + id);
        testRepository.deleteById(id);
    }

    // ---------- Admin/Teacher: thêm/sửa/xóa câu hỏi ----------
    @Transactional
    public QuestionTakeDto addQuestion(Long testId, TestQuestionCreateRequest req) {
        Test test = testRepository.findById(testId).orElseThrow(() -> new RuntimeException("Test not found: " + testId));
        TestQuestion q = new TestQuestion();
        q.setTest(test);
        q.setContent(req.getContent());
        q.setOrderIndex(req.getOrderIndex() != null ? req.getOrderIndex() : 0);
        q.setPoints(req.getPoints() != null ? req.getPoints() : 1);
        if (req.getType() != null) {
            try {
                q.setType(QuestionType.valueOf(req.getType()));
            } catch (IllegalArgumentException e) {
                q.setType(QuestionType.MCQ);
            }
        } else {
            q.setType(QuestionType.MCQ);
        }
        q.setCorrectSchema(req.getCorrectSchema());
        q = testQuestionRepository.save(q);
        if (req.getOptions() != null && !req.getOptions().isEmpty()) {
            int correctOptionIndex = req.getCorrectOptionIndex() != null ? req.getCorrectOptionIndex() : 0;
            Long correctOptionId = null;
            for (int i = 0; i < req.getOptions().size(); i++) {
                TestOptionCreateRequest oReq = req.getOptions().get(i);
                TestOption opt = new TestOption();
                opt.setQuestion(q);
                opt.setContent(oReq.getContent());
                opt.setIsCorrect(oReq.getIsCorrect() != null && oReq.getIsCorrect());
                opt = testOptionRepository.save(opt);
                if (i == correctOptionIndex) {
                    correctOptionId = opt.getId();
                }
            }
            if (correctOptionId != null && q.getType() == QuestionType.MCQ) {
                q.setCorrectSchema(java.util.Map.of("correct_option_id", correctOptionId));
                testQuestionRepository.save(q);
            }
        }
        List<TestOption> opts = testOptionRepository.findByQuestion_IdOrderByIdAsc(q.getId());
        return QuestionTakeDto.builder()
                .id(q.getId())
                .content(q.getContent())
                .type(q.getType().name())
                .points(q.getPoints())
                .orderIndex(q.getOrderIndex())
                .options(opts.stream().map(o -> OptionTakeDto.builder().id(o.getId()).content(o.getContent()).build()).collect(Collectors.toList()))
                .build();
    }

    @Transactional
    public void deleteQuestion(Long questionId) {
        testQuestionRepository.deleteById(questionId);
    }
}
