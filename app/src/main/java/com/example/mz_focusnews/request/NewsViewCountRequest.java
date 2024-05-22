package com.example.mz_focusnews.request;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;


public class NewsViewCountRequest extends StringRequest {

    // 서버 URL 설정(php 파일 연동)
    final static private String URL = "http://43.201.173.245/news_view_count.php";
    private Map<String, String> params;

    public NewsViewCountRequest(int newsId, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);

        params = new HashMap<>();
        params.put("news_id", String.valueOf(newsId)); // int 값을 String으로 변환하여 전달
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return params;
    }
}