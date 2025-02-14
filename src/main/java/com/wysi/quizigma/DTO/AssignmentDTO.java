package com.wysi.quizigma.DTO;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AssignmentDTO {

    private String id;
    private String creator;
    private Integer setId;
    private Integer maxQuestions;
    private Date deadline;
    private List<PlayerDTO> players = new ArrayList<>();
    public AssignmentDTO() {
    }
    public AssignmentDTO(Integer setId, String creator, Integer maxQuestions, Date deadline) {
        this.creator = creator;
        id = null;
        this.setId = setId;
        this.maxQuestions = maxQuestions;
        this.deadline = deadline;
        players = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getSetId() {
        return setId;
    }

    public void setSetId(Integer setId) {
        this.setId = setId;
    }

    public Integer getMaxQuestions() {
        return maxQuestions;
    }

    public void setMaxQuestions(Integer maxQuestions) {
        this.maxQuestions = maxQuestions;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public List<PlayerDTO> getPlayers() {
        return players;
    }

    public void addPlayer(PlayerDTO player) {
        players.add(player);
    }
    
    public void removePlayer(PlayerDTO player) {
        players.remove(player);
    }
    public void setPlayers(List<PlayerDTO> players) {
        this.players = players;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

}
