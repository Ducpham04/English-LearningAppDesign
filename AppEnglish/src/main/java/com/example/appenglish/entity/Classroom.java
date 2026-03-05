package com.example.AppEnglish.entity;

import com.example.AppEnglish.enums.ClassStatus;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "classrooms")
@Getter
@Setter
public class Classroom extends BaseEntity {
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id")
    private User teacher;

    private LocalDate startDate;
    private LocalDate endDate;

    @Column(name = "max_students")
    private Integer maxStudents;

    @Column(name = "level")
    private String level; // beginner, intermediate, advanced

    // Schedule as JSONB: {"daysOfWeek": [1,3,5], "startTime": "19:00", "endTime":
    // "20:30", "timezone": "Asia/Ho_Chi_Minh"}
    @Type(JsonType.class)
    @Column(name = "schedule", columnDefinition = "jsonb")
    private Map<String, Object> schedule;

    @Enumerated(EnumType.STRING)
    private ClassStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "curriculum_id")
    private Curriculum curriculum;

    @OneToMany(mappedBy = "classroom", cascade = CascadeType.ALL)
    private List<ClassStudent> students = new ArrayList<>();
}
