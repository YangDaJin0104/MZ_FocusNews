package com.example.mz_focusnews.Quiz;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

public class InitializeQuizTime extends BroadcastReceiver {
    private static final String TAG = "InitializeQuizTime";
    private static final String USER_ID = "coddl";
    private static final String PREFS_NAME = "QuizPrefs";

    @Override
    public void onReceive(Context context, Intent intent) {
        Map<String, Boolean> mapData = new HashMap<>();

        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        // Map 데이터의 각 요소를 SharedPreferences에 저장
        for (Map.Entry<String, Boolean> entry : mapData.entrySet()) {
            editor.putBoolean(entry.getKey(), entry.getValue());
        }

        editor.putBoolean(USER_ID, false); // Reset the boolean variable
        editor.apply();
        Log.d(TAG, "Quiz flag reset to false.");
    }
}
