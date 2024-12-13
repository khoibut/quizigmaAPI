package com.wysi.quizigma.service;

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
        List<Question> questions = new ArrayList<>();
        for (QuestionDTO question : set.getQuestions()) {
            List<Option> options = new ArrayList<>();
            for (OptionDTO option : question.getOptions()) {
                options.add(new Option(option.getOption(), option.getImage()));
            }
            questions.add(new Question(
                    question.getQuestion(),
                    question.getImage(),
                    setRepository.findById(question.getSetId()).orElse(null),
                    options,
                    question.getAnswers()
            ));
        }
        setRepository.save(new Set(name, description, image, owner, questions));
    }

    public List<SetDTO> getSetsByOwner(Integer Id) {
        User owner = userRepository.findById(Id).orElse(null);
        List<Set> sets = setRepository.findByOwner(owner);
        List<SetDTO> setDTOs = new ArrayList<>();
        for (Set set : sets) {
            List<QuestionDTO> questionDTOs = new ArrayList<>();
            for (Question question : set.getQuestions()) {
                List<OptionDTO> optionDTOs = new ArrayList<>();
                for (Option option : question.getOptions()) {
                    optionDTOs.add(new OptionDTO(option.getOption(), option.getImage()));
                }
                questionDTOs.add(new QuestionDTO(
                        question.getId(),
                        question.getQuestion(),
                        question.getImage(),
                        question.getSet().getId(),
                        optionDTOs,
                        question.getAnswers()
                ));
            }
            setDTOs.add(new SetDTO(
                    set.getId(),
                    set.getName(),
                    set.getDescription(),
                    set.getImage(),
                    questionDTOs
            ));
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

    public void deleteSet(SetDTO set, User owner) {
        if(setRepository.findById(set.getId()).orElse(null).getOwner() != owner) {
            throw new IllegalArgumentException("You are not the owner of this set");
        }
        setRepository.delete(setRepository.findById(set.getId()).orElse(null));
    }
}
