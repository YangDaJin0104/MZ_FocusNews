package com.example.mz_focusnews.RelatedNews;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import android.content.Context;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

public class RelatedNewsUtils {

    private static RequestQueue queue; // 클래스 필드로 RequestQueue 추가

    // RequestQueue를 초기화하는 메서드
    private static void initQueue(Context context) {
        if (queue == null) {
            queue = Volley.newRequestQueue(context.getApplicationContext());
        }
    }

    public static void updateRelatedNews(Context context, int newsId, int related1, int related2) {
        initQueue(context);

        String url = "http://43.201.173.245//updateRelatedNews.php"; // PHP 스크립트 URL

//        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response", response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Error.Response", error.toString());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("news_id", String.valueOf(newsId));
                params.put("related_news1", String.valueOf(related1));
                params.put("related_news2", String.valueOf(related2));
                return params;
            }
        };
        queue.add(postRequest);
    }
}
