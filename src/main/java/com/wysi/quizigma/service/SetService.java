package com.wysi.quizigma.service;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;

import com.wysi.quizigma.repository.SetRepository;

import java.util.List;

import com.wysi.quizigma.model.Question;

import org.springframework.stereotype.Service;

import com.wysi.quizigma.DTO.OptionDTO;
import com.wysi.quizigma.DTO.SetDTO;
import com.wysi.quizigma.model.User;
import com.wysi.quizigma.DTO.QuestionDTO;
import com.wysi.quizigma.model.Option;
import com.wysi.quizigma.repository.UserRepository;

@Service
public class SetService {

    @Autowired
    private final SetRepository setRepository;
    private final UserRepository userRepository;

    public SetService(SetRepository setRepository, UserRepository userRepository) {
        this.setRepository = setRepository;
        this.userRepository = userRepository;
    }

    public void deleteSet(Integer id) {
        setRepository.deleteById(id);
    }

    public void createNewSet(SetDTO set) {
        String name = set.getName();
        String description = set.getDescription();
        byte[] image = set.getImage();
        User owner = userRepository.findByUsername(set.getOwner().getUsername());
        List <Question> questions = new ArrayList<Question>();
        List <Option> options = new ArrayList<Option>();

    }
}
