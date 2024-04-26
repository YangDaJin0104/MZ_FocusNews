package com.example.mz_focusnews;

public class NewsArticle {
    private String title;
    private String source;
    private String time;
    private String url;
    private String content;

    public NewsArticle(String title, String source, String time, String url) {
        this.title = title;
        this.source = source;
        this.time = time;
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public String getSource() {
        return source;
    }

    public String getTime() {
        return time;
    }

    public String getUrl() {
        return url;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
