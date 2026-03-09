package com.example.appenglish.modules.curriculum.service;

import com.example.appenglish.entity.Curriculum;
import com.example.appenglish.entity.User;
import com.example.appenglish.modules.curriculum.dto.CurriculumCreateRequest;
import com.example.appenglish.modules.curriculum.dto.CurriculumDto;
import com.example.appenglish.repository.CurriculumRepository;
import com.example.appenglish.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CurriculumService {

    private final CurriculumRepository curriculumRepository;
    private final UserRepository userRepository;

    public List<CurriculumDto> getAll() {
        return curriculumRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public CurriculumDto getById(Long id) {
        return curriculumRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new RuntimeException("Curriculum not found: " + id));
    }

    public List<CurriculumDto> getByTeacher(String teacherId) {
        return curriculumRepository.findByTeacherId(teacherId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public CurriculumDto create(CurriculumCreateRequest req) {
        Curriculum c = new Curriculum();
        c.setTitle(req.getTitle());
        c.setDescription(req.getDescription());
        c.setThumbnailUrl(req.getThumbnailUrl());
        c.setPrice(req.getPrice() != null ? req.getPrice() : 0);
        if (req.getTeacherId() != null && !req.getTeacherId().isBlank()) {
            userRepository.findById(req.getTeacherId()).ifPresent(c::setTeacher);
        }
        return toDto(curriculumRepository.save(c));
    }

    public void delete(Long id) {
        curriculumRepository.deleteById(id);
    }

    private CurriculumDto toDto(Curriculum c) {
        return CurriculumDto.builder()
                .id(c.getId())
                .title(c.getTitle())
                .description(c.getDescription())
                .thumbnailUrl(c.getThumbnailUrl())
                .price(c.getPrice())
                .teacherId(c.getTeacher() != null ? c.getTeacher().getId() : null)
                .teacherName(c.getTeacher() != null ? c.getTeacher().getFullName() : null)
                .build();
    }
}
