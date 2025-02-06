package com.wysi.quizigma.model;

import java.util.List;

public class QuizizzDTO {
    private QuizData data;

    public static class QuizData{
        private Quiz quiz;
        public static class Quiz{
            private Info info;

            public static class Info{
                private List<Question> questions;

                public static class Question{
                    
                }
            }
        }
    }
}
