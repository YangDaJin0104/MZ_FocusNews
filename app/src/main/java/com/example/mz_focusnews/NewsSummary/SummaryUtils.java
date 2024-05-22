package com.example.mz_focusnews.NewsSummary;

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

public class SummaryUtils {
    public void sendSummaryToServer(Context context, String link, String summary) {
        String url = "http://43.201.173.245//SummaryInsert.php";

        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // 서버로부터 응답을 처리
                        Log.d("Response", response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // 에러 처리
                        Log.e("Error.Response", error.toString());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                // 요청 파라미터 설정
                Map<String, String> params = new HashMap<>();
                params.put("summary", summary);
                params.put("link", link);
                return params;
            }
        };

        // 요청을 RequestQueue에 추가
        queue.add(postRequest);
    }
}
