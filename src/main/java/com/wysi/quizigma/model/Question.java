package com.wysi.quizigma.model;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "question")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "question", nullable = false)
    private String question;

    @Column(name = "image", columnDefinition = "BYTEA")
    private byte[] image;

    @ManyToOne
    @JoinColumn(name = "set_id", nullable = false)
    private Set set;

    @OneToMany
    private List<Option> options;

    @Column(name = "correct_option", nullable = false)
    private List<Integer> answers;

    public Question() {
    }

    public Question(String question, byte[] image, Set set, List<Option> options, List<Integer> answers) {
        this.question = question;
        this.image = image;
        this.set = set;
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

    public Set getSet() {
        return set;
    }

    public void setSet(Set set) {
        this.set = set;
    }

    public List<Option> getOptions() {
        return options;
    }

    public void setOptions(List<Option> options) {
        this.options = options;
    }

    public List<Integer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<Integer> answers) {
        this.answers = answers;
    }

}
