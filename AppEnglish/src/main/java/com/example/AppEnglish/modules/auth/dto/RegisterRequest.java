package com.example.appenglish.modules.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank(message = "email khônng được để trống")
    @Email
    private String email ;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 6 ,  message= "Mật khẩu phải có ít nhất 6 ký tự")
    private String password ;

    @NotBlank(message = "Tên không được để trống")
    private String fullName ;

    @NotBlank (message = "Vai trò không được để trống")
    private String  role ;

    private String learningGoal ;
    private String levelSelection ;
}
