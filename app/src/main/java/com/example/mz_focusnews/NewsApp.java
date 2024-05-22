package com.example.mz_focusnews;

import android.app.Application;

import java.util.HashMap;
import java.util.Map;

/**
 * 뉴스 애플리케이션 클래스
 */
public class NewsApp extends Application {
    private Map<String, UserSession> userSessions;

    @Override
    public void onCreate() {
        super.onCreate();
        userSessions = new HashMap<>();
    }

    public Map<String, UserSession> getUserSessions() {
        return userSessions;
    }
}
