package com.wysi.quizigma.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "players")
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @Column(name = "score", nullable = false)
    private Integer rightCount;

    @Column(name = "wrong", nullable = false)
    private Integer wrongCount;

    public Player() {
    }

    public Player(String name, Room room) {
        this.name = name;
        this.room = room;
        this.rightCount = 0;
        this.wrongCount = 0;
    }

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

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public Integer getRightCount() {
        return rightCount;
    }

    public void setRightCount(Integer rightCount) {
        this.rightCount = rightCount;
    }

    public Integer getWrongCount() {
        return wrongCount;
    }

    public void setWrongCount(Integer wrongCount) {
        this.wrongCount = wrongCount;
    }

    public void incrementRightCount() {
        this.rightCount++;
    }

    public void incrementWrongCount() {
        this.wrongCount++;
    }

}
