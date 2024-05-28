package com.example.mz_focusnews.NewsDB;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.example.mz_focusnews.ApiKeyManager;
import com.example.mz_focusnews.Quiz.Question;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

// ChatGPT 기반 이미지 생성 클래스
public class ImageGenerator extends AsyncTask<Void, Void, String> {
    private static final String TAG = "ImageGenerator";

    private Context context;
    private String SUMMARY;

    public ImageGenerator(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(Void... voids) {
        String imageUrl = chatGPTImageGenerator(context);
        getImageUrl(imageUrl);      // TODO: lvalue 추가 필요
        return chatGPTImageGenerator(context);
    }

    private String chatGPTImageGenerator(Context context) {
        final String url = "https://api.openai.com/v1/images/generations";
        final String model = "dall-e-3";

        // SharedPreferences로 저장된 SUMMARY 가져오기 (TODO: 테스트 필요 (현재 null로만 출력됨)
        SharedPreferences preferences = context.getSharedPreferences("NewsData", Context.MODE_PRIVATE);
        SUMMARY = preferences.getString("summary", "null");
        Log.d(TAG, "Summary = " + SUMMARY);

        // SUMMARY가 null일 경우, 그냥 '뉴스' 사진 저장
        if(SUMMARY.equals("null")){
            SUMMARY = "뉴스";
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
            String body = "{\"model\": \"" + model + "\", \"prompt\": \"" + SUMMARY + "\", \"n\": 1, \"size\": \"1024x1024\"}";
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
            
            // TODO: 결과에서 url만 추출하기
            Log.d(TAG, "결과: " + response.toString());
        
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
            JSONArray dataArray = jsonObject.getJSONArray("data");

            if (dataArray.length() > 0) {
                JSONObject firstObject = dataArray.getJSONObject(0);
                String imageUrl = firstObject.getString("url");

                Log.d(TAG, "Image URL = " + imageUrl);
                return imageUrl;

            } else {
                Log.e(TAG, "No data found in JSON response");
                return null;
            }
        } catch (JSONException e) {
            Log.e(TAG, "Failed to parse JSON: " + e.getMessage());
            return null;
        }
    }

    public void UpdateDBNewsImage(){

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

