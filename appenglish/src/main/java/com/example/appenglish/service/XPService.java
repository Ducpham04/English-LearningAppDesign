package com.example.appenglish.service;

import com.example.appenglish.entity.User;
import com.example.appenglish.entity.XPLog;
import com.example.appenglish.repository.UserRepository;
import com.example.appenglish.repository.XPLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class XPService {
    private final UserRepository userRepository;
    private final XPLogRepository xpLogRepository;

    @Autowired
    public XPService(UserRepository userRepository, XPLogRepository xpLogRepository) {
        this.userRepository = userRepository;
        this.xpLogRepository = xpLogRepository;
    }

    /**
     * Thêm XP cho user và lưu vào log hành động
     *
     * @param userId ID của user
     * @param amount Số XP cần cộng
     * @param actionType Loại hành động (điểm XP kiếm được từ đâu)
     */

    public void addXp(Long userId, Integer amount, String actionType) {
        // Tìm user trong cơ sở dữ liệu, nếu không tồn tại thì ném lỗi
        User user = userRepository.findById(userId).orElseThrow(() ->
                new RuntimeException("User not found!"));

        // Tạo mới log XP và lưu vào bảng XPLog
        XPLog xpLog = new XPLog();
        xpLog.setUser(user);// Liên kết user
        xpLog.setAmount(amount);// Số XP kiếm được
        xpLog.setAction(actionType);// Nguồn XP (VD: TestCompletion, DailyMission)
        xpLogRepository.save(xpLog); // Lưu vào database
    }

    /**
     * Tính tổng XP của user dựa trên bảng XPLog
     *
     * @param userId ID của user
     * @return Tổng XP hiện tại của user
     */

    public Integer getTotalXp(Long userId) {
        //Lấy tất cả các log XP liên quan đến user này
        List<XPLog> logs = xpLogRepository.findByUserId(userId);
        // Tính tổng XP từ các log
        return logs.stream().mapToInt(XPLog::getAmount).sum();
    }
}
