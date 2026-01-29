package com.example.appenglish.modules.course.service;

import com.example.appenglish.entity.*;
import com.example.appenglish.enums.LessonType;
import com.example.appenglish.repository.*;
import com.example.appenglish.modules.course.dto.LessonDetailsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LessonService {

    private final LessonRepository lessonRepository;
    private final TestAttemptRepository testAttemptRepository;
    private final UserRepository userRepository;

    public LessonDetailsResponse getLessonDetails(Long id) {
        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài học"));

        // Build Response cơ bản
        LessonDetailsResponse response = LessonDetailsResponse.builder()
                .lessonId(lesson.getId()) // ID chạy từ 1
                .title(lesson.getTitle())
                .type(lesson.getType())
                .orderIndex(lesson.getOrderIndex())
                .metadata(lesson.getContentPayload()) // Lấy dữ liệu từ JSONB
                .build();

        // 1. Nếu là bài học VIDEO
        if (lesson.getType() == LessonType.VIDEO && lesson.getVideo() != null) {
            response.setVideoUrl(lesson.getVideo().getVideoUrl());
        }

        // 2. Nếu bài học có gắn QUIZ (Bất kể type là gì, nếu có Test thì vẫn lấy quizId)
        if (lesson.getTest() != null) {
            Test test = lesson.getTest();
            response.setQuizId(test.getId()); // quizID liên quan

            // Nếu đúng type là QUIZ thì mới lấy danh sách câu hỏi
            if (lesson.getType() == LessonType.QUIZ) {
                response.setQuestions(test.getQuestions().stream()
                    .map(q -> LessonDetailsResponse.QuestionResponse.builder()
                        .id(q.getId())
                        .content(q.getContent())
                        .options(q.getOptions().stream()
                            .map(o -> LessonDetailsResponse.OptionResponse.builder()
                                .id(o.getId())
                                .content(o.getContent())
                                .build())
                            .collect(Collectors.toList()))
                        .build())
                    .collect(Collectors.toList()));
            }
        }

        return response;
    }

    /**
     * Chấm điểm bài Quiz
     */
    @Transactional
    public Map<String, Object> submitQuiz(Long lessonId, Long userId, Map<Long, Long> userAnswers) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Bài học không tồn tại"));
        
        Test test = lesson.getTest();
        if (test == null) throw new RuntimeException("Bài học không có Quiz đi kèm");

        List<Question> questions = test.getQuestions();
        int correctCount = 0;

        for (Question q : questions) {
            Long selectedId = userAnswers.get(q.getId());
            boolean isCorrect = q.getOptions().stream()
                    .anyMatch(o -> o.getId().equals(selectedId) && Boolean.TRUE.equals(o.getIsCorrect()));
            if (isCorrect) correctCount++;
        }

        double score = questions.isEmpty() ? 0 : (double) correctCount / questions.size() * 10;

        // Lưu lịch sử (ID của TestAttempt cũng tự tăng từ 1)
        User user = userRepository.findById(userId).orElseThrow();
        TestAttempt attempt = TestAttempt.builder()
                .test(test)
                .user(user)
                .score(score)
                .correctAnswers(correctCount)
                .build();
        testAttemptRepository.save(attempt);

        Map<String, Object> result = new HashMap<>();
        result.put("lessonId", lessonId);
        result.put("quizId", test.getId());
        result.put("score", Math.round(score * 100.0) / 100.0);
        result.put("correctCount", correctCount);
        result.put("passed", score >= 5.0);

        return result;
    }
}