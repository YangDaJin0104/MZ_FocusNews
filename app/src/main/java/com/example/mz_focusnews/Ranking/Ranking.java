package com.example.mz_focusnews.Ranking;

public class Ranking {
    private String userId;      // 유저 아이디
    private int score;          // 점수

    public Ranking(String userId, int score){
        this.userId = userId;
        this.score = score;
    }

    public String getUserId(){ return userId; }
    public int getScore(){ return score; }
}

/*
rank attribute X

사용자 랭킹 구하는 쿼리
SET @rank=0;
SELECT @rank:=@rank+1 AS rank, user_id, score
FROM ranking
ORDER BY score DESC;
 */