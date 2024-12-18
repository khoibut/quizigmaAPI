package com.wysi.quizigma.DTO;

import java.util.Base64;

import com.wysi.quizigma.model.Image;

public class OptionDTO {

    private Integer id;
    private String option;
    private String image;

    public OptionDTO() {
    }

    public OptionDTO(String option, String image) {
        this.option = option;
        this.image = image;
    }

    public OptionDTO(String option, Image image) {
        this.option = option;
        if (image.getImage() != null) {
            this.image = image.getType()+Base64.getEncoder().encodeToString(image.getImage());
        } else {
            this.image = null;
        }
    }
    public OptionDTO(Integer id, String option, Image image) {
        this.id = id;
        this.option = option;
        if (image.getImage() != null) {
            this.image = image.getType()+Base64.getEncoder().encodeToString(image.getImage());
        } else {
            this.image = null;
        }
    }

    public OptionDTO(Integer id, String option, String image) {
        this.id = id;
        this.option = option;
        this.image = image;
    }
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
