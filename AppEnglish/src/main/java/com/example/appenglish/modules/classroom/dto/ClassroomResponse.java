package com.example.appenglish.modules.classroom.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;

/**
 * DTO cho Classroom (Response).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(name = "ClassroomResponse")
public class ClassroomResponse {

    @Schema(description = "ID lớp học", example = "1")
    private Long id;

    @Schema(description = "Tên lớp học", example = "Lớp A1 - Tiếng Anh Cơ Bản")
    private String name;

    @Schema(description = "ID giáo viên", example = "1")
    private Long teacherId;

    @Schema(description = "Tên giáo viên", example = "Nguyễn Văn A")
    private String teacherName;

    @Schema(description = "Ngày bắt đầu", example = "2026-01-15")
    private LocalDate startDate;

    @Schema(description = "Ngày kết thúc", example = "2026-12-31")
    private LocalDate endDate;

    @Schema(description = "Trạng thái lớp", example = "ACTIVE")
    private String status;

    @Schema(description = "Tổng số học viên", example = "30")
    private Integer totalStudents;

    @Schema(description = "Số học viên hoàn thành", example = "15")
    private Integer completedStudents;

    @Schema(description = "Phần trăm tiến độ lớp (0-100)", example = "50.0")
    private Double progressPercentage;

    @Schema(description = "Ngày tạo", example = "2026-01-10T10:00:00")
    private String createdAt;
}
