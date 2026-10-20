package com.example.quiz_1150720.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.quiz_1150720.constant.QuizResponseMessage;
import com.example.quiz_1150720.dto.ApiResponse;
import com.example.quiz_1150720.dto.UserLoginRequest;
import com.example.quiz_1150720.dto.UserLoginResponse;
import com.example.quiz_1150720.dto.UserRegisterRequest;
import com.example.quiz_1150720.service.QuizUserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/user")
// 加上 @CrossOrigin：允許前端網域跨域存取，改用 JWT 後，不需要設定 allowCredentials = "true"，origins 也可以彈性開放
@CrossOrigin(origins = "*")
public class UserController {

    private final QuizUserService quizUserService;

    public UserController(QuizUserService quizUserService) {
        this.quizUserService = quizUserService;
    }

    // 1. 註冊 API -> POST
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> register(@Valid @RequestBody UserRegisterRequest request) {
        quizUserService.register(request);
        return ResponseEntity.ok(ApiResponse.success(QuizResponseMessage.REGISTER_SUCCESS, null));
    }

    // 2. 登入 API -> POST（回傳帶有 JWT Token 的資料）
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserLoginResponse>> login(@Valid @RequestBody UserLoginRequest request) {
        UserLoginResponse response = quizUserService.login(request);
        return ResponseEntity.ok(ApiResponse.success(QuizResponseMessage.LOGIN_SUCCESS, response));
    }

    // 3. 登出 API -> POST
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout() {
        quizUserService.logout();
        return ResponseEntity.ok(ApiResponse.success(QuizResponseMessage.LOGOUT_SUCCESS, null));
    }
}
