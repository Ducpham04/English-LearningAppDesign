package com.example.appenglish.modules.classroom.service;

import com.example.appenglish.entity.Classroom;
import com.example.appenglish.entity.ClassStudent;
import com.example.appenglish.modules.classroom.dto.ClassroomResponse;
import com.example.appenglish.repository.ClassroomRepository;
import com.example.appenglish.repository.ClassStudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * Service cho Classroom.
 */
@Service
@RequiredArgsConstructor
public class ClassroomService {

    private final ClassroomRepository classroomRepository;
    private final ClassStudentRepository classStudentRepository;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Lấy chi tiết lớp học với tính toán % tiến độ.
     * 
     * Công thức tính tiến độ:
     * - Đếm số học viên hoàn thành khóa học
     * - Chia cho tổng số học viên trong lớp
     * - Nhân 100 để được phần trăm
     */
    public ClassroomResponse getClassDetails(Long classId) {
        Optional<Classroom> classroom = classroomRepository.findById(classId);
        
        if (classroom.isEmpty()) {
            throw new RuntimeException("Classroom not found with id: " + classId);
        }

        Classroom cls = classroom.get();
        
        // Đếm tổng số học viên trong lớp
        int totalStudents = (int) classStudentRepository.countByClassroomId(classId);
        
        // Đếm số học viên hoàn thành (status = 'COMPLETED')
        int completedStudents = (int) classStudentRepository.countByClassroomIdAndStatus(classId, "COMPLETED");
        
        // Tính % tiến độ
        double progressPercentage = 0.0;
        if (totalStudents > 0) {
            progressPercentage = (double) completedStudents / totalStudents * 100;
            progressPercentage = Math.round(progressPercentage * 10.0) / 10.0; // Làm tròn 1 chữ số thập phân
        }

        return ClassroomResponse.builder()
                .id(cls.getId())
                .name(cls.getName())
                .teacherId(cls.getTeacher().getId())
                .teacherName(cls.getTeacher().getFullName())
                .startDate(cls.getStartDate())
                .endDate(cls.getEndDate())
                .status(cls.getStatus().toString())
                .totalStudents(totalStudents)
                .completedStudents(completedStudents)
                .progressPercentage(progressPercentage)
                .createdAt(cls.getCreatedAt() != null ? 
                        cls.getCreatedAt().format(DATE_FORMATTER) : null)
                .build();
    }

    /**
     * Cập nhật trạng thái hoàn thành cho học viên.
     */
    public void updateStudentCompletion(Long classId, Long studentId, boolean completed) {
        // Tìm ClassStudent record
        Optional<ClassStudent> classStudent = classStudentRepository.findByClassroomIdAndUserId(classId, studentId);
        
        if (classStudent.isPresent()) {
            ClassStudent cs = classStudent.get();
            cs.setStatus(completed ? "COMPLETED" : "ACTIVE");
            classStudentRepository.save(cs);
        } else {
            throw new RuntimeException("Student not found in this classroom");
        }
    }
}
