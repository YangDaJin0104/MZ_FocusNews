package com.example.mz_focusnews.Quiz;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.Calendar;

public class QuizTimeReset extends Service {
    private static final String TAG = "QuizTimeReset";
    private Handler handler;
    private Runnable runnable;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                // TODO: 배포 시 오전 12시에 초기화하도록 설정
                Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);
                if (hour == 19 && minute == 24) {
                    Log.d(TAG, "퀴즈 초기화 시간입니다. 현재 시간(=설정 시간): " + hour + "시 " + minute + "분");
                    broadcastQuizTimeInitialize();
                }
                // 특정 시간마다 실행되도록 핸들러에 다시 등록
                handler.postDelayed(this, 60000); // 1분마다 체크 (10초마다 체크하면 같은 시간동안 여러 번 실행됨)
            }
        };
        // 특정 시간마다 실행되도록 핸들러에 등록
        handler.post(runnable);
        return START_STICKY;
    }

    private void broadcastQuizTimeInitialize() {
        Intent intent = new Intent("ACTION_SET_QUIZ_FLAG");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }
}
