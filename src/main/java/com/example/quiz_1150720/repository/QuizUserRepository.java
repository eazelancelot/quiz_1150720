package com.example.quiz_1150720.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.quiz_1150720.entity.QuizUser;

@Repository
public interface QuizUserRepository extends JpaRepository<QuizUser, String> {
    
	/**
     * 檢查帳號是否存在 (Native SQL)
     * 透過 COUNT 回傳大於 0 的整數或判斷是否有資料
     */
    @Query(value = "SELECT COUNT(*) > 0 FROM quiz_user WHERE account = :account", nativeQuery = true)
    public boolean existsByAccount(@Param("account") String account);

    /**
     * 檢查 Email 是否已被註冊 (Native SQL)
     */
    @Query(value = "SELECT COUNT(*) > 0 FROM quiz_user WHERE email = :email", nativeQuery = true)
    public boolean existsByEmail(@Param("email") String email);

    /**
     * 根據帳號查詢使用者完整實體 (Native SQL)
     * 使用 SELECT * 映射回 quiz_user 資料表
     */
    @Query(value = "SELECT * FROM quiz_user WHERE account = :account", nativeQuery = true)
    public QuizUser findByAccount(@Param("account") String account);
}
