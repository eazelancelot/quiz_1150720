package com.example.quiz_1150720.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.quiz_1150720.entity.Quiz;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {

	@Query(value = "SELECT * FROM quiz ORDER BY id DESC", nativeQuery = true)
    public List<Quiz> findAllQuizzesNative();

    @Query(value = "SELECT * FROM quiz WHERE id = :id", nativeQuery = true)
    public Quiz findQuizByIdNative(@Param("id") Long id);
    
    /**
     * 【原生 SQL 刪除語法】
     * - @Modifying：告訴 Spring Data JPA 這是一支會 新增/修改/刪除資料的 SQL（非 SELECT），否則會拋出例外。
     * - value = "DELETE FROM quiz WHERE id IN (:ids)"：透過 SQL 的 IN 語法支援批次刪除傳入的一組 ID 清單。
     * - nativeQuery = true：使用原生 SQL。
     * - @Transactional：因為 @Modifying 是異動資料(新增/修改/刪除)，要求一定要加上事務( @Transactional)，不然
     *    會拋出例外，但最理想的事務應該是要加在 Service 上，讓 Repository 專責處理資料
     * 
     * 註：因為資料庫外來鍵（Foreign Key）有設定 ON DELETE CASCADE（或依賴 JPA 的級聯刪除），
     * 當主檔 quiz 被刪除時，底下的 question 與 question_option 會自動一併被清除。
     */
    @Modifying
    @Query(value = "DELETE FROM quiz WHERE id IN (:ids)", nativeQuery = true)
    public void deleteQuizzesByIdsNative(@Param("ids") List<Long> ids);
}
