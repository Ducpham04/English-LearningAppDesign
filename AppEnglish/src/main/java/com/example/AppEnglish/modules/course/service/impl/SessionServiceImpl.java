package com.example.AppEnglish.modules.course.service.impl;

import com.example.AppEnglish.common.exception.ApiException;
import com.example.AppEnglish.entity.Session;
import com.example.AppEnglish.modules.course.dto.SessionListDTO;
import com.example.AppEnglish.modules.course.service.SessionService;
import com.example.AppEnglish.repository.CurriculumRepository;
import com.example.AppEnglish.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class SessionServiceImpl implements SessionService {

    private final SessionRepository sessionRepository;
    private final CurriculumRepository curriculumRepository;

    @Override
    public Page<SessionListDTO> getSessionList(String level, String status, Pageable pageable) {
        Page<Session> sessionPage = sessionRepository.findAllFiltered(level, pageable);
        Page<SessionListDTO> dtoPage = sessionPage.map(SessionListDTO::fromEntity);

        if (status != null && !status.isBlank()) {
            List<SessionListDTO> filtered = dtoPage.getContent().stream()
                    .filter(dto -> status.equalsIgnoreCase(dto.getStatus()))
                    .collect(Collectors.toList());
            return new PageImpl<>(filtered, pageable, filtered.size());
        }

        return dtoPage;
    }

    @Override
    public List<SessionListDTO> getSessionsByCurriculum(Long courseId) {
        if (!curriculumRepository.existsById(courseId)) {
            throw new ApiException("Curriculum not found with id: " + courseId, "CURRICULUM_NOT_FOUND");
        }

        return sessionRepository.findByCourse_IdOrderByOrderIndexAsc(courseId).stream()
                .map(SessionListDTO::fromEntity)
                .collect(Collectors.toList());
    }
}
