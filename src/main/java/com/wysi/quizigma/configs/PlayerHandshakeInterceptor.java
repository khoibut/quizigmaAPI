package com.wysi.quizigma.configs;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import com.wysi.quizigma.service.GameService;

public class PlayerHandshakeInterceptor implements HandshakeInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(PlayerHandshakeInterceptor.class);
    private final GameService gameService;

    public PlayerHandshakeInterceptor(GameService gameService) {
        this.gameService = gameService;
    }

    @Override
    public boolean beforeHandshake(@NonNull ServerHttpRequest request, @NonNull ServerHttpResponse response, @NonNull WebSocketHandler wsHandler,
            @NonNull Map<String, Object> attributes) throws Exception {
            String query = request.getURI().getQuery();
            for (String param : query.split("&")) {
                String[] pair = param.split("=");
                if (pair[0].equals("room")) {
                    String roomId = pair[1];
                    System.out.println("Room ID: " + roomId);
                    if (pair[1].length() > 1) {
                        attributes.put("room", roomId);
                    }else{
                        return false;
                    }
                }else if(pair[0].equals("player")) {
                    String player = pair[1];
                    System.out.println("Player: " + player);
                    if (pair[1].length() > 1) {
                        attributes.put("player", player);
                    }else{
                        return false;
                    }
                }
            }
        return true;
    }

    @Override
    public void afterHandshake(@NonNull ServerHttpRequest request, @NonNull ServerHttpResponse response, @NonNull WebSocketHandler wsHandler,
            @Nullable Exception exception) {
        // No implementation needed
    }
}
