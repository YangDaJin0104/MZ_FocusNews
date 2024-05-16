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
    public boolean isCorrectAnswer(Map<Integer, String> userAnswers) {
        
        // 각 문제의 답을 비교하면서 정답인 경우 score를 증가시킴
        for (int id : userAnswers.keySet()) {
            String userAnswer = userAnswers.get(id);
            String correctAnswer = correctAnswers.get(id);

            if (userAnswer.equals(correctAnswer)) {
                System.out.println("정답입니다!!!");     // 테스트
                return true;
            }
        }

        return false;
    }
}
