package com.example.mz_focusnews.Quiz;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.ArrayList;
import java.util.Random;

public class QuestionGenerator {
    private static final String TAG = "QuestionGenerator";

    // 오늘의 퀴즈 생성 (구현 필요)
    public static Question generateTodayQuiz(String response) {
        // JSON 데이터 파싱하여 필요한 정보 추출
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject message = jsonObject.getJSONArray("choices").getJSONObject(0).getJSONObject("message");
            String content = message.getString("content");
            JSONObject innerJsonObject = new JSONObject(content);

            String question = innerJsonObject.getString("question");
            String correctAnswer = innerJsonObject.getString("answer");
            String option1 = innerJsonObject.getString("option1");
            String option2 = innerJsonObject.getString("option2");
            String option3 = innerJsonObject.getString("option3");
            String option4 = innerJsonObject.getString("option4");

            // ChatGPT가 제시한 정답이 보기 안에 있는지 확인 (가끔 없을 때가 있음)
            if(correctAnswer.equals(option1) ||
                    correctAnswer.equals(option2) ||
                    correctAnswer.equals(option3) ||
                    correctAnswer.equals(option4)) {

                // 오늘의 퀴즈 객체 생성
                Question questionObject = new Question(
                        0,           // 오늘의 퀴즈id는 default 0
                        question,      // 문제 내용
                        correctAnswer,  // 정답
                        option1,        // 보기1
                        option2,        // 보기2
                        option3,        // 보기3
                        option4         // 보기4
                );

                // 테스트 출력
                Log.d(TAG, "Question: " + question);
                Log.d(TAG, "Answer: " + correctAnswer);
                Log.d(TAG, "option1: " + option1);
                Log.d(TAG, "option2: " + option2);
                Log.d(TAG, "option3: " + option3);
                Log.d(TAG, "option4: " + option4);

                return questionObject;
            }

            return null;

        } catch (JSONException e) {
            System.err.println("Failed to parse JSON: " + e.getMessage());
            return null;
        }
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