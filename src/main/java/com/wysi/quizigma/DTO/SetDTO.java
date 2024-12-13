package com.wysi.quizigma.DTO;

import java.util.Base64;
import java.util.List;

import com.wysi.quizigma.model.Image;

public class SetDTO {

    private Integer id;
    private String name;
    private String description;
    private String image;
    private List<QuestionDTO> questions;

    // Parameterized constructor
    public SetDTO(Integer id, String name, String description, String image, List<QuestionDTO> questions) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.image = image;
        this.questions = questions;
    }

    public SetDTO() {
    }

    public SetDTO(Integer id, String name, String description, Image image, List<QuestionDTO> questions) {
        this.id = id;
        this.name = name;
        this.description = description;
        if(image.getImage()!=null){
            this.image = image.getType()+Base64.getEncoder().encodeToString(image.getImage());
        } else {
            this.image = null;
        }
        this.questions = questions;
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<QuestionDTO> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuestionDTO> questions) {
        this.questions = questions;
    }

}
