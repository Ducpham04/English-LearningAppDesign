package com.example.appenglish.modules.course.service;

import com.example.appenglish.entity.*;
import com.example.appenglish.enums.LessonType;
import com.example.appenglish.repository.*;
import com.example.appenglish.modules.course.dto.LessonCompleteRequest;
import com.example.appenglish.modules.course.dto.LessonCompleteResponse;
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
     * API 5.3: Lấy chi tiết bài học (Video URL, Subtitles, Progress)
     */
    public LessonDetailsResponse getLessonDetails(Long id) {
        Lesson lesson = lessonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài học"));

        // 1. Build cấu trúc phản hồi cơ bản
        LessonDetailsResponse response = LessonDetailsResponse.builder()
                .lessonId(lesson.getId())
                .sessionId(lesson.getSession() != null ? lesson.getSession().getId() : null)
                .title(lesson.getTitle())
                .type(lesson.getType())
                .description(lesson.getDescription())
                .duration(lesson.getDurationFromPayload())
                .order(lesson.getOrderIndex())
                .status("completed") // Mock: Thực tế sẽ lấy từ UserLessonProgress
                .build();

        // 2. Xử lý dữ liệu đặc thù cho bài học VIDEO
        if (lesson.getType() == LessonType.VIDEO && lesson.getVideo() != null) {
            response.setVideoUrl(lesson.getVideo().getVideoUrl());
            response.setSubtitles(getMockSubtitles());
            response.setTargetWords(List.of("hello", "my", "name", "is"));
        }

        // 3. Xử lý danh sách câu hỏi nếu bài học là QUIZ
        if (lesson.getTest() != null) {
            Test test = lesson.getTest();
            response.setQuizId(test.getId());
            response.setMinScore(70);

            if (lesson.getType() == LessonType.QUIZ && test.getQuestions() != null) {
                response.setQuestions(test.getQuestions().stream()
                        .map(q -> LessonDetailsResponse.QuestionResponse.builder()
                                .id(q.getId())
                                .content(q.getContent())
                                .options(q.getOptions() != null ? q.getOptions().stream()
                                        .map(o -> LessonDetailsResponse.OptionResponse.builder()
                                                .id(o.getId())
                                                .content(o.getContent())
                                                .build())
                                        .collect(Collectors.toList()) : new ArrayList<>())
                                .build())
                        .collect(Collectors.toList()));
            }
        }

        // Mock thông tin tiến độ cá nhân
        response.setUserScore(85.0);
        response.setCompletedAt(java.time.LocalDateTime.now());

        return response;
    }

    /**
     * API 5.5: Xử lý Hoàn thành bài học & Chấm điểm Quiz
     */
    @Transactional
    public LessonCompleteResponse completeLesson(Long lessonId, Long userId, LessonCompleteRequest request) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Lesson not found with id: " + lessonId));

        // Mặc định lấy score từ request, sẽ bị ghi đè nếu có nộp answers (Quiz)
        Double finalScore = request.getScore();

        // 1. Logic chấm điểm tự động nếu có gửi kèm danh sách answers
        if (request.getAnswers() != null && !request.getAnswers().isEmpty() && lesson.getTest() != null) {
            Test test = lesson.getTest();
            List<Question> questions = test.getQuestions() != null ? test.getQuestions() : new ArrayList<>();
            int correctCount = 0;

            for (Question q : questions) {
                Long selectedId = request.getAnswers().get(q.getId());
                if (q.getOptions() != null) {
                    boolean isCorrect = q.getOptions().stream()
                            .anyMatch(o -> o.getId().equals(selectedId) && Boolean.TRUE.equals(o.getIsCorrect()));
                    if (isCorrect) correctCount++;
                }
            }
            
            // Tính điểm trên thang 100 (ví dụ: 85.0) để khớp format Swagger
            finalScore = questions.isEmpty() ? 0.0 : ((double) correctCount / questions.size()) * 100;
            
            // Lưu lịch sử nộp bài vào DB
            User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
            testAttemptRepository.save(TestAttempt.builder()
                    .test(test)
                    .user(user)
                    .score(finalScore)
                    .correctAnswers(correctCount)
                    .build());
        }

        // 2. Tìm bài học tiếp theo trong cùng Session để gợi ý cho người dùng
        Optional<Lesson> nextLessonOpt = lessonRepository.findFirstBySessionIdAndOrderIndexGreaterThanOrderByOrderIndexAsc(
                lesson.getSession().getId(), lesson.getOrderIndex());

        LessonCompleteResponse.NextLessonInfo nextLessonInfo = null;
        if (nextLessonOpt.isPresent()) {
            Lesson next = nextLessonOpt.get();
            nextLessonInfo = LessonCompleteResponse.NextLessonInfo.builder()
                    .id("lsn_" + String.format("%03d", next.getId()))
                    .title(next.getTitle())
                    .status("locked")
                    .build();
        }

        // 3. Trả về kết quả theo format Swagger (bao gồm XP và Huy hiệu)
        return LessonCompleteResponse.builder()
                .lessonId("lsn_" + String.format("%03d", lessonId))
                .completed(true)
                .score(finalScore != null ? Math.round(finalScore * 10.0) / 10.0 : 0.0)
                .xpEarned(50) // Mock: Thưởng 50 XP
                .badgesUnlocked(List.of("badge_first_lesson")) // Mock huy hiệu
                .nextLesson(nextLessonInfo)
                .build();
    }

    /**
     * Helper: Tạo dữ liệu phụ đề mẫu cho bài học Video
     */
    private List<LessonDetailsResponse.SubtitleResponse> getMockSubtitles() {
        return List.of(
            LessonDetailsResponse.SubtitleResponse.builder()
                .id("sub_001")
                .text("Hello, my name is Sarah.")
                .startTime(0.0)
                .endTime(2.0)
                .words(List.of(
                    LessonDetailsResponse.WordDetail.builder()
                        .text("Hello").pronunciation("/həˈloʊ/").meaning("Xin chào").startTime(0.0).endTime(0.5).build(),
                    LessonDetailsResponse.WordDetail.builder()
                        .text("my").pronunciation("/maɪ/").meaning("của tôi").startTime(0.6).endTime(0.8).build()
                ))
                .build()
        );
    }
}