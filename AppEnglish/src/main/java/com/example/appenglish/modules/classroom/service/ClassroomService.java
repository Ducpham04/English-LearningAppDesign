package com.example.appenglish.modules.classroom.service;

import com.example.appenglish.entity.Classroom;
import com.example.appenglish.entity.ClassStudent;
import com.example.appenglish.modules.classroom.dto.ClassroomResponse;
import com.example.appenglish.repository.ClassroomRepository;
import com.example.appenglish.repository.ClassStudentRepository;
import com.example.appenglish.repository.LessonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClassroomService {

    private final ClassroomRepository classroomRepository;
    private final ClassStudentRepository classStudentRepository;
    private final LessonRepository lessonRepository;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public ClassroomResponse getClassDetails(Long classId) {
        Classroom cls = classroomRepository.findById(classId)
                .orElseThrow(() -> new RuntimeException("Classroom not found with id: " + classId));

        int totalStudents = (int) classStudentRepository.countByClassroomId(classId);
        int completedStudents = (int) classStudentRepository.countByClassroomIdAndStatus(classId, "COMPLETED");

        double progressPercentage = 0.0;
        if (totalStudents > 0) {
            progressPercentage = (double) completedStudents / totalStudents * 100;
            progressPercentage = Math.round(progressPercentage * 10.0) / 10.0;
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
                .createdAt(cls.getCreatedAt() != null ? cls.getCreatedAt().format(DATE_FORMATTER) : null)
                .build();
    }

    public double calculateLearningProgress(Long userId, Long classroomId) {
        long totalLessons = lessonRepository.countByClassroomId(classroomId);
        if (totalLessons == 0) return 0.0;

        long completedLessons = lessonRepository.countCompletedByUserIdAndClassroomId(userId, classroomId);

        double progress = (double) completedLessons / totalLessons * 100;
        return Math.round(progress * 100.0) / 100.0;
    }

    @Transactional
    public void updateStudentCompletion(Long classId, Long studentId, boolean completed) {
        ClassStudent cs = classStudentRepository.findByClassroomIdAndUserId(classId, studentId)
                .orElseThrow(() -> new RuntimeException("Student not found in this classroom"));

        cs.setStatus(completed ? "COMPLETED" : "ACTIVE");
        classStudentRepository.save(cs);
    }
}