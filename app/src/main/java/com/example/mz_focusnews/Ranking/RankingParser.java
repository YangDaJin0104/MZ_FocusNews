package com.example.mz_focusnews.Ranking;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RankingParser {
    private static final String TAG = "RankingParser";

    public static List<Ranking> parseRanking(String json) {
        List<Ranking> rankings = new ArrayList<>();

        try {
            JSONArray jsonArray = new JSONArray(json);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                String userId = jsonObject.optString("user_id");
                int score = jsonObject.optInt("score");

                Log.d(TAG, "*** userId = " + userId + " score = " + score);     // DB로부터 가져온 데이터 확인

                // Ranking 객체를 생성해 데이터 저장
                Ranking ranking = new Ranking(userId, score);

                rankings.add(ranking);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return rankings;
    }
}
