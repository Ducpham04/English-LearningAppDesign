package com.example.appenglish.modules.curriculum.service;

import com.example.appenglish.entity.Curriculum;
import com.example.appenglish.entity.User;
import com.example.appenglish.modules.curriculum.dto.CurriculumDto;
import com.example.appenglish.modules.curriculum.dto.CurriculumRequest;
import com.example.appenglish.repository.CurriculumRepository;
import com.example.appenglish.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CurriculumService {

    private final CurriculumRepository curriculumRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<CurriculumDto> getAllCurriculums() {
        return curriculumRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CurriculumDto getCurriculumById(Long id) {
        Curriculum curriculum = curriculumRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Curriculum not found with id: " + id));
        return mapToDto(curriculum);
    }

    @Transactional(readOnly = true)
    public List<CurriculumDto> getCurriculumsByTeacher(String teacherId) {
        return curriculumRepository.findByTeacherId(teacherId != null ? teacherId : null).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public CurriculumDto createCurriculum(CurriculumRequest request) {
        User teacher = null;
        if (request.getTeacherId() != null) {
            teacher = userRepository.findById(String.valueOf(request.getTeacherId()))
                    .orElseThrow(() -> new RuntimeException("Teacher not found with id: " + request.getTeacherId()));
        }

        Curriculum curriculum = Curriculum.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .thumbnailUrl(request.getThumbnailUrl())
                .price(request.getPrice() != null ? request.getPrice() : 0)
                .teacher(teacher)
                .build();

        Curriculum saved = curriculumRepository.save(curriculum);
        return mapToDto(saved);
    }

    @Transactional
    public CurriculumDto updateCurriculum(Long id, CurriculumRequest request) {
        Curriculum curriculum = curriculumRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Curriculum not found with id: " + id));

        if (request.getTitle() != null) {
            curriculum.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            curriculum.setDescription(request.getDescription());
        }
        if (request.getThumbnailUrl() != null) {
            curriculum.setThumbnailUrl(request.getThumbnailUrl());
        }
        if (request.getPrice() != null) {
            curriculum.setPrice(request.getPrice());
        }
        if (request.getTeacherId() != null) {
            User teacher = userRepository.findById(String.valueOf(request.getTeacherId()))
                    .orElseThrow(() -> new RuntimeException("Teacher not found with id: " + request.getTeacherId()));
            curriculum.setTeacher(teacher);
        }

        Curriculum saved = curriculumRepository.save(curriculum);
        return mapToDto(saved);
    }

    @Transactional
    public void deleteCurriculum(Long id) {
        if (!curriculumRepository.existsById(id)) {
            throw new RuntimeException("Curriculum not found with id: " + id);
        }
        curriculumRepository.deleteById(id);
    }

    private CurriculumDto mapToDto(Curriculum curriculum) {
        return CurriculumDto.builder()
                .id(curriculum.getId())
                .title(curriculum.getTitle())
                .description(curriculum.getDescription())
                .thumbnailUrl(curriculum.getThumbnailUrl())
                .price(curriculum.getPrice())
                .teacherId(curriculum.getTeacher() != null ? Long.parseLong(curriculum.getTeacher().getId()) : null)
                .teacherName(curriculum.getTeacher() != null ? curriculum.getTeacher().getFullName() : null)
                .build();
    }
}
