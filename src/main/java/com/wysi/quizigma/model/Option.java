package com.wysi.quizigma.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "options")
public class Option {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "option")
    private String option;

    @ManyToOne
    @JoinColumn(name="question_id", nullable=false)
    private Question question;

    @OneToOne(cascade=CascadeType.ALL)
    @JoinColumn(name = "image_id")
    private Image image;

    public Option() {
    }

    public Option(String option, Image image) {
        this.option = option;
        this.image = image;
    }

    public Option(String option, String image) {
        this.option = option;
        this.image = new Image(image);
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

    public Image getImage() {
        return this.image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public void setImage(String image) {
        this.image = new Image(image);
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

}
