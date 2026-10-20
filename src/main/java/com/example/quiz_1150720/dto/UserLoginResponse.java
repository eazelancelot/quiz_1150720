package com.example.quiz_1150720.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserLoginResponse {
	
	private String token;
	
	private String account;
	
	private String name;
}
