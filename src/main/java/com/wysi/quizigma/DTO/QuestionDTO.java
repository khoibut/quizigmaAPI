package com.wysi.quizigma.DTO;

import java.util.List;

public class QuestionDTO {

    private Integer id;
    private String question;
    private byte[] image;
    private Integer setId;
    private List<OptionDTO> options;
    private List<Integer> answers;

    public QuestionDTO(Integer id, String question, byte[] image, Integer setId, List<OptionDTO> options, List<Integer> answers) {
        this.id = id;
        this.question = question;
        this.image = image;
        this.setId = setId;
        this.options = options;
        this.answers = answers;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public Integer getSetId() {
        return setId;
    }

    public void setSetId(Integer setId) {
        this.setId = setId;
    }

    public List<OptionDTO> getOptions() {
        return options;
    }

    public void setOptions(List<OptionDTO> options) {
        this.options = options;
    }

    public List<Integer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<Integer> answers) {
        this.answers = answers;
    }

}
