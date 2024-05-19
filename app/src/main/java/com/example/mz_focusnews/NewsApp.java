package com.example.mz_focusnews;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.mz_focusnews.NewsCrawling.News;
import com.example.mz_focusnews.request.UpdateCategoryRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
