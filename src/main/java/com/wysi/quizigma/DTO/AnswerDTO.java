package com.wysi.quizigma.DTO;

public class AnswerDTO {

    private String player;
    private String answer;
    private Integer questionId;
    private String roomId;
    public AnswerDTO(String player, String answer, Integer questionId, String roomId) {
        this.player = player;
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

    public String getPlayer() {
        return player;
    }

    public String getAnswer() {
        return answer;
    }

    public void setUsername(String player) {
        this.player = player;
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
