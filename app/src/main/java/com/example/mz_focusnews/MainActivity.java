package com.example.mz_focusnews;

import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

import com.example.mz_focusnews.Quiz.CSVFileReader;
import com.example.mz_focusnews.Quiz.Question;
import com.example.mz_focusnews.Quiz.QuestionGenerator;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int QUESTION_COUNT = 4;        // 문제 갯수 (오늘의 퀴즈 제외)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CSVFileReader csvFileReader = new CSVFileReader();

        // CSV 파일을 읽어옴
        List<String[]> csvData = csvFileReader.readCSVFile(this, "quiz.csv");

        // 읽어온 데이터 출력
        for (String[] line : csvData) {
            StringBuilder lineBuilder = new StringBuilder();
            for (String value : line) {
                lineBuilder.append(value).append(", ");
            }
            Log.d(TAG, "CSV Read: " + lineBuilder.toString());
        }

        // 퀴즈 출제
        List<Question> quizQuestions = QuestionGenerator.generateQuestions(this, QUESTION_COUNT);

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
    }
}