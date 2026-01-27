package com.example.appenglish.repository;

import com.example.appenglish.entity.Curriculum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository cho Curriculum (Khóa học).
 */
@Repository
public interface CurriculumRepository extends JpaRepository<Curriculum, Long> {

    /**
     * Tìm tất cả khóa học của một giáo viên.
     */
    List<Curriculum> findByTeacherId(Long teacherId);

    /**
     * Tìm khóa học theo tiêu đề.
     */
    List<Curriculum> findByTitleContainingIgnoreCase(String title);

    /**
     * Tìm các khóa học miễn phí.
     */
    @Query("SELECT c FROM Curriculum c WHERE c.price = 0")
    List<Curriculum> findFreeCourses();

    /**
     * Tìm các khóa học có giá.
     */
    @Query("SELECT c FROM Curriculum c WHERE c.price > 0")
    List<Curriculum> findPaidCourses();

    /**
     * Tìm khóa học theo giáo viên và tiêu đề.
     */
    Optional<Curriculum> findByTeacherIdAndTitle(Long teacherId, String title);

    /**
     * Kiểm tra khóa học có tồn tại.
     */
    boolean existsByIdAndTeacherId(Long id, Long teacherId);
}
