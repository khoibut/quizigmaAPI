package com.wysi.quizigma.DTO;

public class RoomDTO {
    private Integer Id;
    private Integer setId;
    private Integer timeLimit;

    public RoomDTO() {
    }

    public RoomDTO(Integer id, Integer setId, Integer timeLimit) {
        Id = id;
        this.setId = setId;
        this.timeLimit = timeLimit;
    }

    public Integer getId() {
        return Id;
    }

    public void setId(Integer id) {
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

}
