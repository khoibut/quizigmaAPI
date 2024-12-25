package com.wysi.quizigma.configs;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import com.wysi.quizigma.service.GameService;

public class UserHandshakeInterceptor implements HandshakeInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(UserHandshakeInterceptor.class);
    private final GameService gameService;

    public UserHandshakeInterceptor(GameService gameService) {
        this.gameService = gameService;
    }

    @Override
    public boolean beforeHandshake(@NonNull ServerHttpRequest request, @NonNull ServerHttpResponse response, @NonNull WebSocketHandler wsHandler,
            @NonNull Map<String, Object> attributes) throws Exception {
        List<String> playerNameHeaders = request.getHeaders().get("Player");
        List<String> roomIdHeaders = request.getHeaders().get("Room");

        logger.info("Incoming WebSocket handshake request: {}", request.getURI());
        logger.info("Headers: {}", request.getHeaders());

        if (playerNameHeaders == null || playerNameHeaders.isEmpty()) {
            response.setStatusCode(HttpStatus.BAD_REQUEST);
            response.getHeaders().add("error", "Player name missing");
            logger.error("Player name missing in WebSocket handshake request");
            return false;
        }
        if (roomIdHeaders == null || roomIdHeaders.isEmpty()) {
            response.setStatusCode(HttpStatus.BAD_REQUEST);
            response.getHeaders().add("error", "Room ID missing");
            logger.error("Room ID missing in WebSocket handshake request");
            return false;
        }
        String roomId = roomIdHeaders.get(0);
        String playerName = playerNameHeaders.get(0);
        logger.info("Attempting WebSocket handshake for roomId: {}", roomId);
        try {
            logger.info("Player {} added to room {}", playerName, roomId);
            return true;
        } catch (IllegalArgumentException e) {
            response.setStatusCode(HttpStatus.BAD_REQUEST);
            response.getHeaders().add("error", e.getMessage());
            logger.error("Error adding player to room: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
            Exception exception) {
        // No implementation needed
    }
}
