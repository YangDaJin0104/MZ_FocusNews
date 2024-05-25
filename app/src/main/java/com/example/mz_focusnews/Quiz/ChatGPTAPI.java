package com.example.mz_focusnews.Quiz;

import android.content.Context;
import android.util.Log;

import com.example.mz_focusnews.ApiKeyManager;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class ChatGPTAPI {
    private static final String TAG = "ChatGPTAPI";

    public static String chatGPT(Context context, String summarize) {
        String url = "https://api.openai.com/v1/chat/completions";
        String model = "gpt-3.5-turbo";

        try {
            // ApiKeyManager로 ChatGPT API Key 받아오기 (해당 클래스에서 key를 이 함수에만 쓰기 때문에 지역 변수로 수정)
            ApiKeyManager apiKeyManager = ApiKeyManager.getInstance(context);
            String apiKey = apiKeyManager.getApiKey();

            URL obj = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer " + apiKey);
            connection.setRequestProperty("Content-Type", "application/json");

            // prompt 생성
            String prompt = summarize + "위 내용을 바탕으로 객관식 문제를 새로 만들어줘. 문제 내용, 정답, 선택지1, 선택지2, 선택지3, 선택지4를 question, answer, option1, option2, option3, option4 Json 형태로 보내줘(answer==option1||2||3||4)";

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

            return response.toString();

        } catch (IOException e) {
            Log.e(TAG, "Error in ChatGPT request: " + e.getMessage());
            return null;
        }
    }
}