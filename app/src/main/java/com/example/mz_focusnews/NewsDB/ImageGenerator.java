package com.example.mz_focusnews.NewsDB;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.example.mz_focusnews.ApiKeyManager;
import com.example.mz_focusnews.Ranking.RankingParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// ChatGPT 기반 이미지 생성 클래스
public class ImageGenerator extends AsyncTask<Void, Void, Void> {
    private static final String TAG = "ImageGenerator";
    private static final String UPDATE_URL = "http://43.201.173.245/updateImgUrl.php";
    private static final String GET_URL = "http://43.201.173.245/getNullImgNewsData.php";

    private Context context;

    public ImageGenerator(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        Map<Integer, String> newsData = getNewsData();        // news 테이블의 img_url 컬럼이 null인 news_id만 가져옴
        
        // newsData의 각 항목을 순회하면서 news_id에 따른 summary를 가져와 이미지 생성
        for (Map.Entry<Integer, String> entry : newsData.entrySet()) {
            Integer newsId = entry.getKey();
            String summary = entry.getValue();

            chatGPTImageGenerator(context, newsId, summary);   // ChatGPT API를 이용해 요약된 내용의 이미지 생성
        }

        return null;
    }

    private String chatGPTImageGenerator(Context context, Integer newsId, String summary) {
        final String url = "https://api.openai.com/v1/images/generations";
        final String model = "dall-e-3";

        // SUMMARY가 null일 경우, 넘어감. (이미지 X)
        if (summary.equals("null")) {
            return null;
        }

        try {
            // ApiKeyManager로 ChatGPT API Key 받아오기
            ApiKeyManager apiKeyManager = ApiKeyManager.getInstance(context);
            String apiKey = apiKeyManager.getApiKey();

            URL obj = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
            connection.setRequestMethod("POST"); // 요청 메서드를 POST로 변경
            connection.setRequestProperty("Authorization", "Bearer " + apiKey);
            connection.setRequestProperty("Content-Type", "application/json");

            String firstSentence = extractFirstSentence(summary);      // summary에서 첫 번째 문장만 추출. (긴 프롬프트를 보낼 경우 에러가 발생하기 때문에)

            // request body (SUMMARY = prompt)
            String body = "{\"model\": \"" + model + "\", \"prompt\": \"" + firstSentence + "\", \"n\": 1, \"size\": \"1024x1024\"}";
            connection.setDoOutput(true);
            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(body.getBytes());
            outputStream.flush();
            outputStream.close();

            // Response from ChatGPT
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = br.readLine()) != null) {
                response.append(line);
            }
            br.close();

            String imgUrl = getImageUrl(response.toString());   // 결과에서 url만 추출하기
            Log.d(TAG, "2. chatGPTImageGenerator() - userId = " + newsId + " / summary = " + firstSentence + " / imgUrl = " + imgUrl);

            updateDBNewsImage(newsId, imgUrl);     // DB 업데이트 (null 값인 img_url을 새로 생성한 이미지 url로 업데이트)

            return response.toString();

        } catch (IOException e) {
            Log.e(TAG, "Error in ChatGPT request: " + e.getMessage());
            return null;
        }
    }

    private String extractFirstSentence(String summary) {       // summary에서 첫 번째 문장만 추출
        // summary를 마침표를 기준으로 나누어 배열로 변환
        String[] sentences = summary.split("\\. ");

        // 첫 번째 문장이 있는지 확인하고 반환
        if (sentences.length > 0) {
            return sentences[0] + ".";
        } else {
            return "";
        }
    }


    // ChatGPT response에서 url만 추출
    private String getImageUrl(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);

            // ChatGPT의 리턴 값에 따라 "data" 배열을 가져옴.
            JSONArray dataArray = jsonObject.getJSONArray("data");

            // 배열에서 첫 번째 객체를 가져옴.
            JSONObject dataObject = dataArray.getJSONObject(0);

            // ChatGPT의 리턴 값에 따라 "url" 키에 해당하는 값을 추출
            String imageUrl = dataObject.getString("url");

            return imageUrl;

        } catch (JSONException e) {
            Log.e(TAG, "Failed to parse JSON: " + e.getMessage());
            return null;
        }
    }


    public void updateDBNewsImage(Integer newsId, String imgUrl) {
        try {
            // 요청을 보낼 URL 설정
            URL url = new URL(UPDATE_URL);

            // 연결 설정
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            // POST 데이터 설정
            String postData = "newsId=" + newsId + "&imgUrl=" + URLEncoder.encode(imgUrl, "UTF-8");

            // 데이터 전송
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = postData.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // 응답 코드 확인
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                Log.d(TAG, "3. updateDBNewsImage() - News image URL updated successfully");
            } else {
                Log.e(TAG, "3. updateDBNewsImage() - Error updating news image URL. Response code: " + responseCode);
            }

            // 연결 종료
            connection.disconnect();

        } catch (IOException e) {
            Log.e(TAG, "Exception occurred: " + e.getMessage());
        }
    }

    private Map<Integer, String> getNewsData() {
        Map<Integer, String> newsData = new HashMap<>();

        try {
            URL url = new URL(GET_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            JSONArray jsonArray = new JSONArray(response.toString());
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                int newsId = jsonObject.getInt("news_id");
                String summary = jsonObject.getString("summary");

                Log.d(TAG, "1. getNewsData() - newsId = " + newsId + " / summary = " + summary);
                newsData.put(newsId, summary);
            }

        } catch (Exception e) {
            Log.e(TAG, "Error retrieving news data: " + e.getMessage());
        }

        Log.d(TAG, "News data: " + newsData);
        return newsData;
    }
}