package com.example.AppEnglish.modules.course.service.impl;

import com.example.AppEnglish.common.exception.ApiException;
import com.example.AppEnglish.entity.*;
import com.example.AppEnglish.modules.course.dto.ClassDetailDTO;
import com.example.AppEnglish.modules.course.service.ClassroomService;
import com.example.AppEnglish.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ClassroomServiceImpl implements ClassroomService {

        private final ClassroomRepository classroomRepository;
        private final ClassStudentRepository classStudentRepository;
        private final LessonRepository lessonRepository;
        private final UserProfileRepository userProfileRepository;

        @Override
        public ClassDetailDTO getClassDetails(Long classId, Long currentUserId) {
                Classroom classroom = classroomRepository.findByIdWithDetails(classId)
                                .orElseThrow(() -> new ApiException("Classroom not found with id: " + classId,
                                                "CLASS_NOT_FOUND"));

                User teacher = classroom.getTeacher();

                String teacherBio = null;
                if (teacher != null) {
                        teacherBio = userProfileRepository.findByUser_Id(teacher.getId())
                                        .map(UserProfile::getBio)
                                        .orElse(null);
                }

                int studentCount = (int) classStudentRepository.countByClassroom_Id(classId);

                boolean enrolled = currentUserId != null &&
                                classStudentRepository.existsByClassroom_IdAndStudent_Id(classId, currentUserId);

                Curriculum curriculum = classroom.getCurriculum();
                int totalLessons = curriculum != null
                                ? (int) lessonRepository.countBySession_Course_Id(curriculum.getId())
                                : 0;
                int completedLessons = 0;

                return ClassDetailDTO.fromEntity(classroom, teacherBio, studentCount, enrolled, totalLessons,
                                completedLessons);
        }
}
