package com.example.mz_focusnews.Ranking;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RankingParser {
    private static final String TAG = "RankingParser";
    private static final String USER_ID = "coddl";      // 현재 앱 사용자(테스트용 하드코딩) - 로그인 후 정보 받아옴

    public static List<Ranking> parseRanking(String json) {
        List<Ranking> rankings = new ArrayList<>();

        try {
            JSONArray jsonArray = new JSONArray(json);

            // 1~3등 랭킹 정보 저장
            for (int i = 0; rankings.size() < 3; i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                String userId = jsonObject.optString("user_id");
                String userName = jsonObject.optString("user_name");
                int score = jsonObject.optInt("quiz_score");
                int rank = jsonObject.optInt("rank");

                Ranking ranking = new Ranking(userId, userName, score, rank);
                rankings.add(ranking);
            }

            // 사용자 랭킹 정보가 1~3등 내에 있는지 확인
            for(int i=0; i < 3; i++){
                if(USER_ID.equals(rankings.get(i).getUserId())){
                    return rankings;        // 이미 1~3등 내에 있을 경우 리턴
                }
            }

            // 사용자 랭킹 정보 저장
            for (int j = 3; j < jsonArray.length(); j++) {
                JSONObject jsonObject = jsonArray.getJSONObject(j);
                String userId = jsonObject.optString("user_id");

                // JSON에서 사용자 랭킹 정보를 찾은 경우 Ranking 객체 생성
                if (USER_ID.equals(userId)) {
                    String userName = jsonObject.optString("user_name");
                    int score = jsonObject.optInt("quiz_score");
                    int rank = jsonObject.optInt("rank");

                    Ranking ranking = new Ranking(userId, userName, score, rank);
                    rankings.add(ranking);
                    break;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return rankings;
    }

    // UpdateDBQuizScore.java에서 사용하는 getScore 함수
    public static int parseScore(String json) {
        try {
            JSONArray jsonArray = new JSONArray(json);

            // 사용자의 기존 score 찾기
            for (int j = 0; j < jsonArray.length(); j++) {
                JSONObject jsonObject = jsonArray.getJSONObject(j);
                String userId = jsonObject.optString("user_id");

                if (USER_ID.equals(userId)) {
                    return  jsonObject.optInt("quiz_score");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return 0;
    }
}
