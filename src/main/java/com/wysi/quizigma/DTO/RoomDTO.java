package com.wysi.quizigma.DTO;

import java.util.ArrayList;
import java.util.List;

public class RoomDTO {
    private String Id;
    private Integer setId;
    private Integer timeLimit;
    private boolean isStarted = false;
    private List<PlayerDTO> players = new ArrayList<>();
    public RoomDTO() {
    }
    public RoomDTO(Integer setId, Integer timeLimit) {
        isStarted = false;
        Id = null;
        this.setId = setId;
        this.timeLimit = timeLimit;
        players = new ArrayList<>();
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public Integer getSetId() {
        return setId;
    }

    public void setSetId(Integer setId) {
        this.setId = setId;
    }

    public Integer getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(Integer timeLimit) {
        this.timeLimit = timeLimit;
    }

    public List<PlayerDTO> getPlayers() {
        return players;
    }

    public void setPlayers(List<PlayerDTO> players) {
        this.players = players;
    }

    public void addPlayer(PlayerDTO player) {
        players.add(player);
    }

    public void removePlayer(PlayerDTO player) {
        players.remove(player);
    }

    public boolean isStarted() {
        return isStarted;
    }

    public void setStarted(boolean started) {
        isStarted = started;
    }

}
