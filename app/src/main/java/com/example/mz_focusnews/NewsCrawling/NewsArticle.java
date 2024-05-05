package com.example.mz_focusnews.NewsCrawling;

/*
기사 데이터 저장
* */
public class NewsArticle {
    private String title;
    private String publicationDate;
    private String url;
    private String content;

    public NewsArticle(String title, String publicationDate, String url) {
        this.title = title;
        this.publicationDate = publicationDate;
        this.url = url;
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

    public String getUrl() {
        return url;
    }

    public String getContent() {
        return content;
    }
}