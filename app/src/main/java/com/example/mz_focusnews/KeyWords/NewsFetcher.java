package com.example.mz_focusnews.KeyWords;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.mz_focusnews.NewsDB.News;
import com.example.mz_focusnews.adapter.NewsAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class NewsFetcher {

    private static final String USER_KEYWORD_URL = "http://10.0.2.2:8081/api/users/";
    private static final String NEWS_BY_KEYWORD_URL = "http://10.0.2.2:8081/api/news/search?keyword=";
    private static final String TAG = "NewsFetcher";

    private RequestQueue requestQueue;
    private NewsAdapter newsAdapter;
    private List<News> newsList;

    public NewsFetcher(Context context, NewsAdapter newsAdapter, List<News> newsList) {
        this.requestQueue = Volley.newRequestQueue(context);
        this.newsAdapter = newsAdapter;
        this.newsList = newsList;
    }

    public void fetchUserKeywordAndNews(String userId) {
        String url = USER_KEYWORD_URL + userId + "/keyword";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    Log.d(TAG, "Keyword: " + response);
                    fetchNewsByKeyword(response);
                },
                error -> Log.e(TAG, "Error fetching keyword", error)
        );
        requestQueue.add(stringRequest);
    }

    private void fetchNewsByKeyword(String keyword) {
        String url = NEWS_BY_KEYWORD_URL + keyword;
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    newsList.clear();
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
                error -> Log.e(TAG, "Error fetching news by keyword", error)
        );
        requestQueue.add(jsonArrayRequest);
    }
}
