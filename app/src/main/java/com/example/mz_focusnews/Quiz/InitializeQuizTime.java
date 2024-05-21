package com.example.mz_focusnews.Quiz;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

public class InitializeQuizTime extends BroadcastReceiver {
    private static final String TAG = "InitializeQuizTime";
    private static final String IS_SOLVED_QUIZ_KEY = "123";
    private static final String PREFS_NAME = "QuizPrefs";

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(IS_SOLVED_QUIZ_KEY, false); // Reset the boolean variable
        editor.apply();
        Log.d(TAG, "Quiz flag reset to false.");
    }
}
