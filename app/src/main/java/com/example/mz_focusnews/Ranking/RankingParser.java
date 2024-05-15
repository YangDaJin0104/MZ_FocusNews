package com.example.mz_focusnews.Ranking;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RankingParser {
    private static final String TAG = "RankingParser";
    private static final String USER_ID = "user5";      // 현재 앱 사용자(테스트용 하드코딩) - 로그인 후 정보 받아옴

    public static List<Ranking> parseRanking(String json) {
        List<Ranking> rankings = new ArrayList<>();

        try {
            JSONArray jsonArray = new JSONArray(json);

            for (int i = 0; rankings.size() <= 3; i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                String userId = jsonObject.optString("user_id");
                int score = jsonObject.optInt("quiz_score");
                int rank = jsonObject.optInt("rank");

                // Ranking 객체를 생성해 데이터 저장 - 1, 2, 3등
                if(rank == 1 || rank == 2 || rank == 3){
                    Ranking ranking = new Ranking(userId, score, rank);
                    rankings.add(ranking);
                } else{
                    // 사용자 랭킹 정보가 있는지 확인 (없을 경우 추가)
                    for(int j=0; j<rankings.size(); j++){
                        if (USER_ID.equals(rankings.get(j).getUserId())) {     // 이미 유저의 등수가 1~3등 안에 있다면 더이상 객체 생성 X
                            return rankings;
                        }
                    }
                    Ranking ranking = new Ranking(userId, score, rank);
                    rankings.add(ranking);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return rankings;
    }
}
