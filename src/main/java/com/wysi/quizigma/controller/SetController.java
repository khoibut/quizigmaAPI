package com.wysi.quizigma.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.wysi.quizigma.DTO.SetDTO;
import com.wysi.quizigma.service.SetService;
import com.wysi.quizigma.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;



@RestController
@RequestMapping("/api/v1/set")
public class SetController {
    private static final Logger logger = LoggerFactory.getLogger(SetController.class);
    private final SetService setService;
    private final UserService userService;

    public SetController(SetService setService, UserService userService) {
        this.setService = setService;
        this.userService = userService;
    }
    @Operation(summary = "Create a new set", description = "Create a new set, make sure questions is empty", requestBody=@io.swagger.v3.oas.annotations.parameters.RequestBody(
        content=@Content(
            mediaType="application/json",
            schema=@Schema(implementation=SetDTO.class),
            examples=@ExampleObject(value="{\"name\":\"Set 1\",\"description\":\"This is a set\",\"image\":\"base64\",\"questions\":[]}")
        )
    ))
    @ApiResponse(responseCode = "201", description = "Set created")
    @PostMapping("")
    public ResponseEntity<Object> createSet(
        @Parameter(description = "Set name, description, image of the set. Make sure to keep the question field empty as there are no question yet.", required = true)
        @RequestBody SetDTO newSet,@RequestHeader("Authorization") String token
        ) {
        setService.createNewSet(newSet, userService.getUser(token));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/discovery")
    public ResponseEntity<List<SetDTO>> getRecentSets() {
        return new ResponseEntity<>(setService.getRecentSets(), HttpStatus.OK);
    }    

    @GetMapping("/search")
    public ResponseEntity<List<SetDTO>> searchSets(@RequestParam String searchBar) {
        return new ResponseEntity<>(setService.getSetsByName(searchBar), HttpStatus.OK);
    }
    @Operation(summary = "Get all sets", description = "Get all sets created by the user")
    @ApiResponse(responseCode = "200", description = "Sets returned")
    @GetMapping("")
    public ResponseEntity<List<SetDTO>> getSets(
        @Parameter(description = "Token for userId", required = true)
        @RequestHeader("Authorization") String token) {
        return new ResponseEntity<>(setService.getSetsByOwner(userService.getUser(token).getId()), HttpStatus.OK);
    }
    
    @Operation(summary = "Delete a set", description = "Delete a set by id")
    @ApiResponse(responseCode = "200", description = "Set deleted")
    @ApiResponse(responseCode = "400", description = "Bad request, user is not the owner", content = @Content(
        mediaType = "application/json",
        schema = @Schema(type="object", example = "{\"error\":\"You are not the owner of this set\"}")
    ))
    @DeleteMapping("")
    public ResponseEntity<Object> deleteSet(
        @Parameter(description = "Set id", required = true)
        @RequestParam Integer id, 
        @Parameter(description = "Token for userId", required = true)
        @RequestHeader("Authorization") String token) {
        try{
            setService.deleteSet(id, userService.getUser(token));
        } catch (IllegalArgumentException e) {
            logger.error("Error deleting set id {}, by user id P{} ", id, userService.getUser(token).getId());
            return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "Update a set by setId", description = "Update a set, the questions won't be updated so it doesn't need to be sent",requestBody=@io.swagger.v3.oas.annotations.parameters.RequestBody(
        content=@Content(
            mediaType="application/json",
            schema=@Schema(implementation=SetDTO.class),
            examples=@ExampleObject(value="{\"id\":1,\"name\":\"Set 1\",\"description\":\"This is a set\",\"image\":\"base64\",\"questions\":[]}")
        )
    ))
    @ApiResponse(responseCode = "200", description = "Set updated")
    @ApiResponse(responseCode = "400", description = "Bad request, user is not the owner", content = @Content(
        mediaType = "application/json",
        schema = @Schema(type="object", example = "{\"error\":\"You are not the owner of this set\"}")
    ))
    @PatchMapping("")
    public ResponseEntity<Object> updateSet(@RequestBody SetDTO set, 
        @Parameter(description = "Token for userId", required = true)
    @RequestHeader("Authorization") String token) {
        try{
            setService.editSet(set, userService.getUser(token));
        } catch (IllegalArgumentException e) {
            logger.error("Error updating set id {}, by user id P{} ", set.getId(), userService.getUser(token).getId());
            return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
