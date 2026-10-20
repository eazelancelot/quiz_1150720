package com.example.quiz_1150720.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.quiz_1150720.constant.QuizResponseMessage;
import com.example.quiz_1150720.dto.ApiResponse;
import com.example.quiz_1150720.dto.QuizSubmitRequest;
import com.example.quiz_1150720.security.JwtUtil;
import com.example.quiz_1150720.service.QuizSubmissionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/quiz")
@CrossOrigin(origins = "*")
public class QuizSubmissionController {

	private final QuizSubmissionService quizSubmissionService;
	private final JwtUtil jwtUtil;

	public QuizSubmissionController(QuizSubmissionService quizSubmissionService, JwtUtil jwtUtil) {
		this.quizSubmissionService = quizSubmissionService;
		this.jwtUtil = jwtUtil;
	}

	/**
	 * 填寫並送出問卷 API -> POST /api/quiz/submit
	 */
	@PostMapping("/submit")
	public ResponseEntity<ApiResponse<Void>> submitQuiz(@RequestHeader("Authorization") String tokenHeader,
			@Valid @RequestBody QuizSubmitRequest request) {

		// 1. 從 Bearer Token 中解析出當前使用者的帳號(token 在使用者 login 成功後會取得)
		String token = tokenHeader.replace("Bearer ", "");
		String account = jwtUtil.extractAccount(token);

		// 2. 呼叫 Service 執行填寫問卷邏輯
		quizSubmissionService.submitQuiz(request, account);

		return ResponseEntity.ok(ApiResponse.success(QuizResponseMessage.SUBMIT_QUIZ_SUCCESS, null));
	}
}
