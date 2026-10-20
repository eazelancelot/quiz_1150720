package com.example.quiz_1150720.security;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

/**
 * @Component:<br>
 * - 用來標記自定義的「業務類別、工具類或元件」，通常用在像 JwtUtil 這種通用的工具類。<br>
 * - 當在其他地方(例如: QuizUserService) 試圖注入它時，Spring 才會找到對應的 Bean。
 */
@Component
public class JwtUtil {

	// 透過 JJWT 0.13.0 建議的方式自動產生符合 HS256 安全長度的密鑰
	private final SecretKey SECRET_KEY = Jwts.SIG.HS256.key().build();

	// Token 有效時間：24 小時 (毫秒)
	private final long EXPIRATION_TIME = 86400000L;

	/**
	 * 1. 根據帳號產生 JWT Token
	 */
	public String generateToken(String account) {
		return Jwts.builder() // 建立一個全新的 JWT 建造者（JwtBuilder）物件，為組裝 JWT 的起點
				.subject(account) // 設定 JWT 的 Subject（主旨 / sub 宣告），將使用者帳號填入以便未來辨識身分
				.issuedAt(new Date()) // 設定 JWT 的 Issued At（發行時間 / iat 宣告），記錄 Token 產生的精確時間點
				.expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // 設定 JWT 的 Expiration Time（過期時間 /
																					// exp 宣告），定義有效期限
				.signWith(SECRET_KEY) // 使用密鑰與演算法對 JWT 進行數位簽章，防止 Token 在傳輸過程中遭惡意竄改
				.compact(); // 將前面設定好的資料打包、序列化，組合成常見的三段式字串格式回傳
	}

	/**
	 * 2. 從 Token 中解析出帳號
	 */
	public String extractAccount(String token) {
		Claims claims = Jwts.parser() // 建立 JWT 解析器的建造者，準備開始解密與驗證
				.verifyWith(SECRET_KEY) // 設定用來驗證簽章的密鑰，確保 Token 未被竄改且來源合法
				.build() // 將解析器配置建構成一個不可變的 JwtParser 實體
				.parseSignedClaims(token) // 解析並驗證帶有簽名的 JWT (JWS)，會自動檢查格式、簽章及是否過期
				.getPayload(); // 取得 Token 內部的 Payload（承載資料 / 聲明 Claims），將所有內部夾帶的資料取出

		// 從取出的 Claims 資料中讀取 Subject (sub) 欄位，取得當初存入的使用者帳號
		return claims.getSubject();
	}

	/**
	 * 3. 驗證 Token 是否有效
	 */
	public boolean validateToken(String token) {
		try {
			Jwts.parser() // 建立 JWT 解析器的建造者
					.verifyWith(SECRET_KEY) // 設定用來驗證簽章的密鑰
					.build() // 建立 JwtParser 實體
					.parseSignedClaims(token); // 解析並驗證 Token，若過程順利代表有效；若過期、被竄改或格式錯誤會拋出例外
			return true;
		} catch (Exception e) {
			return false; // Token 驗證失敗（過期、簽名不符或格式錯誤時被 catch 攔截並回傳 false）
		}
	}
}
