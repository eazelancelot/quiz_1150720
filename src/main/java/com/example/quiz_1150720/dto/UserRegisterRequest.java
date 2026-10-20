package com.example.quiz_1150720.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRegisterRequest {

    @NotBlank(message = "帳號不得為空")
    private String account;

    @NotBlank(message = "密碼不得為空")
    private String password;

    @NotBlank(message = "姓名不得為空")
    private String name;

    @NotBlank(message = "電話不得為空")
    private String phone;

    @NotBlank(message = "Email 不得為空")
    @Email(message = "Email 格式不正確")
    private String email;
}
