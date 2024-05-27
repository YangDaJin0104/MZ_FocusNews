package com.example.mz_focusnews;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.GZIPInputStream;

public class NewsDataAdapter {
    private RequestQueue requestQueue;

    public NewsDataAdapter(Context context) {
        requestQueue = Volley.newRequestQueue(context);
    }

    public void fetchAllNewsId(Context context, NewsDataCallback callback) {
        String url = "http://43.201.173.245/getAllNewsId.php";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            int newsId = jsonArray.getInt(i);
                            fetchNewsData(context, newsId, callback);
                        }
                    } catch (JSONException e) {
                        Log.e("NewsDataAdapter", "JSON Parsing Error", e);
                        callback.onError(e.getMessage());
                    }
                },
                error -> {
                    Log.e("NewsDataAdapter", "Request Error", error);
                    callback.onError(error.toString());
                }
        );
        requestQueue.add(stringRequest);
    }

    public void fetchNewsData(Context context, int newsId, NewsDataCallback callback) {
        String url = "http://43.201.173.245/getNewsData.php?news_id=" + newsId;

//        Log.d("url fetchNewsData", url);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // 서버 응답 처리
                        try {
                            if (response.trim().charAt(0) == '[') {
                                JSONArray jsonArray = new JSONArray(response);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject contentObj = jsonArray.getJSONObject(i);
                                    String content = contentObj.getString("content").replaceAll("\\s+", " ");
                                    if (content.length() > 1000) {
                                        content = content.substring(0, 1000);
                                    }
                                    callback.onDataFetched(newsId, content);
                                }
                            } else {
                                JSONObject jsonObject = new JSONObject(response);
                                if (jsonObject.has("error")) {
                                    callback.onError(jsonObject.getString("error"));
                                    Log.e("fetchData Error", jsonObject.getString("error"));
                                } else {
                                    callback.onError(jsonObject.getString("error"));
                                    Log.e("Error..?", jsonObject.getString("error"));
                                }
                            }
                        } catch (JSONException e) {
                            callback.onError("JSON parsing error: " + e.getMessage());
                            Log.e("JSON Parsing Error", e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // 에러 처리
                Log.e("Error", error.toString());
            }
        });
        // 요청을 RequestQueue에 추가
        requestQueue.add(stringRequest);
    }



}