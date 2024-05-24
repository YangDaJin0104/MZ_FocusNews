package com.example.mz_focusnews;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.mz_focusnews.RelatedNews.NewsData;


public class ContentFragment extends Fragment {

    private TextView summary1, summary2, summary3;
    private TextView newsTitle;
    private TextView relatedNews1, relatedNews2;
    private RequestQueue requestQueue;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_content, container, false);


        summary1 = view.findViewById(R.id.news_content1);
        summary2 = view.findViewById(R.id.news_content2);
        summary3 = view.findViewById(R.id.news_content3);

        newsTitle = view.findViewById(R.id.news_title);

        relatedNews1 = view.findViewById(R.id.news_related1);
        relatedNews2 = view.findViewById(R.id.news_related2);

        requestQueue = Volley.newRequestQueue(getActivity());

        fetchSummaryData(getActivity(), 1214);     //요약문 가져오기  //후에 homefragment나 categoryfragment에서 넘겨받아야함

        return view;
    }

    public void fetchSummaryData(Context context, int newsId) {         // 요약문 읽어와서 보여주는 함수
        String url = "http://43.201.173.245/getSummary.php";

        // URL에 파라미터 추가
        String urlWithParams = url + "?news_id=" + newsId;

        // StringRequest 생성
        StringRequest stringRequest = new StringRequest(Request.Method.GET, urlWithParams,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // 서버 응답 처리
                        try {
                            if (response.trim().charAt(0) == '[') {
                                JSONArray jsonArray = new JSONArray(response);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject newsItem = jsonArray.getJSONObject(i);
                                    Log.d("JSONObject FetchSummary", newsItem.toString());
                                    processNewsItem(newsItem);
                                }
                            } else {
                                JSONObject jsonObject = new JSONObject(response);
                                if (jsonObject.has("error")) {
                                    Log.e("Error", jsonObject.getString("error"));
                                } else {
                                    processNewsItem(jsonObject);
                                }
                            }
                        } catch (JSONException e) {
                            Log.e("JSON Parsing Error", e.getMessage());
                        }
                        Log.d("Response", response);
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

    private void fetchRelatedSummary(Context context, int newsId, TextView targetTextView) {
        String url = "http://43.201.173.245/getSummary.php?news_id=" + newsId;

//        RequestQueue requestQueue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonResponse = new JSONArray(response);
                            if (jsonResponse.length() > 0) {
                                JSONObject firstItem = jsonResponse.getJSONObject(0);
                                if (firstItem.has("title") && !firstItem.isNull("title")) {
                                    String title = firstItem.getString("title");
                                    targetTextView.setText(title);
                                } else {
                                    Log.e("Error", "No title found for news_id: " + newsId);
                                    targetTextView.setText("Title not available"); // 또는 다른 기본 텍스트
                                }
                            }
                        } catch (JSONException e) {
                            Log.e("JSON Parsing Error", e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Volley Error", "Error fetching title: " + error.toString());
            }
        });
        requestQueue.add(stringRequest);
    }

    private void updateRelatedSummaries(Context context, NewsData item) {
        fetchRelatedSummary(context, item.getRelated1(), relatedNews1);
        fetchRelatedSummary(context, item.getRelated2(), relatedNews2);
    }


    private void processNewsItem(JSONObject newsItem) throws JSONException {
        int id = newsItem.getInt("news_id");
        String summary = newsItem.getString("summary");
        String title = newsItem.getString("title");
        int related1 = newsItem.optInt("related_news1", 0);
        int related2 = newsItem.optInt("related_news2", 0);

        NewsData item = new NewsData(id, title, summary, related1, related2);

        String[] summaries = summary.split("\\. ");
        if (summaries.length >= 3) {
            summary1.setText(summaries[0].trim() + ".");
            summary2.setText(summaries[1].trim() + ".");
            summary3.setText(summaries[2].trim());
        }

        newsTitle.setText(title);

        updateRelatedSummaries(getActivity(), item);
    }
}