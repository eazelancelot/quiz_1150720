package com.example.quiz_1150720.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.quiz_1150720.constant.QuizResponseMessage;
import com.example.quiz_1150720.dto.ApiResponse;
import com.example.quiz_1150720.dto.QuizDeleteRequest;
import com.example.quiz_1150720.dto.QuizRequest;
import com.example.quiz_1150720.dto.QuizResponse;
import com.example.quiz_1150720.service.QuizService;

import jakarta.validation.Valid;

@CrossOrigin
@RestController
@RequestMapping("/api/quiz")
public class QuizController {

    // 使用 final 宣告，並透過建構方法注入 (不再使用 @Autowired)
    private final QuizService quizService;

    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    // 1. 創建問卷 -> POST
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<QuizResponse>> createQuiz(@Valid @RequestBody QuizRequest request) {
        QuizResponse response = quizService.createQuiz(request);
        return ResponseEntity.ok(ApiResponse.success(QuizResponseMessage.CREATE_SUCCESS, response));
    }

    // 2. 更新問卷 -> POST
    @PostMapping("/update")
    public ResponseEntity<ApiResponse<QuizResponse>> updateQuiz(@Valid @RequestBody QuizRequest request) {
        QuizResponse response = quizService.updateQuiz(request);
        return ResponseEntity.ok(ApiResponse.success(QuizResponseMessage.UPDATE_SUCCESS, response));
    }

    // 3. 取得問卷列表 -> GET
    @GetMapping("/list")
    public ResponseEntity<ApiResponse<List<QuizResponse>>> getAllQuizzes() {
        List<QuizResponse> responses = quizService.getAllQuizzes();
        return ResponseEntity.ok(ApiResponse.success(QuizResponseMessage.GET_LIST_SUCCESS, responses));
    }

    // 4. 根據問卷 id 取得該問卷的問題以及選項 -> GET (透過路徑變數傳遞 id)
    @GetMapping("/detail/{id}")
    public ResponseEntity<ApiResponse<QuizResponse>> getQuizById(@PathVariable("id") Long id) {
        QuizResponse response = quizService.getQuizById(id);
        return ResponseEntity.ok(ApiResponse.success(QuizResponseMessage.GET_DETAIL_SUCCESS, response));
    }
    
    // 5. 根據問卷 ID 清單刪除問卷（支援單筆或多筆）-> POST
    @PostMapping("/delete")
    public ResponseEntity<ApiResponse<Void>> deleteQuizzes(@Valid @RequestBody QuizDeleteRequest request) {
        quizService.deleteQuizzes(request);
        return ResponseEntity.ok(ApiResponse.success(QuizResponseMessage.DELETE_SUCCESS, null));
    }
}
