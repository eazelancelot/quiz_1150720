package com.example.quiz_1150720.service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.quiz_1150720.dto.QuizOptionStatisticsResponse;
import com.example.quiz_1150720.dto.QuizSubmissionDetailResponse;
import com.example.quiz_1150720.repository.QuizSubmissionRepository;

@Service
public class QuizStatisticsService {

	private final QuizSubmissionRepository quizSubmissionRepository;

	public QuizStatisticsService(QuizSubmissionRepository quizSubmissionRepository) {
		this.quizSubmissionRepository = quizSubmissionRepository;
	}

	/**
	 * 1. 取得某問卷的所有填寫明細
	 */
	public List<QuizSubmissionDetailResponse> getQuizSubmissions(Long quizId) {
		List<Map<String, Object>> results = quizSubmissionRepository.findQuizSubmissionsByQuizId(quizId);

		return results.stream().map(row -> {
			QuizSubmissionDetailResponse dto = new QuizSubmissionDetailResponse();
			// 以下所有 row.get() 小括號中的字串要與 SQL 語法中的別名(Alias)一致
			dto.setUserAccount((String) row.get("userAccount"));
			dto.setUserName((String) row.get("userName"));

			/*
			 * 處理時間格式轉換:<br>
			 * 即使當初寫進 DB 的值是 LocalDateTime 的資料格式，但因為使用 SQL 搭配 
			 * List<Map<String, Object>> 查詢時，依照傳統的 JDBC 規範，對於資料庫的 DATETIME 
			 * 或 TIMESTAMP 欄位，放入通用的 Map 時，驅動預設統一會轉成 java.sql.Timestamp，
			 * 而不是較新的 java.time.LocalDateTime
			 */
			Timestamp timestamp = (Timestamp) row.get("submitTime");
			if (timestamp != null) {
				dto.setSubmitTime(timestamp.toLocalDateTime());
			}

			/*
			 * 在 Java 中，所有數值型別的包裝類別（Long、Integer、BigInteger、BigDecimal、Short 等）
			 * 全部都繼承自 java.lang.Number，所以先轉型成 Number 可以避免轉型錯誤
			 */
			dto.setQuestionId(((Number) row.get("questionId")).longValue());
			dto.setQuestionTitle((String) row.get("questionTitle"));
			dto.setSelectedOptionText((String) row.get("selectedOptionText"));
			dto.setAnswerText((String) row.get("answerText"));
			return dto;
		}).collect(Collectors.toList());
	}

	/**
	 * 2. 取得某問卷各題選項統計次數
	 */
	public List<QuizOptionStatisticsResponse> getQuizStatistics(Long quizId) {
		List<Map<String, Object>> results = quizSubmissionRepository.getQuizOptionStatistics(quizId);

		return results.stream().map(row -> {
			QuizOptionStatisticsResponse dto = new QuizOptionStatisticsResponse();
			// 以下所有 row.get() 小括號中的字串要與 SQL 語法中的別名(Alias)一致
			dto.setQuestionId(((Number) row.get("questionId")).longValue());
			dto.setQuestionTitle((String) row.get("questionTitle"));
			dto.setOptionId(((Number) row.get("optionId")).longValue());
			dto.setOptionText((String) row.get("optionText"));
			dto.setSelectCount(((Number) row.get("selectCount")).longValue());
			return dto;
		}).collect(Collectors.toList());
	}
}
