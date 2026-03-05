package com.example.AppEnglish.repository;

import com.example.AppEnglish.entity.Session;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SessionRepository extends JpaRepository<Session, Long> {

    List<Session> findByCourse_IdOrderByOrderIndexAsc(Long courseId);

    @Query("""
                SELECT s FROM Session s
                WHERE (:level IS NULL OR s.level = :level)
                ORDER BY s.orderIndex ASC
            """)
    Page<Session> findAllFiltered(@Param("level") String level, Pageable pageable);
}
