package com.example.mz_focusnews;

public class NewsItem {

    // 뉴스 이미지 변수 추가 예정
    // private String image;
    private String title; // 뉴스 제목
    private String publisher; // 출판사
    private String time; // 게시 시간

    // 생성자
    public NewsItem(String title, String publisher, String time) {
        this.title = title;
        this.publisher = publisher;
        this.time = time;
    }

    /**
     * getter and setter
     */
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
