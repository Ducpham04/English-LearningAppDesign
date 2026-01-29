package com.example.appenglish.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

/**
 * Thực thể Test (Bài kiểm tra/Quiz).
 * Liên kết giữa Lesson và danh sách các câu hỏi.
 */
@Entity
@Table(name = "tests")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Test extends BaseEntity {

    private String title;
    
    private Integer duration; // Phút (null = không giới hạn)
    
    @Column(name = "pass_score")
    private Integer passScore; // Điểm sàn để vượt qua

    @Column(name = "is_face_required")
    private Boolean isFaceRequired; // Yêu cầu xác thực khuôn mặt khi làm bài

    // --- RELATIONS ---

    /**
     * Liên kết 1-1 với Lesson. 
     * Một bài học loại QUIZ sẽ có một thực thể Test tương ứng.
     */
    @OneToOne
    @JoinColumn(name = "lesson_id")
    private Lesson lesson;

    /**
     * Liên kết 1-nhiều với Question.
     * Một bài test bao gồm nhiều câu hỏi. 
     * Đây là trường giải quyết lỗi getQuestions() của bạn.
     */
    @OneToMany(mappedBy = "test", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Question> questions;
}