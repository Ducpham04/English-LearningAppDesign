package com.example.appenglish.repository;

import com.example.appenglish.entity.ClassJoinRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClassJoinRequestRepository extends JpaRepository<ClassJoinRequest, Long> {
    List<ClassJoinRequest> findByClassroom_IdAndStatusOrderByRequestedAtDesc(Long classroomId, String status);
    Optional<ClassJoinRequest> findByClassroom_IdAndStudent_Id(Long classroomId, String studentId);
    boolean existsByClassroom_IdAndStudent_IdAndStatus(Long classroomId, String studentId, String status);
    void deleteByClassroom_Id(Long classroomId);
}
