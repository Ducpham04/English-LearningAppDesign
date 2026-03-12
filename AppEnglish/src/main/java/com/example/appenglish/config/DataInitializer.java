package com.example.appenglish.config;

import com.example.appenglish.entity.Lesson;
import com.example.appenglish.entity.Session;
import com.example.appenglish.entity.Topic;
import com.example.appenglish.entity.User;
import com.example.appenglish.enums.LessonType;
import com.example.appenglish.enums.UserRole;
import com.example.appenglish.repository.LessonRepository;
import com.example.appenglish.repository.SessionRepository;
import com.example.appenglish.repository.TopicRepository;
import com.example.appenglish.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Chạy tự động sau khi Spring Boot khởi động:
 * - Fix role = NULL cho các user cũ.
 * - Tạo admin mặc định nếu chưa có.
 * - Seed dữ liệu mẫu Topics + Sessions nếu chưa có.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final TopicRepository topicRepository;
    private final SessionRepository sessionRepository;
    private final LessonRepository lessonRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        fixNullRoles();
        ensureAdminExists();
        seedTopicsAndSessions();
        seedLessons();
    }

    // -------------------------------------------------------------------------
    // Fix null roles
    // -------------------------------------------------------------------------
    private void fixNullRoles() {
        List<User> usersWithNullRole = userRepository.findAll().stream()
                .filter(u -> u.getRole() == null)
                .toList();

        if (usersWithNullRole.isEmpty()) return;

        log.info("DataInitializer: Found {} users with null role, fixing...", usersWithNullRole.size());

        for (User user : usersWithNullRole) {
            String email = user.getEmail().toLowerCase();
            if (email.contains("admin")) {
                user.setRole(UserRole.ADMIN);
            } else if (email.contains("teacher")) {
                user.setRole(UserRole.TEACHER);
            } else {
                user.setRole(UserRole.STUDENT);
            }
            userRepository.save(user);
            log.info("DataInitializer: Set role {} for user {}", user.getRole(), user.getEmail());
        }
    }

    // -------------------------------------------------------------------------
    // Ensure admin account exists
    // -------------------------------------------------------------------------
    private void ensureAdminExists() {
        boolean adminExists = userRepository.findAll().stream()
                .anyMatch(u -> u.getRole() == UserRole.ADMIN);

        if (!adminExists) {
            User admin = new User();
            admin.setEmail("admin@lms.com");
            admin.setPasswordHash(passwordEncoder.encode("Admin@123"));
            admin.setFullName("System Admin");
            admin.setRole(UserRole.ADMIN);
            admin.setLevel(1);
            admin.setXp(0);
            admin.setStreak(0);
            admin.setTotalLessonsCompleted(0);
            admin.setFaceIdEnrolled(false);
            userRepository.save(admin);
            log.info("DataInitializer: Created default admin → email: admin@lms.com | password: Admin@123");
        }
    }

    // -------------------------------------------------------------------------
    // Seed demo Topics + Sessions
    // -------------------------------------------------------------------------
    private void seedTopicsAndSessions() {
        // Fix is_active = NULL cho các topic cũ từ init script
        topicRepository.findAll().stream()
                .filter(t -> t.getIsActive() == null)
                .forEach(t -> {
                    t.setIsActive(true);
                    topicRepository.save(t);
                    log.info("DataInitializer: Fixed is_active for topic '{}'", t.getName());
                });

        // Chỉ seed nếu chưa có topic tiếng Việt "Ăn uống"
        if (topicRepository.existsByName("Ăn uống & Nhà hàng")) {
            log.info("DataInitializer: Vietnamese topics already exist, skipping seed.");
            return;
        }

        log.info("DataInitializer: Seeding demo topics and sessions...");

        // Topic data: name, description, difficulty, estimatedMinutes, sessions[]
        Object[][] topicsData = {
            { "Ăn uống & Nhà hàng", "Học từ vựng và hội thoại về ẩm thực, gọi món, đặt bàn nhà hàng.", "BEGINNER", 45,
              new String[]{ "Gọi món tại nhà hàng", "Đặt bàn qua điện thoại", "Mô tả món ăn yêu thích", "Thanh toán hóa đơn" }
            },
            { "Du lịch & Khách sạn", "Từ vựng và tình huống thực tế khi đi du lịch, đặt phòng, hỏi đường.", "BEGINNER", 60,
              new String[]{ "Check-in khách sạn", "Hỏi đường và di chuyển", "Mua vé tham quan", "Xử lý vấn đề khi đi du lịch" }
            },
            { "Công việc & Văn phòng", "Tiếng Anh giao tiếp trong môi trường công sở, họp hành, email.", "INTERMEDIATE", 90,
              new String[]{ "Giới thiệu bản thân trong công việc", "Họp nhóm bằng tiếng Anh", "Viết email chuyên nghiệp", "Thuyết trình đơn giản" }
            },
            { "Mua sắm & Giao dịch", "Từ vựng mua sắm, thương lượng giá cả, khiếu nại hàng hóa.", "BEGINNER", 45,
              new String[]{ "Mua quần áo tại cửa hàng", "Thương lượng và mặc cả", "Đổi/trả hàng hóa", "Mua sắm online" }
            },
            { "Sức khỏe & Bệnh viện", "Tình huống tại bệnh viện, miêu tả triệu chứng, mua thuốc.", "INTERMEDIATE", 60,
              new String[]{ "Đặt lịch khám bác sĩ", "Mô tả triệu chứng bệnh", "Mua thuốc tại nhà thuốc", "Hỏi về chế độ ăn uống" }
            },
            { "Giáo dục & Học tập", "Từ vựng liên quan đến trường học, học tập, bài tập, bạn bè.", "BEGINNER", 45,
              new String[]{ "Giới thiệu trường học", "Hỏi về môn học và lịch học", "Thảo luận nhóm học tập", "Xin ý kiến thầy cô" }
            },
        };

        for (int i = 0; i < topicsData.length; i++) {
            Object[] data = topicsData[i];
            Topic topic = Topic.builder()
                    .name((String) data[0])
                    .description((String) data[1])
                    .difficultyLevel((String) data[2])
                    .estimatedDurationMinutes((Integer) data[3])
                    .orderIndex(i + 1)
                    .isActive(true)
                    .build();
            Topic savedTopic = topicRepository.save(topic);

            String[] sessionTitles = (String[]) data[4];
            for (int j = 0; j < sessionTitles.length; j++) {
                Session session = new Session();
                session.setTitle(sessionTitles[j]);
                session.setTopic(savedTopic);
                session.setOrderIndex(j + 1);
                sessionRepository.save(session);
            }

            log.info("DataInitializer: Seeded topic '{}' with {} sessions", data[0], sessionTitles.length);
        }
    }

    // -------------------------------------------------------------------------
    // Seed lessons cho các session chưa có bài học
    // -------------------------------------------------------------------------
    private void seedLessons() {
        List<Session> sessions = sessionRepository.findAll();
        int count = 0;
        for (Session session : sessions) {
            long existingLessons = lessonRepository.findBySessionIdOrderByOrderIndexAsc(session.getId()).size();
            if (existingLessons > 0) continue;

            // Mỗi session được seed 3 bài học mẫu
            Object[][] lessonsData = {
                { "Từ vựng cơ bản - " + session.getTitle(), "Học từ vựng và cụm từ thông dụng.", LessonType.VOCAB, 5 },
                { "Luyện phát âm - " + session.getTitle(), "Luyện phát âm chuẩn các từ quan trọng.", LessonType.SPEAKING, 7 },
                { "Hội thoại thực tế - " + session.getTitle(), "Luyện hội thoại với AI theo tình huống thực.", LessonType.QUIZ, 10 },
            };

            for (int i = 0; i < lessonsData.length; i++) {
                Lesson lesson = new Lesson();
                lesson.setTitle((String) lessonsData[i][0]);
                lesson.setDescription((String) lessonsData[i][1]);
                lesson.setType((LessonType) lessonsData[i][2]);
                lesson.setDurationMinutes((Integer) lessonsData[i][3]);
                lesson.setOrderIndex(i + 1);
                lesson.setSession(session);
                lessonRepository.save(lesson);
                count++;
            }
        }
        if (count > 0) {
            log.info("DataInitializer: Seeded {} lessons across {} sessions", count, sessions.size());
        }
    }
}
