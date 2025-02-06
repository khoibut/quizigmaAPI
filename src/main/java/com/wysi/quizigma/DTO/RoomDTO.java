package com.wysi.quizigma.DTO;

import java.util.ArrayList;
import java.util.List;

public class RoomDTO {
    private String id;
    private String creator;
    private Integer setId;
    private Integer timeLimit;
    private boolean isStarted = false;
    private boolean lateJoin;
    private List<PlayerDTO> players = new ArrayList<>();
    public RoomDTO() {
    }
    public RoomDTO(Integer setId, String creator, Integer timeLimit, boolean lateJoin) {
        isStarted = false;
        this.creator = creator;
        id = null;
        this.setId = setId;
        this.timeLimit = timeLimit;
        this.lateJoin = lateJoin;
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

    public Integer getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(Integer timeLimit) {
        this.timeLimit = timeLimit;
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

    public boolean isLateJoin() {
        return lateJoin;
    }

}
