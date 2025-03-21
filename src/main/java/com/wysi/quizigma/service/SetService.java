package com.wysi.quizigma.service;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wysi.quizigma.DTO.OptionDTO;
import com.wysi.quizigma.DTO.QuestionDTO;
import com.wysi.quizigma.DTO.SetDTO;
import com.wysi.quizigma.model.Option;
import com.wysi.quizigma.model.Question;
import com.wysi.quizigma.model.Set;
import com.wysi.quizigma.model.User;
import com.wysi.quizigma.repository.SetRepository;
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

    public void createNewSet(SetDTO set, User owner) {
        String name = set.getName();
        String description = set.getDescription();
        String image = set.getImage();
        setRepository.save(new Set(name, description, image, owner, new ArrayList<>()));
    }

    public List<SetDTO> getSetsByOwner(Integer Id) {
        User owner = userRepository.findById(Id).orElse(null);
        List<Set> sets = setRepository.findByOwner(owner);
        List<SetDTO> setDTOs = new ArrayList<>();
        for(Set set : sets) {
            List<QuestionDTO> questions = new ArrayList<>();
            for(Question question : set.getQuestions()) {
                List<OptionDTO> options = new ArrayList<>();
                for(Option option : question.getOptions()) {
                    options.add(new OptionDTO(option.getId(),option.getOption(), option.getImage()));
                }
                questions.add(new QuestionDTO(question.getId(), question.getQuestion(),question.getType() ,question.getImage(), question.getSet().getId(), options, question.getAnswers()));
            }
            setDTOs.add(new SetDTO(set.getId(), set.getName(),owner.getUsername(), set.getDescription(), set.getImage(), questions));
        }
        return setDTOs;
    }

    public List<SetDTO> getRecentSets() {
        List<Set> sets = setRepository.findTop10ByOrderByIdDesc();
        List<SetDTO> setDTOs = new ArrayList<>();
        for(Set set : sets) {
            List<QuestionDTO> questions = new ArrayList<>();
            User owner = set.getOwner();
            for(Question question : set.getQuestions()) {
                List<OptionDTO> options = new ArrayList<>();
                for(Option option : question.getOptions()) {
                    options.add(new OptionDTO(option.getId(),option.getOption(), option.getImage()));
                }
                questions.add(new QuestionDTO(question.getId(), question.getQuestion(),question.getType() ,question.getImage(), question.getSet().getId(), options, question.getAnswers()));
            }
            setDTOs.add(new SetDTO(set.getId(), set.getName(),owner.getUsername(), set.getDescription(), set.getImage(), questions));
        }
        return setDTOs;
    }

    public List<SetDTO> getSetsByName(String name) {
        try {
            name = URLDecoder.decode(name, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("UTF-8 encoding is not supported", e);
        }
        List<Set> sets = setRepository.findTop10ByNameContainingIgnoreCase(name);
        List<SetDTO> setDTOs = new ArrayList<>();
        for(Set set : sets) {
            User owner = set.getOwner();
            List<QuestionDTO> questions = new ArrayList<>();
            for(Question question : set.getQuestions()) {
                List<OptionDTO> options = new ArrayList<>();
                for(Option option : question.getOptions()) {
                    options.add(new OptionDTO(option.getId(),option.getOption(), option.getImage()));
                }
                questions.add(new QuestionDTO(question.getId(), question.getQuestion(),question.getType() ,question.getImage(), question.getSet().getId(), options, question.getAnswers()));
            }
            setDTOs.add(new SetDTO(set.getId(), set.getName(),owner.getUsername(), set.getDescription(), set.getImage(), questions));
        }
        return setDTOs;
    }
    public void editSet(SetDTO set, User owner) {
        if(setRepository.findById(set.getId()).orElse(null).getOwner() != owner) {
            throw new IllegalArgumentException("You are not the owner of this set");
        }
        Set setToEdit = setRepository.findById(set.getId()).orElse(null);
        setToEdit.setName(set.getName());
        setToEdit.setDescription(set.getDescription());
        setToEdit.setImage(set.getImage());
        setRepository.save(setToEdit);
    }

    public void deleteSet(Integer setId, User owner) {

        if(setRepository.findById(setId).orElse(null).getOwner() != owner) {
            throw new IllegalArgumentException("You are not the owner of this set");
        }
        setRepository.delete(setRepository.findById(setId).orElse(null));
    }
}
