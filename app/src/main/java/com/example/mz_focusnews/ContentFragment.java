package com.example.mz_focusnews;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.mz_focusnews.NewsDB.News;
import com.example.mz_focusnews.RelatedNews.NewsData;

import java.util.Arrays;
import com.bumptech.glide.Glide;

public class ContentFragment extends Fragment {

    private ProgressBar progressBar;
    private TextView tv_title;
    private TextView tv_time;
    private TextView summary1, summary2, summary3;
    private TextView relatedNews1, relatedNews2;
    private ImageView image;
    private RequestQueue requestQueue;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_content, container, false);

        progressBar = view.findViewById(R.id.loading);
        tv_title = view.findViewById(R.id.interest_title);
        tv_time = view.findViewById(R.id.news_date);
        summary1 = view.findViewById(R.id.news_content1);
        summary2 = view.findViewById(R.id.news_content2);
        summary3 = view.findViewById(R.id.news_content3);
        image = view.findViewById(R.id.news_img);

//        newsTitle = view.findViewById(R.id.interest_title);

        relatedNews1 = view.findViewById(R.id.news_related1);
        relatedNews2 = view.findViewById(R.id.news_related2);

        requestQueue = Volley.newRequestQueue(getActivity());

        // Bundle에서 newsId 추출
        if (getArguments() != null) {
            int newsId = getArguments().getInt("newsId", -1);
            Log.d("catch newsId", String.valueOf(newsId));
            fetchSummaryData(getActivity(), newsId, view);    //요약문 가져오기  //후에 homefragment나 categoryfragment에서 넘겨받아야함
        }

        relatedNews1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 새 Fragment 인스턴스 생성
                ContentFragment newContentFragment = new ContentFragment();

                // Bundle 객체 생성 및 newsId 추가
                Bundle args = new Bundle();
                Integer relatedNewsId = (Integer) view.getTag();  // relatedNews1의 태그에서 related1의 ID를 가져옴
                if (relatedNewsId == null) {
                    Toast.makeText(getContext(), "관련 뉴스 정보가 없습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
                args.putInt("newsId", relatedNewsId);
                newContentFragment.setArguments(args);

                // Fragment 매니저를 사용하여 트랜잭션 시작
                getChildFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, newContentFragment)
                        .addToBackStack(null)  // 백 스택에 추가
                        .commit();

            }
        });

        relatedNews2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 새 Fragment 인스턴스 생성
                ContentFragment newContentFragment = new ContentFragment();

                // Bundle 객체 생성 및 newsId 추가
                Bundle args = new Bundle();
                Integer relatedNewsId = (Integer) view.getTag();  // relatedNews2의 태그에서 related1의 ID를 가져옴
                if (relatedNewsId == null) {
                    Toast.makeText(getContext(), "관련 뉴스 정보가 없습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
                args.putInt("newsId", relatedNewsId);
                newContentFragment.setArguments(args);

                // Fragment 매니저를 사용하여 트랜잭션 시작
                getChildFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, newContentFragment)
                        .addToBackStack(null)  // 백 스택에 추가
                        .commit();

            }
        });
        return view;
    }

    public void fetchSummaryData(Context context, int newsId, View view) {         // 요약문 읽어와서 보여주는 함수
        String url = "http://43.201.173.245/getSummary.php";

        // URL에 파라미터 추가
        String urlWithParams = url + "?news_id=" + newsId;
        Log.d("fetchSummaryData", String.valueOf(newsId));

        // StringRequest 생성
        StringRequest stringRequest = new StringRequest(Request.Method.GET, urlWithParams,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressBar.setVisibility(view.VISIBLE);
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
                        progressBar.setVisibility(View.GONE); // 프로그레스 바 숨기기
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // 에러 처리
                progressBar.setVisibility(View.GONE); // 프로그레스 바 숨기기
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
        summary = cleanString(summary);
        String title = newsItem.getString("title");
        String date = newsItem.getString("date");
        int related1 = newsItem.optInt("related_news1", 0);
        relatedNews1.setTag(related1);  // relatedNews1 뷰에 related1의 ID를 태그로 저장
        int related2 = newsItem.optInt("related_news2", 0);
        relatedNews2.setTag(related2);  // relatedNews2 뷰에 related2의 ID를 태그로 저장
        String img_url = newsItem.getString("img_url");

        // 뉴스 원문 URL - link를 가져옴에 따라 getSummary.php 파일도 수정했습니다. (coddl)
        String news_url = newsItem.getString("link");

        NewsData item = new NewsData(id, title, summary, related1, related2);

        String[] summaries = summary.split("\\. ");
        Log.d("summaries split", Arrays.toString(summaries));
        if (summaries.length >= 3) {
            summary1.setText(summaries[0].trim() + ".");
            summary2.setText(summaries[1].trim() + ".");
            summary3.setText(summaries[2].trim() + ".");
        } else if (summaries.length == 2) {
            summary1.setText(summaries[0].trim() + ".");
            summary2.setText(summaries[1].trim() + ".");
            summary3.setText("");
        }

        tv_title.setText(title);
        tv_time.setText(date);

        //img_url로 이미지 불러와서 띄우기
        Glide.with(this)
                .load(img_url)
                .into(image);

        // 뉴스 이미지를 클릭할 경우, 뉴스 원문 웹 브라우저로 이동
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri browserUri = Uri.parse(news_url);
                Intent browser = new Intent(Intent.ACTION_VIEW, browserUri);

                startActivity(browser);
            }
        });

        updateRelatedSummaries(getActivity(), item);
    }

    private String cleanString(String string){      // 줄바꿈 제거 및 분리 전처리
        String cleanedString = string.replaceAll("\\r\\n|\\r|\\n", "");     // 줄바꿈 제거
        cleanedString = cleanedString.replaceAll("\\.(?![0-9\\s])(?![A-Za-z])", ". ");     // 숫자, 영문을 제외하고 마침표 다음에 공백이 아닌 문자가 올 경우 공백을 추가
        return cleanedString;
    }
}