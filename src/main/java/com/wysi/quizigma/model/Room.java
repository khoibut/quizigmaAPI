package com.wysi.quizigma.model;

import java.util.List;

import jakarta.persistence.CascadeType;
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
@Table(name = "rooms")
public class Room {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer roomId;

    @ManyToOne
    @JoinColumn(name = "set_id", nullable = false)
    private Set set;
    
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "host_id")
    private User host;

    @Column(name = "time_limit", nullable = false)
    private Integer timeLimit;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL)
    private List<Player> players;

    public Room() {
    }

    public Room(Set set, User host, Integer timeLimit, List<Player> players) {
        this.set = set;
        this.host = host;
        this.timeLimit = timeLimit;
        this.players = players;
    }

    public Integer getRoomId() {
        return roomId;
    }

    public Set getSet() {
        return set;
    }

    public void setSet(Set set) {
        this.set = set;
    }

    public User getHost() {
        return host;
    }

    public void setHost(User host) {
        this.host = host;
    }

    public Integer getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(Integer timeLimit) {
        this.timeLimit = timeLimit;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public void addPlayer(Player player) {
        this.players.add(player);
    }

    public void removePlayer(Player player) {
        this.players.remove(player);
    }

    public void clearPlayers() {
        this.players.clear();
    }

}
