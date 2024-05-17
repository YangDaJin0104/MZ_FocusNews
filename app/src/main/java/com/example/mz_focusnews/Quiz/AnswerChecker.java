package com.example.mz_focusnews.Quiz;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnswerChecker {
    private Map<Integer, String> correctAnswers;

    public AnswerChecker(Question quizQuestion) {
        correctAnswers = new HashMap<>();   // 해시맵을 이용해 출제된 문제ID와 정답 저장
        correctAnswers.put(quizQuestion.getId(), quizQuestion.getCorrectAnswer());
    }

    public AnswerChecker(List<Question> quizQuestions) {
        correctAnswers = new HashMap<>();   // 해시맵을 이용해 출제된 문제ID와 정답 저장

        for (Question question : quizQuestions) {
            correctAnswers.put(question.getId(), question.getCorrectAnswer());
        }
    }

    // 사용자가 입력한 답을 받아 정답과 비교하여 점수를 계산합니다.
    public boolean isCorrectAnswer(String userAnswer, Question question) {
        String correctAnswer = question.getCorrectAnswer();

        if(userAnswer.equals(correctAnswer)){
            System.out.println("정답입니다!!!");     // 테스트
            return true;
        } else{
            System.out.println("틀렸습니다!");     // 테스트
            return false;
        }
    }
}
