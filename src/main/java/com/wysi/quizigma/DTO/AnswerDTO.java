package com.wysi.quizigma.DTO;

public class AnswerDTO {

    private String username;
    private String answer;
    private Integer questionId;

    public AnswerDTO(String username, String answer, Integer questionId) {
        this.username = username;
        this.answer = answer;
        this.questionId = questionId;
    }

    public AnswerDTO() {
    }

    public Integer getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Integer questionId) {
        this.questionId = questionId;
    }

    public String getUsername() {
        return username;
    }

    public String getAnswer() {
        return answer;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

}
