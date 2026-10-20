package com.example.quiz_1150720.entity;

import java.time.LocalDateTime;

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
@Table(name = "quiz_submission")
public class QuizSubmission {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "quiz_id", nullable = false)
	private Long quizId;

	@Column(name = "user_account", nullable = false)
	private String userAccount;

	@Column(name = "submit_time", nullable = false)
	private LocalDateTime submitTime;
}
