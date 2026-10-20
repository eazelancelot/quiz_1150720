package com.example.quiz_1150720.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class QuizSubmissionDetailResponse {
	private String userAccount; // 填寫者帳號
	private String userName; // 填寫者姓名
	private LocalDateTime submitTime; // 填寫時間
	private Long questionId; // 題目 ID
	private String questionTitle; // 題目名稱
	private String selectedOptionText;// 選擇題選項文字 (若是簡答題則為 null)
	private String answerText; // 簡答題填寫內容 (若是選擇題則為 null)
}
