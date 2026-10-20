package com.example.quiz_1150720.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserLoginRequest {

    @NotBlank(message = "帳號不得為空")
    private String account;

    @NotBlank(message = "密碼不得為空")
    private String password;
}
