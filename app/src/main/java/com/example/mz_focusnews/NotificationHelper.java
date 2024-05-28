package com.example.mz_focusnews;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

public class NotificationHelper {
    private static final String CHANNEL_ID = "breaking_news_channel";
    private static final String CHANNEL_NAME = "Breaking News";
    private static final String CHANNEL_DESCRIPTION = "Notifications for breaking news";
    private static final String TAG = "NotificationHelper";

    private NotificationManager notificationManager;
    private Context context;

    public NotificationHelper(Context context) {
        this.context = context;
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(CHANNEL_DESCRIPTION);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void sendBreakingNewsNotification(String title) {
        try {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.siren) // 알림 아이콘 설정
                    .setContentTitle("Breaking News")
                    .setContentText(title)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true);

            Notification notification = builder.build();
            notificationManager.notify((int) System.currentTimeMillis(), notification);

            // 로그 추가
            Log.d(TAG, "속보 알림 전송: " + title);
        } catch (Exception e) {
            Log.e(TAG, "알림 전송 실패: " + e.getMessage());
            Toast.makeText(context, "알림 전송에 실패했습니다: " + title, Toast.LENGTH_SHORT).show();
        }
    }
}
