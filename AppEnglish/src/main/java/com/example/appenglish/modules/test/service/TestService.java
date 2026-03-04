package com.example.appenglish.modules.test.service;

import com.example.appenglish.entity.Curriculum;
import com.example.appenglish.entity.Lesson;
import com.example.appenglish.entity.Test;
import com.example.appenglish.modules.test.dto.TestDto;
import com.example.appenglish.modules.test.dto.TestRequest;
import com.example.appenglish.repository.CurriculumRepository;
import com.example.appenglish.repository.LessonRepository;
import com.example.appenglish.repository.TestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TestService {

    private final TestRepository testRepository;
    private final CurriculumRepository curriculumRepository;
    private final LessonRepository lessonRepository;

    @Transactional(readOnly = true)
    public List<TestDto> getAllTests() {
        return testRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TestDto getTestById(Long id) {
        Test test = testRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Test not found with id: " + id));
        return mapToDto(test);
    }

    @Transactional(readOnly = true)
    public List<TestDto> getTestsByCourse(Long courseId) {
        return testRepository.findByCourseId(courseId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TestDto> getTestsByLesson(Long lessonId) {
        return testRepository.findByLessonId(lessonId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public TestDto createTest(TestRequest request) {
        Curriculum course = null;
        Lesson lesson = null;

        if (request.getCourseId() != null) {
            course = curriculumRepository.findById(request.getCourseId())
                    .orElseThrow(() -> new RuntimeException("Course not found with id: " + request.getCourseId()));
        }

        if (request.getLessonId() != null) {
            lesson = lessonRepository.findById(request.getLessonId())
                    .orElseThrow(() -> new RuntimeException("Lesson not found with id: " + request.getLessonId()));
        }

        Test test = Test.builder()
                .title(request.getTitle())
                .duration(request.getDuration())
                .passScore(request.getPassScore())
                .isFaceRequired(request.getIsFaceRequired() != null ? request.getIsFaceRequired() : false)
                .course(course)
                .lesson(lesson)
                .build();

        Test saved = testRepository.save(test);
        return mapToDto(saved);
    }

    @Transactional
    public TestDto updateTest(Long id, TestRequest request) {
        Test test = testRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Test not found with id: " + id));

        if (request.getTitle() != null) {
            test.setTitle(request.getTitle());
        }
        if (request.getDuration() != null) {
            test.setDuration(request.getDuration());
        }
        if (request.getPassScore() != null) {
            test.setPassScore(request.getPassScore());
        }
        if (request.getIsFaceRequired() != null) {
            test.setIsFaceRequired(request.getIsFaceRequired());
        }
        if (request.getCourseId() != null) {
            Curriculum course = curriculumRepository.findById(request.getCourseId())
                    .orElseThrow(() -> new RuntimeException("Course not found with id: " + request.getCourseId()));
            test.setCourse(course);
        }
        if (request.getLessonId() != null) {
            Lesson lesson = lessonRepository.findById(request.getLessonId())
                    .orElseThrow(() -> new RuntimeException("Lesson not found with id: " + request.getLessonId()));
            test.setLesson(lesson);
        }

        Test saved = testRepository.save(test);
        return mapToDto(saved);
    }

    @Transactional
    public void deleteTest(Long id) {
        if (!testRepository.existsById(id)) {
            throw new RuntimeException("Test not found with id: " + id);
        }
        testRepository.deleteById(id);
    }

    private TestDto mapToDto(Test test) {
        return TestDto.builder()
                .id(test.getId())
                .title(test.getTitle())
                .duration(test.getDuration())
                .passScore(test.getPassScore())
                .isFaceRequired(test.getIsFaceRequired())
                .courseId(test.getCourse() != null ? test.getCourse().getId() : null)
                .courseName(test.getCourse() != null ? test.getCourse().getTitle() : null)
                .lessonId(test.getLesson() != null ? test.getLesson().getId() : null)
                .lessonName(test.getLesson() != null ? test.getLesson().getTitle() : null)
                .build();
    }
}
