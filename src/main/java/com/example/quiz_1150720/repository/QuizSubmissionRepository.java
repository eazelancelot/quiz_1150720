package com.example.quiz_1150720.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.quiz_1150720.entity.QuizSubmission;

@Repository
public interface QuizSubmissionRepository extends JpaRepository<QuizSubmission, Long> {

	/**
	 * 1. 寫入問卷填寫主檔 (Native SQL)
	 */
	@Modifying
	@Query(value = "INSERT INTO quiz_submission (quiz_id, user_account, submit_time) VALUES (:quizId, :account, NOW())", nativeQuery = true)
	public void insertSubmission(@Param("quizId") Long quizId, @Param("account") String account);

	/**
	 * 2. 取得剛剛 INSERT 進去的自增主檔 ID (Native SQL)<br>
	 * - 在多執行緒、高併發（Concurrency）的情況下，使用 LAST_INSERT_ID() 絕對不會拿到別人的流水號， 因為在 MySQL
	 * 中，LAST_INSERT_ID() 不是全域變數，而是繫結在當前的資料庫連線（Connection）上的，
	 * 它的意思是：「告訴我這個連線剛剛最近一次產生的自增 ID 是多少」。所以 Service 中的
	 * 
	 * @Transactional 保證了同一個連線
	 */
	@Query(value = "SELECT LAST_INSERT_ID()", nativeQuery = true)
	public Long getLastInsertId();

	/**
	 * 3. 查詢某問卷所有填寫紀錄與作答內容<br>
	 * 如果回傳結果包含多張表的各種欄位，用 List<Map<String, Object>> 接收最為彈性安全， 再由 Service 轉成 DTO
	 * 
	 */
	@Query(value = "SELECT qs.user_account AS userAccount, " //
			+ "       u.name AS userName, " //
			+ "       qs.submit_time AS submitTime, " //
			+ "       q.id AS questionId, "//
			+ "       q.title AS questionTitle, " //
			+ "       qo.option_text AS selectedOptionText, "//
			+ "       ad.answer_text AS answerText " //
			+ "FROM quiz_submission qs "//
			+ "JOIN quiz_user u ON qs.user_account = u.account "//
			+ "JOIN quiz_answer_detail ad ON qs.id = ad.submission_id " //
			+ "JOIN question q ON ad.question_id = q.id "//
			+ "LEFT JOIN question_option qo ON ad.selected_option_id = qo.id "//
			+ "WHERE qs.quiz_id = :quizId", nativeQuery = true)
	public List<Map<String, Object>> findQuizSubmissionsByQuizId(@Param("quizId") Long quizId);

	/**
	 * 4. 統計某問卷各題選項被選擇的次數 (忽略簡答)
	 */
	@Query(value = "SELECT q.id AS questionId, " //
			+ "       q.title AS questionTitle, " //
			+ "       qo.id AS optionId, "//
			+ "       qo.option_text AS optionText, " //
			+ "       COUNT(ad.id) AS selectCount " //
			+ "FROM question q "//
			+ "JOIN question_option qo ON q.id = qo.question_id "//
			+ "LEFT JOIN quiz_answer_detail ad ON qo.id = ad.selected_option_id " //
			+ "WHERE q.quiz_id = :quizId "//
			+ "GROUP BY q.id, q.title, qo.id, qo.option_text", nativeQuery = true)
	public List<Map<String, Object>> getQuizOptionStatistics(@Param("quizId") Long quizId);

}
