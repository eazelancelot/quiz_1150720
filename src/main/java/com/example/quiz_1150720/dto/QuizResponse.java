package com.example.quiz_1150720.dto;

import java.time.LocalDate;
import java.util.List;

import com.example.quiz_1150720.enums.QuestionType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuizResponse {
    private Long id;
    private String title;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isPublished;
    private List<QuestionResponse> questions;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuestionResponse {
        private Long id;
        private Integer questionNo;
        private String title;
        private QuestionType type;
        private Boolean isRequired;
        private List<QuestionOptionResponse> options;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuestionOptionResponse {
        private Long id;
        private Integer optionNo;
        private String optionText;
    }
}
