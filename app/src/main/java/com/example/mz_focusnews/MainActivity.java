package com.example.mz_focusnews;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.example.mz_focusnews.Quiz.QuizActivity;

public class MainActivity extends AppCompatActivity {
    //private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_quiz_submit);
        QuizActivity.showQuiz(this);
    }
}