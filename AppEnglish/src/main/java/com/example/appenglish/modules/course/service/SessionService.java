package com.example.appenglish.modules.course.service;

import com.example.appenglish.entity.Session;
import com.example.appenglish.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service xử lý logic cho Session (Chương học).
 */
@Service
@RequiredArgsConstructor
public class SessionService {

    private final SessionRepository sessionRepository;

    /**
     * Lấy danh sách chương học theo ID khóa học (curriculumId), sắp xếp theo orderIndex.
     * Sử dụng phương thức findByCourseIdOrderByOrderIndexAsc đã có trong Repository.
     */
    public List<Session> getSessionsByCurriculum(Long curriculumId) {
        return sessionRepository.findByCourseIdOrderByOrderIndexAsc(curriculumId);
    }
}