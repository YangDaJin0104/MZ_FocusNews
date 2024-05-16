package com.example.mz_focusnews.Ranking;

public class Ranking {
    private String userId;      // 유저 아이디
    private String userName;    // 유저 이름
    private int score;          // 점수
    private int rank;        // 순위

    public Ranking(String userId, String userName, int score, int ranking){
        this.userId = userId;
        this.userName = userName;
        this.score = score;
        this.rank = ranking;
    }

    public String getUserId(){ return userId; }
    public String getUserName(){ return userName; }
    public int getScore(){ return score; }
    public int getRank(){ return rank; }
}