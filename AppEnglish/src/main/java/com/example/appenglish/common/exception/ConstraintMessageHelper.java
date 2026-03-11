package com.example.appenglish.common.exception;

import org.springframework.dao.DataIntegrityViolationException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Rút ra thông báo chi tiết từ DataIntegrityViolationException
 * để user biết đang bị chặn bởi bảng/dữ liệu nào.
 */
public final class ConstraintMessageHelper {

    private static final Map<String, String> TABLE_LABELS = new LinkedHashMap<>();
    static {
        TABLE_LABELS.put("pronunciation_attempts", "bản ghi chấm phát âm");
        TABLE_LABELS.put("lesson_videos", "video/transcript của bài học");
        TABLE_LABELS.put("lesson_vocabularies", "từ vựng của bài học");
        TABLE_LABELS.put("lessons", "bài học trong session");
        TABLE_LABELS.put("sessions", "session trong khóa học");
        TABLE_LABELS.put("participated_sessions", "lịch sử tham gia session");
        TABLE_LABELS.put("user_progress", "tiến độ học");
        TABLE_LABELS.put("completed_lessons", "bài đã hoàn thành");
    }

    /** Pattern: "referenced from table \"table_name\"" (PostgreSQL) */
    private static final Pattern REFERENCED_TABLE = Pattern.compile("referenced from table\\s+\"([^\"]+)\"", Pattern.CASE_INSENSITIVE);

    /**
     * Tạo thông báo thân thiện khi xóa lesson/session bị chặn bởi FK.
     */
    public static String buildDeleteConstraintMessage(DataIntegrityViolationException e, String entityLabel) {
        String raw = e.getMostSpecificCause() != null ? e.getMostSpecificCause().getMessage() : e.getMessage();
        if (raw == null) raw = "";

        List<String> parts = new ArrayList<>();
        Matcher m = REFERENCED_TABLE.matcher(raw);
        while (m.find()) {
            String table = m.group(1).toLowerCase();
            String label = TABLE_LABELS.getOrDefault(table, "dữ liệu liên quan (" + table + ")");
            if (!parts.contains(label)) {
                parts.add(label);
            }
        }

        StringBuilder msg = new StringBuilder();
        msg.append("Không thể xóa ").append(entityLabel).append(" vì còn ");
        if (parts.isEmpty()) {
            msg.append("dữ liệu liên quan (video, transcript, bản ghi phát âm, v.v.). Hãy xóa hoặc ẩn dữ liệu phụ thuộc trước.");
        } else {
            msg.append(String.join(", ", parts));
            msg.append(". Bạn cần xóa hoặc xử lý các mục này trước khi xóa.");
        }
        return msg.toString();
    }
}
