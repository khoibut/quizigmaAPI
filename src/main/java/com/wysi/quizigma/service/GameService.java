package com.wysi.quizigma.service;

import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.wysi.quizigma.DTO.AnswerDTO;
import com.wysi.quizigma.DTO.OptionDTO;
import com.wysi.quizigma.DTO.PlayerDTO;
import com.wysi.quizigma.DTO.QuestionDTO;
import com.wysi.quizigma.DTO.RoomDTO;

@Service
public class GameService {

    private final ConcurrentHashMap<String, RoomDTO> rooms = new ConcurrentHashMap<>();
    private final UserService userService;
    private final QuestionService questionService;
    private final RoomService roomService;
    private final SimpMessagingTemplate messagingTemplate;
    private String generatedRoomId() {
        Random random = new Random();
        while (true) {
            String roomId = String.valueOf(random.nextInt(999999));
            if (!rooms.containsKey(roomId)) {
                return roomId;
            }
        }
    }

    public GameService(UserService userService, QuestionService questionService, RoomService roomservice, SimpMessagingTemplate messagingTemplate) {
        this.userService = userService;
        this.questionService = questionService;
        this.roomService = roomservice;
        this.messagingTemplate = messagingTemplate;
    }

    public String addRoom(RoomDTO roomDTO, String token) {
        try {
            String roomId = generatedRoomId();
            roomDTO.setId(roomId);
            roomDTO.setCreator(userService.getUser(token).getUsername());
            rooms.put(roomId, roomDTO);
            return roomId;
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Token is invalid");
        }
    }

    @Scheduled(fixedRate = 1000) // Runs every second
    public void updateTimers() {
        rooms.forEach((roomId, room) -> {
            if (room.isStarted() && room.getTimeLimit() > 0) {
                room.setTimeLimit(room.getTimeLimit() - 1);
                HashMap<String, Object> response = new HashMap<>();
                response.put("type", "timer");
                response.put("time", room.getTimeLimit());
                messagingTemplate.convertAndSend("/queue/creator/" + roomId, response);
                messagingTemplate.convertAndSend("/topic/player/" + roomId, response);
            } else if (room.getTimeLimit() == 0) {
                HashMap<String, Object> response = new HashMap<>();
                response.put("players", room.getPlayers());
                response.put("type", "end");
                System.out.println(room.getPlayers().size());
                saveRoom(roomId);
                removeRoom(roomId);
                messagingTemplate.convertAndSend("/topic/player/" + roomId, response);
                messagingTemplate.convertAndSend("/queue/creator/" + roomId, response);
            }
        });
    }

    public void checkRoom(String roomId) {
        if (!rooms.containsKey(roomId)) {
            throw new IllegalArgumentException("Room does not exist");
        }
    }

    public boolean isStarted(String roomId) {
        return rooms.get(roomId).isStarted();
    }

    public void checkOwnedRoom(String roomId, String token) {
        try {
            checkRoom(roomId);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Room does not exist");
        }
        if (!rooms.get(roomId).getCreator().equals(userService.getUser(token).getUsername())) {
            throw new IllegalArgumentException("User does not own room");
        }
    }

    public void saveRoom(String RoomId) {
        RoomDTO room = rooms.get(RoomId);
        roomService.saveRoom(room);

    }

    public void addPlayer(String roomId, String playerName) {
        RoomDTO room = rooms.get(roomId);
        if (!rooms.containsKey(roomId)) {
            throw new IllegalArgumentException("Room " + roomId + " does not exist");
        }
        if (room.isStarted() && !room.isLateJoin()) {
            throw new IllegalArgumentException("Game has already started");
        }
        for (PlayerDTO player : rooms.get(roomId).getPlayers()) {
            if (player.getName().equals(playerName)) {
                throw new IllegalArgumentException("Player already exists");
            }
        }
        PlayerDTO playerDTO = new PlayerDTO(playerName);
        rooms.get(roomId).addPlayer(playerDTO);
    }

    public List<PlayerDTO> getPlayers(String roomId) {
        if (!rooms.containsKey(roomId)) {
            throw new IllegalArgumentException("Room does not exist");
        }
        List<PlayerDTO> players = rooms.get(roomId).getPlayers();
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

    @Transactional
    public List<QuestionDTO> getQuestions(String roomId) {
        if (!rooms.containsKey(roomId)) {
            throw new IllegalArgumentException("Room does not exist");
        }
        RoomDTO room = rooms.get(roomId);
        List<QuestionDTO> questions = questionService.getQuestionsBySet(room.getSetId());
        for (QuestionDTO question : questions) {
            if ("MCQ".equals(question.getType())) {
                question.setAnswers(null);
            } else {
                question.setOptions(null);
            }
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
                if ("MCQ".equals(question.getType())) {
                    if (question.getAnswers().contains(Integer.valueOf(answerDTO.getAnswer()))) {
                        result = true;
                    }
                } else {
                    for (OptionDTO option : question.getOptions()) {
                        if (option.getOption().equals(answerDTO.getAnswer())) {
                            result = true;
                        }
                    }
                }
            }
        }
        PlayerDTO player = null;
        for (PlayerDTO p : room.getPlayers()) {
            if (p.getName().equals(answerDTO.getPlayer())) {
                player = p;
            }
        }
        if (player != null) {
            if (result) {
                player.incrementScore();
                player.incrementCorrect();
            } else {
                player.incrementIncorrect();
            }
        } else {
            throw new IllegalArgumentException("Player does not exist");
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
