package com.wysi.quizigma.service;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

@Service
public class GameService {

    private final ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>> rooms = new ConcurrentHashMap<>();

    public GameService() {
    }

    public void addGame(String roomId) {
        rooms.put(roomId, new ConcurrentHashMap<>());
    }
    
    public void addPlayer(String roomId, String name) {
        if (!rooms.contains(roomId)) {
            throw new IllegalArgumentException("Room does not exist");
        }
        ConcurrentHashMap<String, Integer> room = rooms.get(roomId);
        if (room.contains(name)) {
            throw new IllegalArgumentException("Player already exists");
        }
        room.put(name, 0);
    }
}
