package com.wysi.quizigma.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.wysi.quizigma.DTO.AnswerDTO;
import com.wysi.quizigma.DTO.PlayerDTO;
import com.wysi.quizigma.DTO.QuestionDTO;
import com.wysi.quizigma.service.AssignmentService;
import com.wysi.quizigma.service.GameService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;

@Controller
public class GameController {

    private static final Logger logger = LoggerFactory.getLogger(GameController.class);
    private final GameService gameService;
    private final AssignmentService assignmentService;
    private final SimpMessagingTemplate messagingTemplate;

    public GameController(GameService gameService, SimpMessagingTemplate messagingTemplate, AssignmentService assignmentService) {
        this.gameService = gameService;
        this.assignmentService = assignmentService;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/player/answer")
    public void answerQuestion(AnswerDTO answerDTO) {
        String roomId = answerDTO.getRoomId();
        logger.info("Player {} answered question: {} in room: {}", answerDTO.getPlayer(), answerDTO.getAnswer(), roomId);
        try {
            boolean correct = gameService.answerQuestion(roomId, answerDTO);
            HashMap<String, String> response = new HashMap<>();
            int score = gameService.getScore(roomId, answerDTO.getPlayer());
            if (correct) {
                response.put("type", "correct");
                response.put("score", Integer.toString(score));
                messagingTemplate.convertAndSend("/queue/" + roomId + "/" + answerDTO.getPlayer(), response);
            } else {
                response.put("type", "incorrect");
                response.put("score", Integer.toString(score));
                messagingTemplate.convertAndSend("/queue/" + roomId + "/" + answerDTO.getPlayer(), response);
            }
            HashMap<String, Object> playersResponse = new HashMap<>();
            playersResponse.put("type", "players");
            playersResponse.put("players", gameService.getPlayers(roomId));
            messagingTemplate.convertAndSend("/queue/creator/" + roomId, playersResponse);
        } catch (IllegalArgumentException e) {
            logger.error(e.getMessage());
        }
    }

    @MessageMapping("/creator/start")
    public void startGame(@RequestBody Map<String, String> body) {

        String roomId = body.get("room");
        List<QuestionDTO> questions = new ArrayList<>();
        HashMap<String, Object> response = new HashMap<>();
        HashMap<String, Object> questionsResponse = new HashMap<>();
        logger.info("Starting game in room: " + roomId);
        try {
            gameService.startGame(roomId);
            questions = gameService.getQuestions(roomId);
        } catch (IllegalArgumentException e) {
            logger.error("Error starting game in room: " + roomId);
        }
        response.put("type", "start");
        questionsResponse.put("type", "questions");
        questionsResponse.put("questions", questions);
        messagingTemplate.convertAndSend("/queue/creator/" + roomId, response);
        messagingTemplate.convertAndSend("/topic/player/" + roomId, response);
        messagingTemplate.convertAndSend("/topic/player/" + roomId, questionsResponse);
    }

    @MessageMapping("/creator/join")
    public void createRoom(@RequestBody Map<String, String> body) {
        String room = body.get("room");
        String creator = body.get("creator");
        HashMap<String, Object> response = new HashMap<>();
        HashMap<String, Object> playersReponse = new HashMap<>();
        logger.info("Creating room: {}", room);
        try {
            logger.info("Creator {} created room: {}", creator, room);
            gameService.checkOwnedRoom(room, creator);
            response.put("type", "joined");
            playersReponse.put("type", "players");
            playersReponse.put("players", gameService.getPlayers(room));
            messagingTemplate.convertAndSend("/queue/creator/" + room, response);
            messagingTemplate.convertAndSend("/queue/creator/" + room, playersReponse);
        } catch (IllegalArgumentException e) {
            logger.error(e.getMessage());
            response.put("type", "error");
            response.put("error", e.getMessage());
            messagingTemplate.convertAndSend("/queue/creator/" + room, response);
        }
    }

    @MessageMapping("/creator/end")
    public void endRoom(@RequestBody Map<String, String> body) {
        String room = body.get("room");
        logger.info("Ending room: {}", room);
        HashMap<String, Object> response = new HashMap<>();
        try {
            response.put("players", gameService.getPlayers(room));
            System.out.println(gameService.getPlayers(room).size());
            gameService.saveRoom(room);
            gameService.removeRoom(room);
        } catch (IllegalArgumentException e) {
            logger.error(e.getMessage());
        }
        response.put("type", "end");
        messagingTemplate.convertAndSend("/queue/creator/" + room, response);
        messagingTemplate.convertAndSend("/topic/player/" + room, response);
    }

    @MessageMapping("/player/join")
    public void playerJoin(@RequestBody Map<String, String> body) {
        String room = body.get("room");
        String player = body.get("player");
        HashMap<String, Object> response = new HashMap<>();
        HashMap<String, Object> playersReponse = new HashMap<>();
        try {
            gameService.addPlayer(room, player);
            logger.info("Player {} joined room: {}", player, room);
            if(gameService.isStarted(room)) {
                response.put("type", "questions");
                response.put("questions", gameService.getQuestions(room));
                messagingTemplate.convertAndSend("/queue/" + room + "/" + player, response);
                return;
            }
            List<PlayerDTO> players = gameService.getPlayers(body.get("room"));
            response.put("type", "joined");
            playersReponse.put("type", "players");
            playersReponse.put("players", players);
            messagingTemplate.convertAndSend("/queue/" + room + "/" + player, response);
            messagingTemplate.convertAndSend("/topic/player/" + room, playersReponse);
            messagingTemplate.convertAndSend("/queue/creator/" + room, playersReponse);
        } catch (IllegalArgumentException e) {
            response.put("type", "error");
            response.put("error", e.getMessage());
            messagingTemplate.convertAndSend("/queue/" + room + "/" + player, response);
            logger.error(e.getMessage());
        }
    }
    @MessageMapping("/player/assignment/join")
    public void playerJoinAssignment(@RequestBody Map<String, String> body) {
        String assignmentId = body.get("assignment");
        String player = body.get("player");
        HashMap<String, Object> response = new HashMap<>();
        try {
            logger.info("Player {} joined assignment: {}", player, assignmentId);
            assignmentService.addPlayer(assignmentId, player);
            response.put("type", "start");
            response.put("questions", assignmentService.getQuestions(assignmentId));
            messagingTemplate.convertAndSend("/queue/assignment/" + assignmentId + "/" + player, response);
        } catch (IllegalArgumentException e) {
            response.put("type", "error");
            response.put("error", e.getMessage());
            messagingTemplate.convertAndSend("/queue/assignment/" + assignmentId + "/" + player, response);
            logger.error(e.getMessage());
        }
    }
    @MessageMapping("/player/assignment/answer")
    public void answerAssignment(@RequestBody AnswerDTO answerDTO) {
        String assignmentId = answerDTO.getRoomId();
        String player = answerDTO.getPlayer();
        HashMap<String, Object> response = new HashMap<>();
        try {
            logger.info("Player {} answered assignment: {}", player, assignmentId);
            boolean correct = assignmentService.answerQuestion(assignmentId, answerDTO);
            if (correct) {
                response.put("type", "correct");
            } else {
                response.put("type", "wrong");
            }
            messagingTemplate.convertAndSend("/queue/assignment/" + assignmentId + "/" + player, response);
        } catch (IllegalArgumentException e) {
            response.put("type", "error");
            response.put("error", e.getMessage());
            messagingTemplate.convertAndSend("/queue/assignment/" + assignmentId + "/" + player, response);
            logger.error(e.getMessage());
        }
    }
}
