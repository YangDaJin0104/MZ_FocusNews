package com.example.mz_focusnews.Quiz;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class InitializeQuizTime extends BroadcastReceiver {
    private static final String TAG = "InitializeQuizTime";
    public static boolean myBoolean = false;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null && intent.getAction().equals("com.example.SET_BOOLEAN_TRUE")) {
            myBoolean = true;
            Log.d(TAG, "Boolean variable set to true");
        }
    }
}
