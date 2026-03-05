package com.example.AppEnglish.repository;

import com.example.AppEnglish.entity.Classroom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ClassroomRepository extends JpaRepository<Classroom, Long> {

    @Query("SELECT c FROM Classroom c LEFT JOIN FETCH c.teacher LEFT JOIN FETCH c.curriculum WHERE c.id = :id")
    Optional<Classroom> findByIdWithDetails(@Param("id") Long id);
}
