package com.wysi.quizigma.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.wysi.quizigma.DTO.AnswerDTO;
import com.wysi.quizigma.DTO.QuestionDTO;
import com.wysi.quizigma.service.GameService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;


@Controller
public class GameController {
    private static final Logger logger = LoggerFactory.getLogger(GameController.class);
    private final GameService gameService;
    private final SimpMessagingTemplate messagingTemplate;

    public GameController(GameService gameService, SimpMessagingTemplate messagingTemplate) {
        this.gameService = gameService;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/player/{roomId}/answer")
    public void answerQuestion(AnswerDTO answerDTO) {
        String roomId = answerDTO.getRoomId();
        logger.info("Player {} answered question: {} in room: {}", answerDTO.getUsername(), answerDTO.getAnswer(), roomId);
        boolean correct = gameService.answerQuestion(roomId,answerDTO);
        int score = gameService.getScore(roomId, answerDTO.getUsername());
        if (correct) {
            messagingTemplate.convertAndSend("/queue/player/" + answerDTO.getUsername(), "Correct answer, your score is: " + score);
        } else {
            messagingTemplate.convertAndSend("/queue/player/" + answerDTO.getUsername(), "Incorrect answer, your score is: " + score);
        }
    }

    @MessageMapping("/creator/{roomId}/start")
    public void startGame(@RequestBody Map<String,String> body) {
        String roomId = body.get("room");
        List<QuestionDTO> questions = new ArrayList<>();
        logger.info("Starting game in room: " + roomId);
        try {
            gameService.startGame(roomId);
            questions = gameService.getQuestions(roomId);
        } catch (IllegalArgumentException e) {
            logger.error("Error starting game in room: " + roomId);
        }
        messagingTemplate.convertAndSend("/topic/creator/room/" + roomId, "Game started");
        messagingTemplate.convertAndSend("/topic/player/room/" + roomId, "Game started");
        messagingTemplate.convertAndSend("/topic/player/room/" + roomId, questions);
    }

    @MessageMapping("/player/{roomId}/ready")
    public void playerReady(@RequestBody Map<String, String> body) {
        try{
            logger.info("Player {} is ready in room: {}", body.get("player"), body.get("room"));
            List<String> players = gameService.getPlayers(body.get("room"));
            messagingTemplate.convertAndSend("/topic/player/room/" + body.get("room"), players);
            messagingTemplate.convertAndSend("/topic/creator/room/" + body.get("room"), players);
        }catch (IllegalArgumentException e){
            logger.error("Error getting players in room: " + body.get("room"));
        }
    }
}
