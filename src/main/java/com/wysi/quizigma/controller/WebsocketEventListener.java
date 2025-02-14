package com.wysi.quizigma.controller;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import com.wysi.quizigma.service.GameService;

@Component
public class WebsocketEventListener {

    private static final Logger logger = LoggerFactory.getLogger(WebsocketEventListener.class);

    private final GameService gameService;
    private final SimpMessagingTemplate messagingTemplate;

    public WebsocketEventListener(GameService gameService, SimpMessagingTemplate messagingTemplate) {
        this.gameService = gameService;
        this.messagingTemplate = messagingTemplate;
    }

    @EventListener
    public void handleSessionDisconnectEvent(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        @SuppressWarnings("null")
        String roomId = (String) headerAccessor.getSessionAttributes().get("room");
        @SuppressWarnings("null")
        String player = (String) headerAccessor.getSessionAttributes().get("player");
        if (player != null) {
            try {
                gameService.removePlayer(roomId, player);
                logger.info("Player " + player + " has left room " + roomId);
                HashMap<String, Object> response = new HashMap<>();
                response.put("type", "players");
                response.put("players", gameService.getPlayers(roomId));
                messagingTemplate.convertAndSend("/queue/creator/" + roomId, response);
                messagingTemplate.convertAndSend("/topic/player/" + roomId, response);
            } catch (IllegalArgumentException e) {
                logger.info("Player " + player + " does not exist in room " + roomId);
            }
        } else if (roomId != null) {
            try {
                logger.info("Room " + roomId + " has been removed");
                HashMap<String, Object> response = new HashMap<>();
                response.put("type", "end");
                response.put("players", gameService.getPlayers(roomId));
                messagingTemplate.convertAndSend("/topic/player/" + roomId, response);
                gameService.saveRoom(roomId);
                gameService.removeRoom(roomId);
            } catch (IllegalArgumentException e) {
                logger.info("Room " + roomId + " does not exist");
            }
        }
    }
}
