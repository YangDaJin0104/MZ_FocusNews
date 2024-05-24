package com.example.mz_focusnews.RelatedNews;

public class NewsData {
    private int id;
    private String title;
    private String summary;
    private int related1;
    private int related2;

    public NewsData(int id, String title, String summary, int related1, int related2) {
        this.id = id;
        this.title = title;
        this.summary = summary;
        this.related1 = related1;
        this.related2 = related2;
    }

    // 관련 기사 ID를 업데이트하는 메소드
    public void setRelatedArticles(int related1, int related2) {
        this.related1 = related1;
        this.related2 = related2;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getSummary() { return summary; }
    public int getRelated1() { return related1; }
    public int getRelated2() { return related2; }
}
