package com.example.mz_focusnews.Quiz;

public class Question {
    private int id;                     // 문제 아이디
    private int level;                  // 문제 레벨 (1~4)
    private String question;            // 문제
    private String correctAnswer;      // 정답
    private String option1;             // 보기1
    private String option2;             // 보기2
    private String option3;             // 보기3
    private String option4;             // 보기4

    public Question(int id, int level, String question, String correctAnswer, String option1, String option2, String option3, String option4) {
        this.id = id;
        this.level = level;
        this.question = question;
        this.correctAnswer = correctAnswer;
        this.option1 = option1;
        this.option2 = option2;
        this.option3 = option3;
        this.option4 = option4;
    }

    public int getId() {
        return id;
    }
    public int getLevel() {
        return level;
    }

    public String getQuestion() {
        return question;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public String getOption1() {
        return option1;
    }

    public String getOption2() {
        return option2;
    }

    public String getOption3() {
        return option3;
    }

    public String getOption4() {
        return option4;
    }
}
