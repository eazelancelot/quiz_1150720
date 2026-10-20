package com.example.quiz_1150720.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class QuizOptionStatisticsResponse {
	private Long questionId; // 題目 ID
	private String questionTitle; // 題目名稱
	private Long optionId; // 選項 ID
	private String optionText; // 選項文字
	private Long selectCount; // 被選擇的次數
}
