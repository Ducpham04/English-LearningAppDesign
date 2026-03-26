package com.example.appenglish.modules.classroom.service;

import com.example.appenglish.entity.Classroom;
import com.example.appenglish.entity.Curriculum;
import com.example.appenglish.entity.User;
import com.example.appenglish.modules.classroom.dto.ClassroomUpsertRequest;
import com.example.appenglish.modules.teacher.dto.ClassDto;
import com.example.appenglish.repository.ClassLessonAssignmentRepository;
import com.example.appenglish.repository.ClassStudentRepository;
import com.example.appenglish.repository.ClassroomRepository;
import com.example.appenglish.repository.CurriculumRepository;
import com.example.appenglish.repository.UserRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClassroomService {

  private final ClassroomRepository classroomRepository;
  private final ClassStudentRepository classStudentRepository;
  private final ClassLessonAssignmentRepository assignmentRepository;
  private final UserRepository userRepository;
  private final CurriculumRepository curriculumRepository;

  @Transactional(readOnly = true)
  public List<ClassDto> getAll() {
    return classroomRepository.findAll().stream().map(this::toClassDto).collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public ClassDto getById(Long id) {
    Classroom c =
        classroomRepository.findById(id).orElseThrow(() -> new RuntimeException("Class not found: " + id));
    return toClassDto(c);
  }

  @Transactional(readOnly = true)
  public List<ClassDto> getByTeacher(String teacherId) {
    return classroomRepository.findByTeacher_Id(teacherId).stream()
        .map(this::toClassDto)
        .collect(Collectors.toList());
  }

  @Transactional
  public ClassDto create(ClassroomUpsertRequest req) {
    if (req.getName() == null || req.getName().isBlank()) {
      throw new RuntimeException("Class name is required");
    }

    Classroom c = new Classroom();
    apply(c, req);
    return toClassDto(classroomRepository.save(c));
  }

  @Transactional
  public ClassDto update(Long id, ClassroomUpsertRequest req) {
    Classroom c =
        classroomRepository.findById(id).orElseThrow(() -> new RuntimeException("Class not found: " + id));
    apply(c, req);
    return toClassDto(classroomRepository.save(c));
  }

  @Transactional
  public void delete(Long id) {
    if (!classroomRepository.existsById(id)) {
      throw new RuntimeException("Class not found: " + id);
    }
    // Clean up relations if needed (student/join requests/assignments)
    // Current admin screen only needs delete class; repositories may cascade in DB.
    classroomRepository.deleteById(id);
  }

  private void apply(Classroom c, ClassroomUpsertRequest req) {
    c.setName(req.getName() != null ? req.getName().trim() : c.getName());
    c.setStartDate(req.getStartDate());
    c.setEndDate(req.getEndDate());
    c.setStatus(req.getStatus());

    if (req.getTeacherId() != null && !req.getTeacherId().isBlank()) {
      User teacher =
          userRepository.findById(req.getTeacherId()).orElseThrow(() -> new RuntimeException("Teacher not found"));
      c.setTeacher(teacher);
    }
    if (req.getCourseId() != null) {
      Curriculum course =
          curriculumRepository
              .findById(req.getCourseId())
              .orElseThrow(() -> new RuntimeException("Course not found"));
      c.setCourse(course);
    }
  }

  private ClassDto toClassDto(Classroom c) {
    int studentCount = 0;
    int assignmentCount = 0;
    try {
      studentCount = classStudentRepository.findByClassroom_Id(c.getId()).size();
    } catch (Exception e) {
      log.warn("Failed to count students for classroomId={}", c.getId(), e);
    }
    try {
      assignmentCount = assignmentRepository.findByClassroom_IdOrderByOrderIndexAscIdAsc(c.getId()).size();
    } catch (Exception e) {
      log.warn("Failed to count assignments for classroomId={}", c.getId(), e);
    }
    return ClassDto.builder()
        .id(c.getId())
        .name(c.getName())
        .joinCode(c.getJoinCode())
        .teacherId(c.getTeacher() != null ? c.getTeacher().getId() : null)
        .teacherName(c.getTeacher() != null ? c.getTeacher().getFullName() : null)
        .courseId(c.getCourse() != null ? c.getCourse().getId() : null)
        .courseTitle(c.getCourse() != null ? c.getCourse().getTitle() : null)
        .startDate(c.getStartDate())
        .endDate(c.getEndDate())
        .status(c.getStatus())
        .studentCount(studentCount)
        .assignmentCount(assignmentCount)
        .build();
  }
}

