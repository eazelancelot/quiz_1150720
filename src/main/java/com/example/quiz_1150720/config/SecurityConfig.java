package com.example.quiz_1150720.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * - 用來標記「設定類別」，專門用來集中管理與宣告多個 @Bean（例如安全設定、資料庫連線池、
 *   第三方套件初始化等）。<br>
 * - 通常會搭配 @Bean 註解一起使用，用來手動產生並註冊一些無法直接用 @Component 宣告的第三方套件或複雜物件<br>
 * - 無法直接用 @Component 宣告的情況，常見的有:<br>
 *   1. 該類別是「別人寫的第三方套件」（你無法修改原始碼）<br>
 *   2. 同一個類別需要「建立多個不同設定的實例（Instances）」<br>
 *   3. 物件的初始化過程「非常複雜」或需要經過多步驟建造<br>
 * 
 * - 總結:<br>
 * - @Component：類別是你寫的、程式碼在你手上、且只需要單一個預設實例。<br>
 * - @Configuration + @Bean：類別是別人寫的、或是你需要客製化多個不同設定的同型別物件、
 * 又或者是初始化過程需要寫一堆複雜的建構邏輯。
 */
@Configuration
public class SecurityConfig {

	/*
	 * 在一個 @Configuration 類別裡定義多個 @Bean，且這些 Bean 彼此互相呼叫時，Spring 會透過 CGLIB 
	 * 代理機制 確保不管呼叫幾次，拿到的永遠都是同一個單例（Singleton）實體。
	 */
	// 使用 Argon2PasswordEncoder 來進行密碼的高安全性雜湊與比對
    @Bean
    public PasswordEncoder passwordEncoder() {
        // 參數分別代表: saltLength, hashLength, parallelism, memory, iterations
        return Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();
    }
}
