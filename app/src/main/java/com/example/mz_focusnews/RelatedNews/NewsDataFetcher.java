package com.example.mz_focusnews.RelatedNews;

import android.content.Context;
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


public class NewsDataFetcher {
    private NewsDataStore newsDataStore;  // NewsDataStore 객체를 저장할 필드 추가

    // 생성자 정의: NewsDataStore 객체를 인수로 받음
    public NewsDataFetcher(NewsDataStore newsDataStore) {
        this.newsDataStore = newsDataStore;  // 인스턴스 변수 초기화
    }
    public void fetchAllNews(Context context, FetchCompleteListener listener) {
        String url = "http://43.201.173.245/getSummary.php";

        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonResponse = new JSONArray(response);

                            for (int i = 0; i < jsonResponse.length(); i++) {
                                JSONObject news = jsonResponse.getJSONObject(i);
                                int newsId = news.getInt("news_id");
                                String summary = news.getString("summary");
                                String title = news.getString("title");
                                int related1 = news.getInt("related_news1");
                                int related2 = news.getInt("related_news2");
                                Log.d("News Summary FetchallNews", "ID: " + newsId + ", Title:" + title + ", Summary: " + summary);
                                newsDataStore.addNewsItem(newsId, title, summary, related1, related2);
                            }
                            listener.onFetchComplete();
                        } catch (JSONException e) {
                            Log.e("JSON Error", "JSON parsing error: " + e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley Error", "Error fetching data: " + error.getMessage());
            }
        });

        queue.add(stringRequest);
    }
}

