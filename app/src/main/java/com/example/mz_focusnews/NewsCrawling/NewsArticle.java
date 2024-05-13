package com.example.mz_focusnews.NewsCrawling;

/*
기사 데이터 저장
* */
public class NewsArticle {
    private String title;
    private String pubDate;
    private String publisher;
    private String link;
    private String imageUrl;
    private String body;

    public NewsArticle(String title, String pubDate, String publisher, String link, String imageUrl, String body) {
        this.title = title;
        this.pubDate = pubDate;
        this.publisher = publisher;
        this.link = link;
        this.imageUrl = imageUrl;
        this.body = body;
    }

    // Getter 메소드 추가
    public String getTitle() { return title; }
    public String getPublicationDate() { return pubDate; }
    public String getPublisher() { return publisher; }
    public String getLink() { return link; }
    public String getImageUrl() { return imageUrl; }
    public String getBody() { return body; }
}