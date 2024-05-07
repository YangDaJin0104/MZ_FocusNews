package com.example.mz_focusnews.NewsCrawling;

/*
기사 데이터 저장
* */
public class NewsArticle {
    private String title; // 기사 제목
    private String publicationDate; // 기사 발행 일자
    private String publisher; // 출판사
    //private String url;
    private String content;

    public NewsArticle(String title, String publicationDate, String publisher) {
        this.title = title;
        this.publicationDate = publicationDate;
        this.publisher = publisher;
        //this.url = url;
        this.content = "";
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public String getPublicationDate() {
        return publicationDate;
    }
    public String getPublisher() {
        return publisher;
    }

    /*public String getUrl() {
        return url;
    }*/

    public String getContent() {
        return content;
    }
}