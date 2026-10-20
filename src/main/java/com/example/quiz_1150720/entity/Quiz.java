package com.example.quiz_1150720.entity;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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
@Table(name = "quiz")
public class Quiz {

    @Id
    /*
     * 【@GeneratedValue(strategy = GenerationType.IDENTITY) 說明】：
     * - 代表主鍵（ID）的產生策略交由資料庫底層的「自動遞增 (AUTO_INCREMENT)」來處理。
     * - 當你新增一筆資料並執行 `.save()` 時，Hibernate 會先將物件送進資料庫，
     *   由 MySQL 自動產生並派發一個流水號 ID 給它，然後再把這個 ID 帶回 Java 物件中。
     */
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // 當 欄位名稱與屬性變數名稱一樣 時，name = "id" 可以不需要寫 --> @Column 小括號中沒東西 --> 整行也可以不寫
    @Column(name = "id")
    private Long id;

    // 欄位名稱與屬性變數名稱一樣 時，name = "title" 可以不需要寫
    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    /* Spring Boot 自動將「小駝峰」轉為「蛇形命名（底線）」:
     * startDate → 自動映射到資料庫的 start_date
     * endDate → 自動映射到資料庫的 end_date
     * isPublished → 自動映射到資料庫的 is_published
     * 有了這個自動轉換機制，你同樣不需要手動去寫 @Column(name = "start_date")
     */
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;
    
    @Column(nullable = false)
    private LocalDate endDate;

    @Column(nullable = false)
    private Boolean isPublished = false;

	/*
     * 【@OneToMany 說明】：
     * - mappedBy = "quiz"：代表關聯的擁有者在 Question 實體中的 quiz 欄位（對應資料庫的 quiz_id）。
     * - cascade = CascadeType.ALL：級聯全開。當儲存或刪除問卷時，底下的問題會一起被儲存或刪除。
     * - orphanRemoval = true：孤兒刪除。若從 questions 清單中移除某個問題，該問題會自動被刪除。
     */
    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Question> questions;
}
