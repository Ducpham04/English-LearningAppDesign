package com.example.appenglish.modules.classroom.service;

import com.example.appenglish.enums.ClassStatus;
import com.example.appenglish.entity.Classroom;
import com.example.appenglish.entity.User;
import com.example.appenglish.modules.classroom.dto.ClassroomDto;
import com.example.appenglish.modules.classroom.dto.ClassroomRequest;
import com.example.appenglish.repository.ClassroomRepository;
import com.example.appenglish.repository.UserRepository;
import com.example.appenglish.repository.ClassStudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClassroomService {

    private final ClassroomRepository classroomRepository;
    private final UserRepository userRepository;
    private final ClassStudentRepository classStudentRepository;

    @Transactional(readOnly = true)
    public List<ClassroomDto> getAllClassrooms() {
        return classroomRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ClassroomDto getClassroomById(Long id) {
        Classroom classroom = classroomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Classroom not found with id: " + id));
        return mapToDto(classroom);
    }

    @Transactional(readOnly = true)
    public List<ClassroomDto> getClassroomsByTeacher(String teacherId) {
        return classroomRepository.findByTeacherId(teacherId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ClassroomDto> getClassroomsByStatus(ClassStatus status) {
        return classroomRepository.findByStatus(status).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ClassroomDto createClassroom(ClassroomRequest request) {
        User teacher = null;
        if (request.getTeacherId() != null) {
            teacher = userRepository.findById(String.valueOf(request.getTeacherId()))
                    .orElseThrow(() -> new RuntimeException("Teacher not found with id: " + request.getTeacherId()));
        }

        Classroom classroom = Classroom.builder()
                .name(request.getName())
                .teacher(teacher)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .status(request.getStatus() != null ? request.getStatus() : ClassStatus.UPCOMING)
                .build();

        Classroom saved = classroomRepository.save(classroom);
        return mapToDto(saved);
    }

    @Transactional
    public ClassroomDto updateClassroom(Long id, ClassroomRequest request) {
        Classroom classroom = classroomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Classroom not found with id: " + id));

        if (request.getName() != null) {
            classroom.setName(request.getName());
        }
        if (request.getTeacherId() != null) {
            User teacher = userRepository.findById(String.valueOf(request.getTeacherId()))
                    .orElseThrow(() -> new RuntimeException("Teacher not found with id: " + request.getTeacherId()));
            classroom.setTeacher(teacher);
        }
        if (request.getStartDate() != null) {
            classroom.setStartDate(request.getStartDate());
        }
        if (request.getEndDate() != null) {
            classroom.setEndDate(request.getEndDate());
        }
        if (request.getStatus() != null) {
            classroom.setStatus(request.getStatus());
        }

        Classroom saved = classroomRepository.save(classroom);
        return mapToDto(saved);
    }

    @Transactional
    public void deleteClassroom(Long id) {
        if (!classroomRepository.existsById(id)) {
            throw new RuntimeException("Classroom not found with id: " + id);
        }
        classroomRepository.deleteById(id);
    }

    private ClassroomDto mapToDto(Classroom classroom) {
        int studentCount = classStudentRepository.findByClassroomId(classroom.getId()).size();
        return ClassroomDto.builder()
                .id(classroom.getId())
                .name(classroom.getName())
                .teacherId(classroom.getTeacher() != null ? Long.parseLong(classroom.getTeacher().getId()) : null)
                .teacherName(classroom.getTeacher() != null ? classroom.getTeacher().getFullName() : null)
                .startDate(classroom.getStartDate())
                .endDate(classroom.getEndDate())
                .status(classroom.getStatus())
                .studentCount(studentCount)
                .build();
    }
}
