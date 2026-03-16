package com.example.appenglish.modules.teacher.service;

import com.example.appenglish.entity.*;
import com.example.appenglish.enums.ClassStatus;
import com.example.appenglish.modules.curriculum.dto.CurriculumDto;
import com.example.appenglish.modules.curriculum.service.CurriculumService;
import com.example.appenglish.modules.lesson.dto.LessonDto;
import com.example.appenglish.modules.lesson.service.LessonService;
import com.example.appenglish.modules.session.dto.SessionDto;
import com.example.appenglish.modules.session.service.SessionService;
import com.example.appenglish.modules.teacher.dto.*;
import com.example.appenglish.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeacherClassService {

    private final ClassroomRepository classroomRepository;
    private final ClassStudentRepository classStudentRepository;
    private final ClassJoinRequestRepository classJoinRequestRepository;
    private final ClassLessonAssignmentRepository assignmentRepository;
    private final UserLessonProgressRepository userLessonProgressRepository;
    private final UserRepository userRepository;
    private final CurriculumRepository curriculumRepository;
    private final LessonRepository lessonRepository;
    private final CurriculumService curriculumService;
    private final SessionService sessionService;
    private final LessonService lessonService;

    public User getCurrentTeacher() {
        org.springframework.security.core.Authentication auth =
                org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));
        if (user.getRole() == null || !user.getRole().name().equals("TEACHER")) {
            throw new RuntimeException("Not authorized as teacher");
        }
        return user;
    }

    public List<ClassDto> getMyClasses() {
        User teacher = getCurrentTeacher();
        return classroomRepository.findByTeacher_Id(teacher.getId()).stream()
                .map(this::toClassDto)
                .collect(Collectors.toList());
    }

    public ClassDto createClass(ClassCreateRequest req) {
        User teacher = getCurrentTeacher();
        if (req.getName() == null || req.getName().isBlank()) {
            throw new RuntimeException("Class name is required");
        }
        if (classroomRepository.existsByNameAndTeacher_Id(req.getName().trim(), teacher.getId())) {
            throw new RuntimeException("A class with this name already exists");
        }
        Classroom c = new Classroom();
        c.setName(req.getName().trim());
        c.setJoinCode(generateUniqueJoinCode());
        c.setTeacher(teacher);
        c.setStartDate(req.getStartDate());
        c.setEndDate(req.getEndDate());
        c.setStatus(req.getStatus() != null ? req.getStatus() : ClassStatus.UPCOMING);
        if (req.getCourseId() != null) {
            c.setCourse(curriculumRepository.findById(req.getCourseId()).orElse(null));
        }
        return toClassDto(classroomRepository.save(c));
    }

    public ClassDetailDto getClassDetail(Long classId) {
        User teacher = getCurrentTeacher();
        Classroom c = classroomRepository.findById(classId)
                .orElseThrow(() -> new RuntimeException("Class not found: " + classId));
        if (!c.getTeacher().getId().equals(teacher.getId())) {
            throw new RuntimeException("Not authorized to view this class");
        }
        if (c.getJoinCode() == null || c.getJoinCode().isBlank()) {
            c.setJoinCode(generateUniqueJoinCode());
            classroomRepository.save(c);
        }
        ClassDto classDto = toClassDto(c);
        CurriculumDto course = c.getCourse() != null ? curriculumService.getById(c.getCourse().getId()) : null;
        List<SessionDto> sessions = course != null && c.getCourse() != null
                ? sessionService.getByCourse(c.getCourse().getId())
                : new ArrayList<>();
        List<LessonDto> lessons = course != null && c.getCourse() != null
                ? lessonService.getByCourse(c.getCourse().getId())
                : new ArrayList<>();
        List<ClassStudentDto> students = classStudentRepository.findByClassroom_Id(classId).stream()
                .map(this::toStudentDto)
                .collect(Collectors.toList());
        List<AssignmentDto> assignments = assignmentRepository.findByClassroom_IdOrderByOrderIndexAscIdAsc(classId).stream()
                .map(a -> toAssignmentDto(a, true))
                .collect(Collectors.toList());
        return ClassDetailDto.builder()
                .classInfo(classDto)
                .course(course)
                .sessions(sessions)
                .lessons(lessons)
                .students(students)
                .assignments(assignments)
                .build();
    }

    public List<ClassStudentDto> getClassStudents(Long classId) {
        ensureTeacherOwnsClass(classId);
        return classStudentRepository.findByClassroom_Id(classId).stream()
                .map(this::toStudentDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ClassStudentDto addStudent(Long classId, String studentId) {
        ensureTeacherOwnsClass(classId);
        if (studentId == null || studentId.isBlank()) {
            throw new RuntimeException("Student ID is required");
        }
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("User not found: " + studentId));
        if (classStudentRepository.existsByClassroom_IdAndStudent_Id(classId, studentId)) {
            throw new RuntimeException("Student already in class");
        }
        Classroom classroom = classroomRepository.findById(classId)
                .orElseThrow(() -> new RuntimeException("Class not found: " + classId));
        ClassStudent cs = new ClassStudent();
        cs.setId(new ClassStudentId());
        cs.getId().setClassId(classId);
        cs.getId().setStudentId(studentId);
        cs.setClassroom(classroom);
        cs.setStudent(student);
        cs.setJoinedAt(LocalDateTime.now());
        cs.setIsFaceVerified(false);
        classStudentRepository.save(cs);
        return toStudentDto(cs);
    }

    @Transactional
    public void removeStudent(Long classId, String studentId) {
        ensureTeacherOwnsClass(classId);
        classStudentRepository.deleteByClassroom_IdAndStudent_Id(classId, studentId);
    }

    public List<ClassJoinRequestDto> getJoinRequests(Long classId) {
        ensureTeacherOwnsClass(classId);
        return classJoinRequestRepository.findByClassroom_IdAndStatusOrderByRequestedAtDesc(classId, "PENDING").stream()
                .map(this::toJoinRequestDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void approveJoinRequest(Long classId, Long requestId) {
        ensureTeacherOwnsClass(classId);
        ClassJoinRequest req = classJoinRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Join request not found: " + requestId));
        if (!req.getClassroom().getId().equals(classId)) {
            throw new RuntimeException("Request does not belong to this class");
        }
        if (!"PENDING".equals(req.getStatus())) {
            throw new RuntimeException("Request already processed");
        }
        User teacher = getCurrentTeacher();
        String studentId = req.getStudent().getId();
        if (classStudentRepository.existsByClassroom_IdAndStudent_Id(classId, studentId)) {
            req.setStatus("APPROVED");
            req.setRespondedAt(LocalDateTime.now());
            req.setRespondedBy(teacher);
            classJoinRequestRepository.save(req);
            return;
        }
        ClassStudent cs = new ClassStudent();
        cs.setId(new ClassStudentId());
        cs.getId().setClassId(classId);
        cs.getId().setStudentId(studentId);
        cs.setClassroom(req.getClassroom());
        cs.setStudent(req.getStudent());
        cs.setJoinedAt(LocalDateTime.now());
        cs.setIsFaceVerified(false);
        classStudentRepository.save(cs);
        req.setStatus("APPROVED");
        req.setRespondedAt(LocalDateTime.now());
        req.setRespondedBy(teacher);
        classJoinRequestRepository.save(req);
    }

    @Transactional
    public void rejectJoinRequest(Long classId, Long requestId) {
        ensureTeacherOwnsClass(classId);
        ClassJoinRequest req = classJoinRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Join request not found: " + requestId));
        if (!req.getClassroom().getId().equals(classId)) {
            throw new RuntimeException("Request does not belong to this class");
        }
        if (!"PENDING".equals(req.getStatus())) {
            throw new RuntimeException("Request already processed");
        }
        User teacher = getCurrentTeacher();
        req.setStatus("REJECTED");
        req.setRespondedAt(LocalDateTime.now());
        req.setRespondedBy(teacher);
        classJoinRequestRepository.save(req);
    }

    /** Xóa lớp học: xóa toàn bộ sinh viên và bài tập trong lớp rồi xóa lớp. */
    @Transactional
    public void deleteClass(Long classId) {
        ensureTeacherOwnsClass(classId);
        classJoinRequestRepository.deleteByClassroom_Id(classId);
        classStudentRepository.deleteByClassroom_Id(classId);
        assignmentRepository.deleteByClassroom_Id(classId);
        classroomRepository.deleteById(classId);
    }

    public List<AssignmentDto> getAssignments(Long classId) {
        ensureTeacherOwnsClass(classId);
        return assignmentRepository.findByClassroom_IdOrderByOrderIndexAscIdAsc(classId).stream()
                .map(a -> toAssignmentDto(a, false))
                .collect(Collectors.toList());
    }

    @Transactional
    public AssignmentDto createAssignment(Long classId, AssignmentCreateRequest req) {
        ensureTeacherOwnsClass(classId);
        if (req.getLessonId() == null) {
            throw new RuntimeException("Lesson ID is required");
        }
        Classroom classroom = classroomRepository.findById(classId)
                .orElseThrow(() -> new RuntimeException("Class not found: " + classId));
        if (assignmentRepository.existsByClassroom_IdAndLesson_Id(classId, req.getLessonId())) {
            throw new RuntimeException("This lesson is already assigned in this class");
        }
        Lesson lesson = lessonRepository.findById(req.getLessonId())
                .orElseThrow(() -> new RuntimeException("Lesson not found: " + req.getLessonId()));
        ClassLessonAssignment a = new ClassLessonAssignment();
        a.setClassroom(classroom);
        a.setLesson(lesson);
        a.setTitle(req.getTitle() != null ? req.getTitle() : lesson.getTitle());
        a.setDescription(req.getDescription());
        a.setDueAt(req.getDueAt());
        a.setMaxScore(req.getMaxScore());
        a.setIsRequired(req.getIsRequired() != null ? req.getIsRequired() : false);
        a.setOrderIndex(req.getOrderIndex() != null ? req.getOrderIndex() : 0);
        return toAssignmentDto(assignmentRepository.save(a), true);
    }

    @Transactional
    public AssignmentDto updateAssignment(Long classId, Long assignmentId, AssignmentUpdateRequest req) {
        ensureTeacherOwnsClass(classId);
        ClassLessonAssignment a = assignmentRepository.findById(assignmentId)
                .filter(x -> x.getClassroom().getId().equals(classId))
                .orElseThrow(() -> new RuntimeException("Assignment not found: " + assignmentId));
        if (req.getTitle() != null) a.setTitle(req.getTitle());
        if (req.getDescription() != null) a.setDescription(req.getDescription());
        if (req.getDueAt() != null) a.setDueAt(req.getDueAt());
        if (req.getMaxScore() != null) a.setMaxScore(req.getMaxScore());
        if (req.getIsRequired() != null) a.setIsRequired(req.getIsRequired());
        if (req.getOrderIndex() != null) a.setOrderIndex(req.getOrderIndex());
        return toAssignmentDto(assignmentRepository.save(a), true);
    }

    @Transactional
    public void deleteAssignment(Long classId, Long assignmentId) {
        ensureTeacherOwnsClass(classId);
        if (!assignmentRepository.findById(assignmentId)
                .filter(x -> x.getClassroom().getId().equals(classId))
                .isPresent()) {
            throw new RuntimeException("Assignment not found: " + assignmentId);
        }
        assignmentRepository.deleteById(assignmentId);
    }

    public List<AssignmentStatsDto> getAssignmentStats(Long classId) {
        ensureTeacherOwnsClass(classId);
        List<ClassStudent> classStudents = classStudentRepository.findByClassroom_Id(classId);
        Set<String> studentIds = classStudents.stream().map(cs -> cs.getStudent().getId()).collect(Collectors.toSet());
        List<ClassLessonAssignment> assignments = assignmentRepository.findByClassroom_IdOrderByOrderIndexAscIdAsc(classId);
        List<AssignmentStatsDto> result = new ArrayList<>();
        for (ClassLessonAssignment a : assignments) {
            List<UserLessonProgress> progressList = userLessonProgressRepository.findByLesson_Id(a.getLesson().getId());
            List<AssignmentStatsDto.StudentProgressItemDto> students = new ArrayList<>();
            for (ClassStudent cs : classStudents) {
                String sid = cs.getStudent().getId();
                UserLessonProgress prog = progressList.stream()
                        .filter(p -> p.getUser().getId().equals(sid))
                        .findFirst()
                        .orElse(null);
                students.add(AssignmentStatsDto.StudentProgressItemDto.builder()
                        .studentId(sid)
                        .studentName(cs.getStudent().getFullName())
                        .email(cs.getStudent().getEmail())
                        .completed(prog != null)
                        .score(prog != null ? prog.getScore() : null)
                        .completedAt(prog != null ? prog.getCompletedAt() : null)
                        .build());
            }
            long completedCount = students.stream().filter(s -> Boolean.TRUE.equals(s.getCompleted())).count();
            result.add(AssignmentStatsDto.builder()
                    .assignmentId(a.getId())
                    .title(a.getTitle())
                    .lessonId(a.getLesson().getId())
                    .dueAt(a.getDueAt())
                    .totalStudents(classStudents.size())
                    .completedCount((int) completedCount)
                    .students(students)
                    .build());
        }
        return result;
    }

    private static final String JOIN_CODE_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final SecureRandom RAND = new SecureRandom();

    private String generateUniqueJoinCode() {
        for (int i = 0; i < 50; i++) {
            StringBuilder sb = new StringBuilder(6);
            for (int j = 0; j < 6; j++) {
                sb.append(JOIN_CODE_CHARS.charAt(RAND.nextInt(JOIN_CODE_CHARS.length())));
            }
            String code = sb.toString();
            if (!classroomRepository.existsByJoinCode(code)) return code;
        }
        throw new RuntimeException("Unable to generate unique join code");
    }

    private void ensureTeacherOwnsClass(Long classId) {
        User teacher = getCurrentTeacher();
        Classroom c = classroomRepository.findById(classId)
                .orElseThrow(() -> new RuntimeException("Class not found: " + classId));
        if (!c.getTeacher().getId().equals(teacher.getId())) {
            throw new RuntimeException("Not authorized to manage this class");
        }
    }

    private ClassDto toClassDto(Classroom c) {
        int studentCount = classStudentRepository.findByClassroom_Id(c.getId()).size();
        int assignmentCount = assignmentRepository.findByClassroom_IdOrderByOrderIndexAscIdAsc(c.getId()).size();
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

    private ClassStudentDto toStudentDto(ClassStudent cs) {
        User u = cs.getStudent();
        return ClassStudentDto.builder()
                .studentId(u.getId())
                .fullName(u.getFullName())
                .email(u.getEmail())
                .avatarUrl(u.getAvatarUrl())
                .joinedAt(cs.getJoinedAt())
                .isFaceVerified(cs.getIsFaceVerified() != null ? cs.getIsFaceVerified() : false)
                .build();
    }

    private ClassJoinRequestDto toJoinRequestDto(ClassJoinRequest r) {
        User u = r.getStudent();
        return ClassJoinRequestDto.builder()
                .id(r.getId())
                .studentId(u.getId())
                .studentName(u.getFullName())
                .studentEmail(u.getEmail())
                .status(r.getStatus())
                .requestedAt(r.getRequestedAt())
                .respondedAt(r.getRespondedAt())
                .build();
    }

    private AssignmentDto toAssignmentDto(ClassLessonAssignment a, boolean includeLesson) {
        LessonDto lessonDto = includeLesson ? lessonService.getById(a.getLesson().getId()) : null;
        return AssignmentDto.builder()
                .id(a.getId())
                .classId(a.getClassroom().getId())
                .lessonId(a.getLesson().getId())
                .title(a.getTitle())
                .description(a.getDescription())
                .dueAt(a.getDueAt())
                .maxScore(a.getMaxScore())
                .isRequired(a.getIsRequired() != null ? a.getIsRequired() : false)
                .orderIndex(a.getOrderIndex())
                .lesson(lessonDto)
                .build();
    }
}
