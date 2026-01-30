package com.example.appenglish.modules.auth.controller;

import com.example.appenglish.service.XPService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/xp")
public class XPController {

    private final XPService xpService;

    @Autowired
    public XPController(XPService xpService) {
        this.xpService = xpService;
    }

    /**
     * API cộng XP cho user
     *
     * @param userId ID của user (truyền trong URL)
     * @param amount Số XP cần cộng (truyền trong query param)
     * @param actionType Nguồn XP (VD: TestCompletion, DailyMission, v.v.)
     * @return HTTP response với trạng thái thành công
     */

    @PostMapping("/add/{userId}")
    public ResponseEntity<String> addXp(
            @PathVariable Long userId,
            @RequestParam Integer amount,
            @RequestParam String actionType
    ) {
        xpService.addXp(userId, amount, actionType); // Gọi service để cộng XP
        return ResponseEntity.ok("XP added successfully!"); // Trả về kết quả thành công
    }

    /**
     * API lấy tổng XP của user
     *
     * @param userId ID của user (truyền trong URL)
     * @return Tổng XP hiện tại của user
     */

    @GetMapping("/total/{userId}")
    public ResponseEntity<Integer> getTotalXp(@PathVariable Long userId) {
        Integer totalXp = xpService.getTotalXp(userId);// Gọi service tính tổng XP
        return ResponseEntity.ok(totalXp); // Trả về tổng XP
    }
}
