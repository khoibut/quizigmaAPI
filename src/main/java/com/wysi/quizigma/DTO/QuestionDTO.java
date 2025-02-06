package com.wysi.quizigma.DTO;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import com.wysi.quizigma.model.Image;

public class QuestionDTO {

    private Integer id;
    private String question;
    private String type;
    private String image;
    private Integer setId;
    private List<OptionDTO> options = new ArrayList<>();

    private List<Integer> answers;

    public QuestionDTO() {
    }

    public QuestionDTO(String question, String type, String image, Integer setId, List<OptionDTO> options, List<Integer> answers) {
        this.id = null;
        this.question = question;
        this.type = type;
        this.image = image;
        this.setId = setId;
        this.options = options;
        this.answers = answers != null ? answers : new ArrayList<>();
    }

    public QuestionDTO(Integer id, String question, String type, Image image, Integer setId, List<OptionDTO> options, List<Integer> answers) {
        this.id = id;
        this.question = question;
        this.type = type;
        if (image.getImage() != null) {
            this.image = image.getType() + "," + Base64.getEncoder().encodeToString(image.getImage());
        } else {
            this.image = null;
        }
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
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
