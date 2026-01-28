package com.example.appenglish.modules.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    private String email ;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 6, message = "Mật khẩu phải có ít nhất 6 ký tự")
    private String password ;

    @NotBlank(message = "Tên không được để trống")
    @Size(min = 2, max = 100, message = "Tên phải từ 2 đến 100 ký tự")
    private String fullName ;

    @NotBlank (message = "Vai trò không được để trống")
    private String  role ;

    @NotBlank (message ="Mục đích học tập không được để trống")
    private String learningGoal ;

    @NotBlank (message ="Level học tiếng anh không được để trống")
    private String levelSelection ;

}
