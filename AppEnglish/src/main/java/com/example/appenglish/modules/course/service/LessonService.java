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

    /**
     * Teacher/Admin tạo xong, Học sinh gọi hàm này để lấy bài về làm
     */
    public LessonDetailsResponse getLessonDetails(Long lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Bài học không tồn tại"));

        LessonDetailsResponse response = LessonDetailsResponse.builder()
                .id(lesson.getId())
                .title(lesson.getTitle())
                .type(lesson.getType())
                .build();

        // Nếu là QUIZ, trả về các câu hỏi nhưng GIẤU ĐÁP ÁN ĐÚNG
        if (lesson.getType() == LessonType.QUIZ && lesson.getTest() != null) {
            Test test = lesson.getTest();
            response.setQuizId(test.getId()); // Gắn QuizID liên quan

            List<LessonDetailsResponse.QuestionResponse> questions = test.getQuestions().stream()
                .map(q -> LessonDetailsResponse.QuestionResponse.builder()
                    .id(q.getId())
                    .content(q.getContent())
                    .options(q.getOptions().stream()
                        .map(o -> LessonDetailsResponse.OptionResponse.builder()
                            .id(o.getId())
                            .content(o.getContent()) // Tuyệt đối không gửi isCorrect về
                            .build())
                        .collect(Collectors.toList()))
                    .build())
                .collect(Collectors.toList());
            response.setQuestions(questions);
        }
        return response;
    }

    /**
     * Học sinh nộp bài, hệ thống tự động chấm điểm
     */
    @Transactional
    public Map<String, Object> submitQuiz(Long lessonId, Long userId, Map<Long, Long> userAnswers) {
        Lesson lesson = lessonRepository.findById(lessonId).orElseThrow();
        Test test = lesson.getTest();
        
        int correctCount = 0;
        List<Question> questions = test.getQuestions();

        for (Question q : questions) {
            Long selectedOptionId = userAnswers.get(q.getId());
            // So khớp với Database
            boolean correct = q.getOptions().stream()
                    .anyMatch(o -> o.getId().equals(selectedOptionId) && Boolean.TRUE.equals(o.getIsCorrect()));
            if (correct) correctCount++;
        }

        double score = (double) correctCount / questions.size() * 10;

        // Lưu lịch sử làm bài cho học sinh
        User student = userRepository.findById(userId).orElseThrow();
        TestAttempt attempt = TestAttempt.builder()
                .test(test)
                .user(student)
                .score(score)
                .correctAnswers(correctCount)
                .build();
        testAttemptRepository.save(attempt);

        Map<String, Object> result = new HashMap<>();
        result.put("score", score);
        result.put("correctCount", correctCount);
        result.put("passed", score >= 5.0);
        return result;
    }
}