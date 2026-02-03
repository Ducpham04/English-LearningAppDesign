package com.example.appenglish.modules.userDetail.controller;

import com.example.appenglish.modules.userDetail.dto.UserXpDto;
import com.example.appenglish.modules.userDetail.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/leaderboard")
@RequiredArgsConstructor
public class LeaderboardController {
    private final UserService userService;

    @GetMapping("/top-week")
    public List<UserXpDto> getTopUsersLastWeek() {
        return userService.getTopUsersByXpInLastWeek();
    }

    @GetMapping("/top-today")
    public List<UserXpDto> getTopUsersToday() {
        return userService.getTopUsersByXpToday();
    }

    @GetMapping("/top-month")
    public List<UserXpDto> getTopUsersThisMonth() {
        return userService.getTopUsersByXpThisMonth();
    }
}
