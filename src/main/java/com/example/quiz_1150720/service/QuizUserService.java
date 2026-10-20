package com.example.quiz_1150720.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.quiz_1150720.constant.QuizResponseMessage;
import com.example.quiz_1150720.dto.UserLoginRequest;
import com.example.quiz_1150720.dto.UserLoginResponse;
import com.example.quiz_1150720.dto.UserRegisterRequest;
import com.example.quiz_1150720.entity.QuizUser;
import com.example.quiz_1150720.repository.QuizUserRepository;
import com.example.quiz_1150720.security.JwtUtil;

@Service
public class QuizUserService {

	private final QuizUserRepository quizUserRepository;
	
	private final PasswordEncoder passwordEncoder;
	
	private final JwtUtil jwtUtil;

	public QuizUserService(QuizUserRepository quizUserRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
		this.quizUserRepository = quizUserRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtUtil = jwtUtil;
	}

	@Transactional
	public void register(UserRegisterRequest request) {
		if (quizUserRepository.existsByAccount(request.getAccount())) {
			throw new RuntimeException(QuizResponseMessage.USER_ACCOUNT_EXISTS);
		}
		if (quizUserRepository.existsByEmail(request.getEmail())) {
			throw new RuntimeException(QuizResponseMessage.USER_EMAIL_EXISTS);
		}

		QuizUser user = new QuizUser();
		user.setAccount(request.getAccount());
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		user.setName(request.getName());
		user.setPhone(request.getPhone());
		user.setEmail(request.getEmail());

		quizUserRepository.save(user);
	}

	/**
	 * 2. 登入功能：改為回傳包含 JWT Token 的 Response 物件
	 */
	public UserLoginResponse login(UserLoginRequest request) {
		QuizUser user = quizUserRepository.findByAccount(request.getAccount());
		if (user == null) {
			throw new RuntimeException(QuizResponseMessage.USER_NOT_FOUND);
		}

		if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
			throw new RuntimeException(QuizResponseMessage.PASSWORD_MISMATCH);
		}

		// 產出 JWT Token
		String token = jwtUtil.generateToken(user.getAccount());

		// 回傳 Token 及基本資訊
		return new UserLoginResponse(token, user.getAccount(), user.getName());
	}

	/**
	 * 3. 登出功能 由於 JWT 是無狀態的（Stateless），伺服器端不需要保留 Session。 登出通常由前端直接刪除儲存的 Token
	 * 即可。這裡保留 API 端點回傳成功訊息。
	 */
	public void logout() {
		// 無狀態架構下後端可不需做額外處理，若需嚴格登出可導入 Redis 黑名單機制
	}
}
