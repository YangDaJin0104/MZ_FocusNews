package com.example.mz_focusnews.Quiz;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class ChatGPTAPI {
    private static final String TAG = "ChatGPTAPI";
    private static final String API_KEY = "//";     // 최민경의 API secret key

    public static String chatGPT(String summarize1, String summarize2, String summarize3) {
        String url = "https://api.openai.com/v1/chat/completions";      // ChatGPT API 엔드포인트의 URL (수정 X)
        String model = "gpt-3.5-turbo";

        try {
            URL obj = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer " + API_KEY);
            connection.setRequestProperty("Content-Type", "application/json");

            // prompt 생성
            String prompt = summarize1 + summarize2 + summarize3 + "위 내용을 바탕으로 객관식 문제를 새로 만들어줘.\n" +
                    "문제 내용, 정답, 선택지1, 선택지2, 선택지3, 선택지4를 Json 형태로 보내줘";
            
            // The request body
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

            // JSON 데이터 파싱하여 필요한 정보 추출
            try {
                JSONObject jsonObject = new JSONObject(response.toString());
                String question = jsonObject.getString("question");
                String correctAnswer = jsonObject.getString("answer");
                String option1 = jsonObject.getString("option1");
                String option2 = jsonObject.getString("option2");
                String option3 = jsonObject.getString("option3");
                String option4 = jsonObject.getString("option4");

                // 테스트 출력
                System.out.println("Question: " + question);
                System.out.println("Answer: " + correctAnswer);
                System.out.println("option1: " + option1);
                System.out.println("option2: " + option2);
                System.out.println("option3: " + option3);
                System.out.println("option4: " + option4);

            } catch (JSONException e) {
                System.err.println("Failed to parse JSON: " + e.getMessage());
            }

            return extractMessageFromJSONResponse(response.toString());
        } catch (IOException e) {
            Log.e(TAG, "Error in ChatGPT request: " + e.getMessage());
            return null;
        }
    }

    // 아래 함수 삭제?
    private static String extractMessageFromJSONResponse(String response) {
        int start = response.indexOf("content") + 11;
        int end = response.indexOf("\"", start);
        return response.substring(start, end);
    }
}