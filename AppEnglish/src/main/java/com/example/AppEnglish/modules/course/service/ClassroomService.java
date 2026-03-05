package com.example.AppEnglish.modules.course.service;

import com.example.AppEnglish.modules.course.dto.ClassDetailDTO;

public interface ClassroomService {

    ClassDetailDTO getClassDetails(Long classId, Long currentUserId);
}
