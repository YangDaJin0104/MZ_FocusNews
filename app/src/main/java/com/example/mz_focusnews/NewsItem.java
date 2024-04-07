package com.example.mz_focusnews;

public class NewsItem {
    private String title; // 뉴스 제목
    private String publisher; // 출판사
    private String time; // 게시 시간

    // 생성자
    public NewsItem(String title, String publisher, String time) {
        this.title = title;
        this.publisher = publisher;
        this.time = time;
    }

    // 제목에 대한 getter
    public String getTitle() {
        return title;
    }

    // 제목에 대한 setter
    public void setTitle(String title) {
        this.title = title;
    }

    // 출판사에 대한 getter
    public String getPublisher() {
        return publisher;
    }

    // 출판사에 대한 setter
    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    // 시간에 대한 getter
    public String getTime() {
        return time;
    }

    // 시간에 대한 setter
    public void setTime(String time) {
        this.time = time;
    }
}
