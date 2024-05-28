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

        // SUMMARY가 null일 경우, 그냥 '뉴스' 사진을 생성하도록 함.
        if (summary.equals("null")) {
            summary = "뉴스";
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

            // request body (SUMMARY = prompt)
            String body = "{\"model\": \"" + model + "\", \"prompt\": \"" + summary + "\", \"n\": 1, \"size\": \"1024x1024\"}";
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
            Log.d(TAG, "userId = " + newsId + " / summary = " + summary + " / imgUrl = " + imgUrl);
            updateDBNewsImage(newsId, imgUrl);     // DB 업데이트 (null 값인 img_url을 새로 생성한 이미지 url로 업데이트)

            return response.toString();

        } catch (IOException e) {
            Log.e(TAG, "Error in ChatGPT request: " + e.getMessage());
            return null;
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

            // POST 데이터 설정
            String postData = "news_id=" + newsId + "&img_url=" + URLEncoder.encode(imgUrl, "UTF-8");

            // 데이터 전송
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = postData.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // 응답 코드 확인 (TODO: 여기서 ResponseCode가 500이 출력되는 중)
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                Log.d(TAG, "News image URL updated successfully");
            } else {
                Log.e(TAG, "Error updating news image URL. Response code: " + responseCode);
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

                newsData.put(newsId, summary);
            }

        } catch (Exception e) {
            Log.e(TAG, "Error retrieving news data: " + e.getMessage());
        }

        Log.d(TAG, "News data: " + newsData);
        return newsData;
    }
}


/*

curl https://api.openai.com/v1/images/generations \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $OPENAI_API_KEY" \
  -d '{
    "model": "dall-e-3",
    "prompt": "a white siamese cat",
    "n": 1,
    "size": "1024x1024"
  }'

ChatGPT 결과 예시
{  "created": 1716878479,  "data": [    {      "revised_prompt": "Focus on a controversial amendment to a special law on lease fraud amidst a partisan standoff.",
"url": "https://oaidalleapiprodscus.blob.core.windows.net/private/org-6gpHzIMepZUB8E6NthUcOpZv/user-b7ySFavkZ9dkdchyOVRnHZTd/img-mwdR8chvIrQPWnCW4uRRLnoo.png?st=2024-05-28T05%3A41%3A19Z&se=2024-05-28T07%3A41%3A19Z&sp=r&sv=2021-08-06&sr=b&rscd=inline&rsct=image/png&skoid=6aaadede-4fb3-4698-a8f6-684d7786b067&sktid=a48cca56-e6da-484e-a814-9c849652bcb3&skt=2024-05-27T19%3A14%3A50Z&ske=2024-05-28T19%3A14%3A50Z&sks=b&skv=2021-08-06&sig=IuGkn4I87iykim3fBDyzVddrP/cHbugwdBAcK43Gfjg%3D"    }  ]}

 */

