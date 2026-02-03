package com.example.appenglish.repository;

import com.example.appenglish.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    @Query(value = "SELECT u.user_id, u.display_name, SUM(ds.xp_points) AS total_xp " +
            "FROM USERS u " +
            "JOIN DAILY_SESSIONS ds ON u.user_id = ds.user_id " +
            "WHERE ds.session_date >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) " +
            "GROUP BY u.user_id, u.display_name " +
            "ORDER BY total_xp DESC " +
            "LIMIT 10", nativeQuery = true)
    List<Object[]> findTopUsersByXpInLastWeek();

    @Query(value = "SELECT u.user_id, u.display_name, SUM(ds.xp_points) AS total_xp " +
            "FROM USERS u " +
            "JOIN DAILY_SESSIONS ds ON u.user_id = ds.user_id " +
            "WHERE ds.session_date = CURDATE() " + // Lấy dữ liệu ngày hôm nay
            "GROUP BY u.user_id, u.display_name " +
            "ORDER BY total_xp DESC " +
            "LIMIT 10", nativeQuery = true)
    List<Object[]> findTopUsersByXpToday();

    @Query(value = "SELECT u.user_id, u.display_name, SUM(ds.xp_points) AS total_xp " +
            "FROM USERS u " +
            "JOIN DAILY_SESSIONS ds ON u.user_id = ds.user_id " +
            "WHERE MONTH(ds.session_date) = MONTH(CURDATE()) " + // Lấy dữ liệu trong tháng hiện tại
            "GROUP BY u.user_id, u.display_name " +
            "ORDER BY total_xp DESC " +
            "LIMIT 10", nativeQuery = true)
    List<Object[]> findTopUsersByXpThisMonth();
}

