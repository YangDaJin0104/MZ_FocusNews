package com.example.mz_focusnews.Quiz;

import android.os.AsyncTask;
import android.util.Log;

import com.example.mz_focusnews.Ranking.RankingParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class UpdateDBQuizScore extends AsyncTask<Void, Void, Void> {
    private static final String TAG = "UpdateDBQuizScore";
    private static final String UPDATE_URL = "http://43.201.173.245/updateQuizScore.php";
    private static final String GET_URL = "http://43.201.173.245/getQuizScore.php";

    private String userId;
    private int newScore;

    public UpdateDBQuizScore(String userId, int newScore) {
        this.userId = userId;
        this.newScore = newScore;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            // 요청을 보낼 URL 설정
            URL url = new URL(UPDATE_URL);

            // 연결 설정
            HttpURLConnection connect = (HttpURLConnection) url.openConnection();
            connect.setRequestMethod("POST");
            connect.setDoOutput(true);

            // 기존 스코어에 점수 추가
            int score = getUserScore();
            newScore = score + newScore;
            Log.d(TAG, "newScore get: " + newScore);

            // POST 데이터 설정
            Map<String, String> postData = new HashMap<>();
            postData.put("userId", userId);
            postData.put("newScore", String.valueOf(newScore));
            String postScoreData = getPostDataString(postData);

            // 데이터 전송
            try (OutputStream os = connect.getOutputStream()) {
                byte[] input = postScoreData.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // 응답 코드 확인
            int responseCode = connect.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                Log.d(TAG, "Record updated successfully");
            } else {
                Log.e(TAG, "Error updating record. Response code: " + responseCode);
            }

            // 연결 종료
            connect.disconnect();

        } catch (IOException e) {
            Log.e(TAG, "Exception occurred: " + e.getMessage());
        }

        return null;
    }

    private String getPostDataString(Map<String, String> params) throws IOException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first) {
                first = false;
            } else {
                result.append("&");
            }
            result.append(entry.getKey());
            result.append("=");
            result.append(entry.getValue());
        }
        return result.toString();
    }

    private int getUserScore() {
        int previous_score;
        String response = fetchDataFromServer();

        if (response != null) {
            previous_score = RankingParser.parseScore(response, userId);
            Log.d(TAG, "previous_score get: " + previous_score);
            return previous_score;
        } else {
            Log.e(TAG, "response == null");
            return 0;
        }
    }

    private String fetchDataFromServer() {
        try {
            URL obj = new URL(GET_URL);

            // 웹 서버와 연결
            HttpURLConnection connect = (HttpURLConnection) obj.openConnection();
            connect.setRequestMethod("GET");        // request 메소드 설정 (GET 방식)
            connect.getResponseCode();

            // response 내용 읽기
            BufferedReader in = new BufferedReader(new InputStreamReader(connect.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            return response.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
