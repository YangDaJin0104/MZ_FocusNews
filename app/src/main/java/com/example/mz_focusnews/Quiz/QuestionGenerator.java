package com.example.mz_focusnews.Quiz;

import android.content.Context;
import android.util.Log;

import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.Set;

public class QuestionGenerator {
    private static final String TAG = "QuestionGenerator";

    // 오늘의 퀴즈 생성 (구현 필요)
    public static Question generateQuestion() {
        // 임시 데이터
        int id = 123123;
        String question = "오늘의 퀴즈 문제입니다.";
        String correctAnswer = "정답";
        String option1 = "보기 1";
        String option2 = "보기 2";
        String option3 = "보기 3";
        String option4 = "정답";

        return new Question(id, question, correctAnswer, option1, option2, option3, option4);
    }

    // 오늘의 퀴즈를 제외한 나머지 4개의 문제 생성
    public static List<Question> generateQuestions(Context context, int count) {
        List<Question> questions = new ArrayList<>();

        Random random = new Random();

        CSVFileReader csvFileReader = new CSVFileReader();

        // CSV 파일을 읽어옴
        List<String[]> csvData = csvFileReader.readCSVFile(context, "quiz.csv");

        // 읽어온 데이터 출력
        int dataCount = 0;
        for (String[] line : csvData) {
            StringBuilder lineBuilder = new StringBuilder();
            for (String value : line) {
                lineBuilder.append(value).append(", ");
            }
            Log.d(TAG, "CSV Read: " + lineBuilder.toString());
            dataCount++;
        }

        // 중복된 문제 출제를 방지하기 위해 Set 사용
        Set<Integer> selectedIndices = new HashSet<>();

        // 4개의 문제가 List에 저장될 때까지 반복
        while (questions.size() < count) {
            // 문제 범위 내에서 랜덤한 문제ID 추출
            int randomIndex = random.nextInt(dataCount);
            
            // List 내에 해당 문제가 있는지 중복 체크
            if (!selectedIndices.contains(randomIndex)) {
                selectedIndices.add(randomIndex);

                // CSV 데이터에서 문제 정보 추출
                String[] questionData = csvData.get(randomIndex);

                // Question 객체 생성
                Question question = new Question(
                        Integer.parseInt(questionData[0]), // 문제 id
                        questionData[1], // 문제 내용
                        questionData[2], // 정답
                        questionData[3], // 보기1
                        questionData[4], // 보기2
                        questionData[5], // 보기3
                        questionData[6]  // 보기4
                );

                // 생성된 문제를 리스트에 추가
                questions.add(question);
            }
        }

        return questions;
    }
}
