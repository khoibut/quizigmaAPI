package com.wysi.quizigma.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.wysi.quizigma.DTO.AnswerDTO;
import com.wysi.quizigma.DTO.PlayerDTO;
import com.wysi.quizigma.DTO.QuestionDTO;
import com.wysi.quizigma.DTO.RoomDTO;

import jakarta.transaction.Transactional;

@Service
public class GameService {

    private final ConcurrentHashMap<String, RoomDTO> rooms = new ConcurrentHashMap<>();
    private final UserService userService;
    private final RoomService roomService;
    private final QuestionService questionService;

    private String generatedRoomId() {
        Random random = new Random();
        while (true) {
            String roomId = String.valueOf(random.nextInt(999999));
            if (!rooms.containsKey(roomId)) {
                return roomId;
            }
        }
    }

    public GameService(UserService userService, RoomService roomService, QuestionService questionService) {
        this.userService = userService;
        this.roomService = roomService;
        this.questionService = questionService;
    }

    public String addRoom(RoomDTO roomDTO) {
        System.out.println("Adding room: " + roomDTO.getSetId());
        String roomId = generatedRoomId();
        roomDTO.setId(roomId);
        rooms.put(roomId, roomDTO);
        for (String key : rooms.keySet()) {
            System.out.println("Room: " + key);
        }
        return roomId;
    }

    public void checkRoom(String roomId) {
        if (!rooms.containsKey(roomId)) {
            throw new IllegalArgumentException("Room does not exist");
        }
    }

    public void addPlayer(String roomId, String playerName) {
        System.out.println("Adding player: " + playerName + " to room: " + roomId);
        if (!rooms.containsKey(roomId)) {
            throw new IllegalArgumentException("Room does not exist");
        }
        for (PlayerDTO player : rooms.get(roomId).getPlayers()) {
            if (player.getName().equals(playerName)) {
                throw new IllegalArgumentException("Player already exists");
            }
        }
        PlayerDTO playerDTO = new PlayerDTO(playerName);
        rooms.get(roomId).addPlayer(playerDTO);
    }

    public List<String> getPlayers(String roomId) {
        if (!rooms.containsKey(roomId)) {
            throw new IllegalArgumentException("Room does not exist");
        }
        List<String> players = new ArrayList<>();
        for (PlayerDTO player : rooms.get(roomId).getPlayers()) {
            players.add(player.getName());
        }
        return players;
    }

    public void startGame(String roomId) {
        if (!rooms.containsKey(roomId)) {
            throw new IllegalArgumentException("Room does not exist");
        }
        rooms.get(roomId).setStarted(true);
    }

    public void removePlayer(String roomId, String playerName) {
        if (!rooms.containsKey(roomId)) {
            throw new IllegalArgumentException("Room does not exist");
        }
        PlayerDTO playerToRemove = null;
        for (PlayerDTO player : rooms.get(roomId).getPlayers()) {
            if (player.getName().equals(playerName)) {
                playerToRemove = player;
            }
        }
        if (playerToRemove == null) {
            throw new IllegalArgumentException("Player does not exist");
        }
        rooms.get(roomId).removePlayer(playerToRemove);
    }

    public void removeRoom(String roomId) {
        if (!rooms.containsKey(roomId)) {
            throw new IllegalArgumentException("Room does not exist");
        }
        rooms.remove(roomId);
    }

    public List<QuestionDTO> getQuestions(String roomId) {
        if (!rooms.containsKey(roomId)) {
            throw new IllegalArgumentException("Room does not exist");
        }
        RoomDTO room = rooms.get(roomId);
        List<QuestionDTO> questions = questionService.getQuestionsBySet(room.getSetId());
        for (QuestionDTO question : questions) {
            question.setAnswers(null);
        }
        return questions;
    }

    @Transactional
    public boolean answerQuestion(String roomId, AnswerDTO answerDTO) {
        boolean result = false;
        if (!rooms.containsKey(roomId)) {
            throw new IllegalArgumentException("Room does not exist");
        }
        RoomDTO room = rooms.get(roomId);
        if (!room.isStarted()) {
            throw new IllegalArgumentException("Game has not started");
        }
        for (QuestionDTO question : questionService.getQuestionsBySet(room.getSetId())) {
            if (question.getId().equals(answerDTO.getQuestionId())) {
                if (question.getAnswers().contains(Integer.valueOf(answerDTO.getAnswer()))) {
                    result = true;
                }
            }
            PlayerDTO player = null;
            for (PlayerDTO p : room.getPlayers()) {
                if (p.getName().equals(answerDTO.getUsername())) {
                    player = p;
                }
            }
            if (result) {
                player.incrementScore();
                player.incrementCorrect();
            }else{
                player.incrementIncorrect();
            }
        }
        return result;
    }

    public Integer getScore(String roomId, String playerName) {
        if (!rooms.containsKey(roomId)) {
            throw new IllegalArgumentException("Room does not exist");
        }
        RoomDTO room = rooms.get(roomId);
        for (PlayerDTO player : room.getPlayers()) {
            if (player.getName().equals(playerName)) {
                return player.getScore();
            }
        }
        throw new IllegalArgumentException("Player does not exist");
    }
}
