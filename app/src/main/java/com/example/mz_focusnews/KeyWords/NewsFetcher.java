package com.example.mz_focusnews.KeyWords;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.mz_focusnews.NewsDB.News;
import com.example.mz_focusnews.adapter.NewsAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NewsFetcher {

    private static final String USER_KEYWORD_URL = "http://10.0.2.2:8081/api/users/";
    private static final String NEWS_BY_KEYWORDS_URL = "http://10.0.2.2:8081/api/news/searchByKeywords";
    private static final String TAG = "NewsFetcher";

    private RequestQueue requestQueue;
    private NewsAdapter newsAdapter;
    private List<News> newsList;

    public NewsFetcher(Context context, NewsAdapter newsAdapter, List<News> newsList) {
        this.requestQueue = Volley.newRequestQueue(context);
        this.newsAdapter = newsAdapter;
        this.newsList = newsList;
    }

    public void fetchUserKeywordsAndNews(String userId) {
        String url = USER_KEYWORD_URL + userId + "/keywords";
        Log.d(TAG, "Fetching keywords from URL: " + url);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    List<String> keywords = new ArrayList<>();
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            String keyword = response.getString(i);
                            if (keyword != null && !keyword.isEmpty()) {
                                keywords.add(keyword);
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "JSON parsing error", e);
                        }
                    }
                    fetchNewsByKeywords(userId); // userId를 사용하여 fetchNewsByKeywords 호출
                },
                error -> {
                    Log.e(TAG, "Error fetching keywords", error);
                    error.printStackTrace();
                }
        );
        requestQueue.add(jsonArrayRequest);
    }

    private void fetchNewsByKeywords(String userId) {
        String url = NEWS_BY_KEYWORDS_URL + "?userId=" + userId;
        Log.d(TAG, "Fetching news with URL: " + url);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    newsList.clear(); // Clear the old news
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject newsObject = response.getJSONObject(i);
                            News news = new News();
                            news.setTitle(newsObject.getString("title"));
                            news.setPublish(newsObject.getString("publish"));
                            news.setDate(newsObject.getString("date"));
                            news.setImgUrl(newsObject.getString("imgUrl"));
                            newsList.add(news);
                        } catch (JSONException e) {
                            Log.e(TAG, "JSON parsing error", e);
                        }
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        newsAdapter.updateNews(newsList);
                    }
                },
                error -> {
                    Log.e(TAG, "Error fetching news by keyword", error);
                    error.printStackTrace();
                }
        );
        requestQueue.add(jsonArrayRequest);
    }
}
