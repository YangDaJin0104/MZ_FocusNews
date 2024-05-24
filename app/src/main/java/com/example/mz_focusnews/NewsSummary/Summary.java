package com.example.mz_focusnews.NewsSummary;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class Summary {
    private static final String TAG = "ChatGPTAPI";
    private static final String API_KEY = "//";    // openAI API key

    public static String chatGPT_summary(String article) {
        String url = "https://api.openai.com/v1/chat/completions";
        String model = "gpt-3.5-turbo";


        try {
            URL obj = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer " + API_KEY);
            connection.setRequestProperty("Content-Type", "application/json");

            // prompt 생성
            String prompt = article + " 이 뉴스기사를 3문장으로 요약해줘. 각 문장은 공백 포함 70글자 내외로 써줘.";

            // request body
            String body = "{\"model\": \"" + model + "\", \"messages\": [{\"role\": \"user\", \"content\": \"" + prompt + "\"}]}";
            connection.setDoOutput(true);
            connection.getOutputStream().write(body.getBytes());

            // Response from ChatGPT
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = br.readLine()) != null) {
                response.append(line);
            }
            br.close();

            // 파싱 후 content만 추출
            JSONObject jsonResponse = new JSONObject(response.toString());
            JSONArray choices = jsonResponse.getJSONArray("choices");

            if (choices.length() > 0) {
                JSONObject firstChoice = choices.getJSONObject(0);
                if (firstChoice.has("message")) {
                    JSONObject message = firstChoice.getJSONObject("message");
                    if (message.has("content")) {
                        String content = message.getString("content");
                        return content;  // 여기서 content만 반환
                    } else {
                        Log.e(TAG, "No 'content' in message");
                    }
                } else {
                    Log.e(TAG, "No 'message' in first choice");
                }
            }

        } catch (IOException | JSONException e) {
            Log.e(TAG, "Error in ChatGPT request: " + e.getMessage());
            return null;
        }
        return null;
    }
}