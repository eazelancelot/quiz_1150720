package com.example.quiz_1150720.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor // JPA 強制需要無參建構子
@AllArgsConstructor // 方便你在程式碼中 new 物件
@Entity
@Table(name = "quiz_answer_detail")
public class QuizAnswerDetail {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "submission_id", nullable = false)
	private Long submissionId;

	@Column(name = "question_id", nullable = false)
	private Long questionId;

	@Column(name = "selected_option_id")
	private Long selectedOptionId; // 選擇題選項 ID（簡答題時為 null）

	@Column(name = "answer_text", columnDefinition = "TEXT")
	private String answerText; // 簡答題填寫內容（選擇題時為 null）
}
