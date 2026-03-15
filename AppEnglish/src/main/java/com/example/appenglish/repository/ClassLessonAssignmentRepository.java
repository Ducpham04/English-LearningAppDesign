package com.example.appenglish.repository;

import com.example.appenglish.entity.ClassLessonAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClassLessonAssignmentRepository extends JpaRepository<ClassLessonAssignment, Long> {
    List<ClassLessonAssignment> findByClassroom_IdOrderByOrderIndexAscIdAsc(Long classId);
    boolean existsByClassroom_IdAndLesson_Id(Long classId, Long lessonId);
    void deleteByClassroom_IdAndId(Long classId, Long assignmentId);
    void deleteByClassroom_Id(Long classId);
}
