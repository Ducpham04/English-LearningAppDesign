package com.example.appenglish.repository;

import com.example.appenglish.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {
    @Query("SELECT s FROM Session s WHERE s.course.id = :courseId")
    List<Session> findByCourseId(Long courseId);
}
