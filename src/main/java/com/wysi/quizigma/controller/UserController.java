package com.wysi.quizigma.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wysi.quizigma.DTO.UserDTO;
import com.wysi.quizigma.security.JwtUtil;
import com.wysi.quizigma.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@RestController
@RequestMapping("/api/v1/acc")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
    }

    @Operation(summary = "Authenticate user", description = "Authenticate user with email and password")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User authenticated, token returned", content = @Content(
                mediaType = "application/json",
                schema = @Schema(type="object", example = "{\"token\":\"your_token_here\"}")
        )),
        @ApiResponse(responseCode = "400", description = "Bad request, invalid input data", content = @Content(
                mediaType = "application/json",
                schema = @Schema(type="object", example = "{\"error\":\"Invalid input\"}")
        )),
        @ApiResponse(responseCode = "401", description = "Unauthorized, invalid email or password", content = @Content(
                mediaType = "application/json",
                schema = @Schema(type="object", example = "{\"error\":\"Invalid email or password\"}")
        ))
    })
    @PostMapping("/auth")
    public ResponseEntity<Map<String, Object>> login(
            @Parameter(description = "User email and password", required = true)
            @RequestBody UserDTO user) {
        Map<String, Object> response = new HashMap<>();
        try {
            String token = userService.authenticate(user.getEmail(), user.getPassword());
            response.put("token", token);
        } catch (IllegalArgumentException e) {
            if (e.getMessage().equals("Invalid email or password")) {
                response.put("error", e.getMessage());
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            } else {
                response.put("error", e.getMessage());
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

        @Operation(summary = "Add new user", description = "Add new user with username, email and password")
        @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created, token returned", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(type="object", example = "{\"token\":\"your_token_here\"}")
            )),
            @ApiResponse(responseCode = "400", description = "Bad request, invalid input data", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(type="object", example = "{\"error\":\"Invalid input\"}")
            )),
            @ApiResponse(responseCode = "409", description = "Conflict, username or email already exists", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(type="object", example = "{\"error\":\"Username or email already exists\"}")
            ))
        })
    @PostMapping("")
    public ResponseEntity<Map<String, Object>> addNewUser(
            @Parameter(description = "User username, email and password", required = true)
            @RequestBody UserDTO user) {
        try {
            String token = userService.addNewUser(user);
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            if (e.getMessage().equals("Email already exists") || e.getMessage().equals("Username already exists")) {
                response.put("error", e.getMessage());
                return new ResponseEntity<>(response, HttpStatus.CONFLICT);
            } else {
                response.put("error", e.getMessage());
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
        }
    }

    @Operation(summary = "Update user", description = "Update user with new username, email or password")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User updated, new token returned", content = @Content(
                mediaType = "application/json",
                schema = @Schema(type="object", example = "{\"token\":\"your_new_token_here\"}")
        )),
        @ApiResponse(responseCode = "400", description = "Bad request, invalid input data", content = @Content(
                mediaType = "application/json",
                schema = @Schema(type="object", example = "{\"error\":\"Invalid input\"}")
        ))
    })
    @PatchMapping("")
    public ResponseEntity<Map<String, Object>> updateUser(
            @Parameter(description = "User username, email and password", required = false)
            @RequestBody UserDTO user, @RequestHeader("Authorization") String token
    ) {
        try {
            String newToken = userService.editUser(user, token);
            Map<String, Object> response = new HashMap<>();
            response.put("token", newToken);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }
}
