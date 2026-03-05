package com.example.AppEnglish.modules.course.dto;

import com.example.AppEnglish.entity.*;
import com.example.AppEnglish.enums.ClassStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClassDetailDTO {
        private Long id;
        private String name;
        private String description;
        private String teacherName;
        private Long teacherId;
        private String teacherAvatar;
        private String teacherBio;
        private int studentCount;
        private Integer maxStudents;
        private Map<String, Object> schedule;
        private String level;
        private CurriculumSummaryDTO curriculum;
        private LocalDate startDate;
        private LocalDate endDate;
        @JsonProperty("isActive")
        private boolean isActive;
        private boolean enrolled;
        private ProgressDTO progress;

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class CurriculumSummaryDTO {
                private Long id;
                private String name;
                private List<Long> sessions;

                public static CurriculumSummaryDTO fromEntity(Curriculum curriculum) {
                        List<Long> sessionIds = curriculum.getSessions().stream()
                                        .map(Session::getId)
                                        .collect(Collectors.toList());

                        return CurriculumSummaryDTO.builder()
                                        .id(curriculum.getId())
                                        .name(curriculum.getTitle())
                                        .sessions(sessionIds)
                                        .build();
                }
        }

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class ProgressDTO {
                private int completion;
                private int lessonsCompleted;
                private int totalLessons;

                public static ProgressDTO of(int completedLessons, int totalLessons) {
                        int completion = totalLessons > 0
                                        ? (int) Math.round((double) completedLessons / totalLessons * 100)
                                        : 0;
                        return ProgressDTO.builder()
                                        .completion(completion)
                                        .lessonsCompleted(completedLessons)
                                        .totalLessons(totalLessons)
                                        .build();
                }
        }

        public static ClassDetailDTO fromEntity(Classroom classroom, String teacherBio,
                        int studentCount, boolean enrolled,
                        int totalLessons, int completedLessons) {
                User teacher = classroom.getTeacher();
                Curriculum curriculum = classroom.getCurriculum();

                CurriculumSummaryDTO curriculumSummary = curriculum != null
                                ? CurriculumSummaryDTO.fromEntity(curriculum)
                                : null;

                return ClassDetailDTO.builder()
                                .id(classroom.getId())
                                .name(classroom.getName())
                                .description(classroom.getDescription())
                                .teacherName(teacher != null ? teacher.getFullName() : null)
                                .teacherId(teacher != null ? teacher.getId() : null)
                                .teacherAvatar(teacher != null ? teacher.getAvatarUrl() : null)
                                .teacherBio(teacherBio)
                                .studentCount(studentCount)
                                .maxStudents(classroom.getMaxStudents())
                                .schedule(classroom.getSchedule())
                                .level(classroom.getLevel())
                                .curriculum(curriculumSummary)
                                .startDate(classroom.getStartDate())
                                .endDate(classroom.getEndDate())
                                .isActive(classroom.getStatus() == ClassStatus.ACTIVE)
                                .enrolled(enrolled)
                                .progress(ProgressDTO.of(completedLessons, totalLessons))
                                .build();
        }
}
