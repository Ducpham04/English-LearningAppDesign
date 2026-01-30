package com.example.appenglish.repository;

import com.example.appenglish.entity.FaceVerificationLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FaceIdRepository extends JpaRepository<FaceVerificationLog,Long> {
}
