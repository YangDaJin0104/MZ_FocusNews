package com.example.mz_focusnews.NewsDB;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.example.mz_focusnews.ApiKeyManager;

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
    private String summarize;

    public ImageGenerator(Context context, String summarize) {
        this.context = context;
        this.summarize = summarize;
    }

    @Override
    protected String doInBackground(Void... voids) {
        return chatGPTImageGenerator(context, summarize);
    }

    private String chatGPTImageGenerator(Context context, String summarize) {
        final String url = "https://api.openai.com/v1/images/generations";
        final String model = "dall-e-3";

        try {
            // ApiKeyManager로 ChatGPT API Key 받아오기
            ApiKeyManager apiKeyManager = ApiKeyManager.getInstance(context);
            String apiKey = apiKeyManager.getApiKey();

            URL obj = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
            connection.setRequestMethod("POST"); // 요청 메서드를 POST로 변경
            connection.setRequestProperty("Authorization", "Bearer " + apiKey);
            connection.setRequestProperty("Content-Type", "application/json");

            // request body (summarize = prompt)
            String body = "{\"model\": \"" + model + "\", \"prompt\": \"" + summarize + "\", \"n\": 1, \"size\": \"1024x1024\"}";
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

 */