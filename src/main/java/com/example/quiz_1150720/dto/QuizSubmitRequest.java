package com.example.quiz_1150720.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class QuizSubmitRequest {

    @NotNull(message = "問卷 ID 不得為空")
    private Long quizId;

    @NotEmpty(message = "作答內容不得為空")
    @Valid
    private List<AnswerItem> answers;

    @Getter
    @Setter
    public static class AnswerItem {
        @NotNull(message = "題目 ID 不得為空")
        private Long questionId;

        // 選擇題選項 ID (若為簡答題則傳 null)
        private Long selectedOptionId;

        // 簡答題文字內容 (若為選擇題則傳 null)
        private String answerText;
    }
}
