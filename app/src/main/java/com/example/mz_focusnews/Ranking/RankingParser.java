package com.example.mz_focusnews.Ranking;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RankingParser {
    private static final String TAG = "RankingParser";
    private static final String USER_ID = "user4";      // 현재 앱 사용자(테스트용 하드코딩)

    public static List<Ranking> parseRanking(String json) {
        List<Ranking> rankings = new ArrayList<>();

        try {
            JSONArray jsonArray = new JSONArray(json);
            
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                String userId = jsonObject.optString("user_id");
                int score = jsonObject.optInt("score");

                // Ranking 객체를 생성해 데이터 저장 - 1, 2, 3등, 사용자 랭킹 정보만 리스트에 저장
                if(i < 3 || userId.equals(USER_ID)){
                    Ranking ranking = new Ranking(userId, score);
                    rankings.add(ranking);

                    Log.d(TAG, i+1 + "등: userId = " + ranking.getUserId() + " score = " + ranking.getScore());   // 테스트
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return rankings;
    }
}
