package com.wysi.quizigma.model;

import java.util.Base64;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "images")
public class Image {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "image", columnDefinition = "BYTEA")
    private byte[] image;

    @Column(name = "type", nullable = false)
    private String type;

    public Image() {
    }

    public Image(byte[] image, String type) {
        this.image = image;
        this.type = type;
    }

    public Image(String image) {
        if (image != null && image.startsWith("data:image")) {
            this.image = Base64.getDecoder().decode(image.split(",")[1]);
            this.type = image.split(",")[0];
        } else {
            this.image = null;
            this.type = null;
        }
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
