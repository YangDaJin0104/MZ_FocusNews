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
    public static List<Question> generateQuestions(Context context, String quizFileName, String quizSolvedFileName, int userId, int count) {
        List<Question> questions = new ArrayList<>();

        Random random = new Random();

        CSVFileReader csvFileReader = new CSVFileReader();
        CSVFileWriter csvFileWriter = new CSVFileWriter();

        // quiz.csv 파일을 읽어옴
        List<String[]> csvData = csvFileReader.readQuizCSVFile(context, quizFileName);

        // 읽어온 데이터 출력 (테스트용)
        for (int i = 1; i < csvData.size(); i++) { // 첫 번째 줄은 무시하고 두 번째 줄부터 시작 (BOM 문제 때문)
            String[] line = csvData.get(i);
            StringBuilder lineBuilder = new StringBuilder();
            for (String value : line) {
                lineBuilder.append(value).append(", ");
            }
            Log.d(TAG, "CSV Read: " + lineBuilder.toString());
        }

        // 4개의 문제가 List에 저장될 때까지 반복
        while (questions.size() < count) {

            // quiz_solved.csv 파일을 읽어옴 - 문제 중복 체크를 위함
            List<String[]> solvedQuestions = csvFileReader.readQuizSolvedCSVFile(context, quizSolvedFileName);

            // 문제 범위 내에서 랜덤한 문제ID 추출
            int randomIndex = random.nextInt(csvData.size());

            // CSV 데이터에서 문제 정보 추출
            String[] questionData = csvData.get(randomIndex);

            // 문제가 이미 해결된 문제인지 확인
            String questionId = questionData[0];        // 출제할 문제ID
            String strUserID = String.valueOf(userId);              // int -> String 형변환
            String strQuestionID = String.valueOf(questionId);      // int -> String 형변환

            // 이미 해당 유저에게 출제된 문제인지 확인
            if (!isSolved(solvedQuestions, strUserID, strQuestionID)) {
                // Question 객체 생성
                Question question = new Question(
                        Integer.parseInt(questionData[0]), // 문제 id
                        questionData[1], // 문제 내용
                        questionData[2], // 정답
                        questionData[3], // 보기1
                        questionData[4], // 보기2
                        questionData[5], // 보기3
                        questionData[6] // 보기4
                );

                // 생성된 문제를 리스트에 추가
                questions.add(question);

                // quiz_solved.csv 파일에 출제된 문제 write
                csvFileWriter.writeCSVFile(context, userId, question.getId());
            }
        }
        return questions;
    }

    // quiz_solved.csv(이미 해결된 문제)의 정보를 읽어오는 함수
    private static boolean isSolved(List<String[]> solvedData, String userId, String questionId){
        for (String[] line : solvedData) {
            // 각 줄의 첫 번째 요소가 questionId와 동일한지 확인
            if (line.length > 0 && line[0].equals(userId) && line[1].equals(questionId)) {
                return true;
            }
        }
        return false;
    }
}