package com.wysi.quizigma.controller;

import java.util.List;

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



@RestController
@RequestMapping("/api/set")
public class SetController {

    private final SetService setService;
    private final UserService userService;

    public SetController(SetService setService, UserService userService) {
        this.setService = setService;
        this.userService = userService;
    }

    @PostMapping("")
    public ResponseEntity<Object> createSet(@RequestBody SetDTO newSet,@RequestHeader("Authorization") String token) {
        setService.createNewSet(newSet, userService.getUser(token));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("")
    public ResponseEntity<List<SetDTO>> getSets(@RequestHeader("Authorization") String token) {
        
        return new ResponseEntity<>(setService.getSetsByOwner(userService.getUser(token).getId()), HttpStatus.OK);
    }
    
    @DeleteMapping("")
    public ResponseEntity<Object> deleteSet(@RequestParam Integer id, @RequestHeader("Authorization") String token) {
        try{
            setService.deleteSet(id, userService.getUser(token));
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PatchMapping("")
    public ResponseEntity<Object> updateSet(@RequestBody SetDTO set, @RequestHeader("Authorization") String token) {
        try{
            setService.editSet(set, userService.getUser(token));
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
