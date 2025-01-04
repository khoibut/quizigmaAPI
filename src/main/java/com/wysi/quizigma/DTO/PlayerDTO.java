package com.wysi.quizigma.DTO;

public class PlayerDTO {
    private String name;
    private Integer score;
    private Integer correct;
    private Integer incorrect;
    public PlayerDTO() {
    }

    public PlayerDTO(String name) {
        this.name = name;
        this.score = 0;
        this.correct = 0;
        this.incorrect = 0;
    }

    public String getName() {
        return name;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Integer getCorrect() {
        return correct;
    }

    public void setCorrect(Integer correct) {
        this.correct = correct;
    }

    public Integer getIncorrect() {
        return incorrect;
    }

    public void setIncorrect(Integer incorrect) {
        this.incorrect = incorrect;
    }

    public void incrementScore() {
        this.score++;
    }

    public void incrementCorrect() {
        this.correct++;
    }

    public void incrementIncorrect() {
        this.incorrect++;
    }

    public void decrementScore() {
        this.score--;
    }

    public void decrementCorrect() {
        this.correct--;
    }

    public void decrementIncorrect() {
        this.incorrect--;
    }

}
