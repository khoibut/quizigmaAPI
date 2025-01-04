package com.wysi.quizigma.DTO;

public class AnswerDTO {

    private String username;
    private String answer;
    private Integer questionId;
    private String roomId;
    public AnswerDTO(String username, String answer, Integer questionId, String roomId) {
        this.username = username;
        this.answer = answer;
        this.questionId = questionId;
        this.roomId = roomId;
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

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }
}   
