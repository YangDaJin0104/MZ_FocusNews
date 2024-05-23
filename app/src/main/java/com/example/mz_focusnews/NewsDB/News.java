package com.example.mz_focusnews.NewsDB;

import java.time.LocalDateTime;
import java.util.Date;

public class News {
    private Integer newsId;
    private int view;
    private String link;
    private String summary;
    private String title;
    private String category;
    private String date;
    private Integer relatedNews1;
    private Integer relatedNews2;
    private String publish;
    private String imgUrl;

    // Getters and setters
    public Integer getNewsId() {
        return newsId;
    }

    public void setNewsId(Integer newsId) {
        this.newsId = newsId;
    }

    public int getView() {
        return view;
    }

    public void setView(int view) {
        this.view = view;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Integer getRelatedNews1() {
        return relatedNews1;
    }

    public void setRelatedNews1(Integer relatedNews1) {
        this.relatedNews1 = relatedNews1;
    }

    public Integer getRelatedNews2() {
        return relatedNews2;
    }

    public void setRelatedNews2(Integer relatedNews2) {
        this.relatedNews2 = relatedNews2;
    }

    public String getPublish() {
        return publish;
    }

    public void setPublish(String publish) {
        this.publish = publish;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

}
