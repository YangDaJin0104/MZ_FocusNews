package com.example.mz_focusnews;

import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

import com.example.mz_focusnews.Quiz.CSVFileReader;
import com.example.mz_focusnews.Quiz.CSVFileWriter;
import com.example.mz_focusnews.Quiz.Question;
import com.example.mz_focusnews.Quiz.QuestionGenerator;
import com.example.mz_focusnews.Quiz.QuizActivity;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int QUESTION_COUNT = 4;        // 문제 갯수 (오늘의 퀴즈 제외)
    private static final int USER_ID = 123;             // 유저 아이디 (DB에서 가져와야 함. 구현 필요)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_quiz_submit);

        QuizActivity.showQuiz(this);
    }
}