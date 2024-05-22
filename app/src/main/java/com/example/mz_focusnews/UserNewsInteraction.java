package com.example.mz_focusnews;

/**
 * 사용자 뉴스 상호작용을 기록하는 클래스
 */
public class UserNewsInteraction {
    private int newsId; // 뉴스 아이디
    private String category; // 뉴스 카테고리
    private long timestamp; // 상호작용 발생 시간 (타임스탬프)

    public UserNewsInteraction(int newsId, String category, long timestamp) {
        this.newsId = newsId;
        this.category = category;
        this.timestamp = timestamp;
    }

    public String getCategory() {
        return category;
    }

    public long getTimestamp() {
        return timestamp;
    }
}

