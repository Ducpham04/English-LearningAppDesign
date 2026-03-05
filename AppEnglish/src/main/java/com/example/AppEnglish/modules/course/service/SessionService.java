package com.example.AppEnglish.modules.course.service;

import com.example.AppEnglish.modules.course.dto.SessionListDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SessionService {

    Page<SessionListDTO> getSessionList(String level, String status, Pageable pageable);

    List<SessionListDTO> getSessionsByCurriculum(Long courseId);
}
