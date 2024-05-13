package com.example.mz_focusnews.Quiz;

import android.content.Context;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mz_focusnews.R;

import java.util.List;

public class QuizActivity extends AppCompatActivity {
    private static final String TAG = "QuizActivity";
    private static final String QUIZ_FILE_NAME = "quiz.csv";
    private static final String QUIZ_SOLVED_FILE_NAME = "quiz_solved.csv";
    private static final int QUESTION_COUNT = 4;        // 문제 갯수 (오늘의 퀴즈 제외)
    private static final int USER_ID = 456;             // 유저 아이디 (DB에서 가져와야 함. 구현 필요)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_quiz_submit);
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            CSVFileReader csvFileReader = new CSVFileReader();
            CSVFileWriter csvFileWriter = new CSVFileWriter();

            csvFileWriter.clearQuizSolvedCSVFile(this, QUIZ_SOLVED_FILE_NAME);    // quiz_solved.csv 파일 초기화 (테스트용)

            // CSV 파일을 읽어옴
            List<String[]> csvData = csvFileReader.readQuizCSVFile(this, QUIZ_FILE_NAME);

            // 읽어온 데이터 출력
            for (String[] line : csvData) {
                StringBuilder lineBuilder = new StringBuilder();
                for (String value : line) {
                    lineBuilder.append(value).append(", ");
                }
                Log.d(TAG, "CSV Read: " + lineBuilder.toString());
            }

            // 퀴즈 출제
            List<Question> quizQuestions = QuestionGenerator.generateQuestions(this, QUIZ_FILE_NAME, QUIZ_SOLVED_FILE_NAME, USER_ID, QUESTION_COUNT);

            // 퀴즈 문제 출력
            for (Question question : quizQuestions) {
                Log.d(TAG, "Question ID: " + question.getId());
                Log.d(TAG, "Question: " + question.getQuestion());
                Log.d(TAG, "Correct Answer: " + question.getCorrectAnswer());
                Log.d(TAG, "Option 1: " + question.getOption1());
                Log.d(TAG, "Option 2: " + question.getOption2());
                Log.d(TAG, "Option 3: " + question.getOption3());
                Log.d(TAG, "Option 4: " + question.getOption4());
            }

            csvFileReader.readQuizSolvedCSVFile(this, QUIZ_SOLVED_FILE_NAME);

            // ChatGPT API 비용 문제 발생 - 오류: You exceeded your current quota, please check your plan and billing details.
            //String response = ChatGPTAPI.chatGPT("HI?");
            //Log.d(TAG, "ChatGPT Return: " + response);
        }
    }

    public static void showQuiz(Context context) {
        // 퀴즈 관련 로직 구현 필요
    }
}
