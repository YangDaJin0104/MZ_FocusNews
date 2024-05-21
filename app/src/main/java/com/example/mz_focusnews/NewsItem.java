package com.example.mz_focusnews;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class NewsItem implements Parcelable {

    private int newsId;
    private String title; // 뉴스 제목
    private String publisher; // 출판사
    private String date; // 게시 시간
    private String summary;
    private String category;


    // usage: 오늘/이주/이달의 뉴스
    public NewsItem(int newsId, String title, String summary) {
        this.newsId = newsId;
        this.title = title;
        this.summary = summary;
    }

    // usage: 카테고리 별 뉴스 목록
//    public NewsItem(int newsId, String title, String publisher, String time) {
//        this.newsId = newsId;
//        this.title = title;
//        this.publisher = publisher;
//        this.time = time;
//    }


    // usage: 사용자 맞춤형 뉴스 추천
    public NewsItem(int newsId, String title, String summary, String date) {
        this.newsId = newsId;
        this.title = title;
        this.date = date;
        this.summary = summary;
    }


    // 카테고리 표시
    public NewsItem(int newsId, String title, String publisher, String date, String summary, String category) {
        this.newsId = newsId;
        this.title = title;
        this.publisher = publisher;
        this.date = date;
        this.summary = summary;
        this.category = category;
    }

    // 사용자 맞춤형 뉴스 추천
    public NewsItem(int newsId, String title, String summary, String category, String date) {
        this.newsId = newsId;
        this.title = title;
        this.summary = summary;
        this.category = category;
        this.date = date;

    }

    /**
     * getter and setter
     */
    public int getNewsId() {
        return newsId;
    }

    public void setNewsId(int newsId) {
        this.newsId = newsId;
    }

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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * override
     */
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(newsId);
        dest.writeString(title);
        dest.writeString(publisher);
        dest.writeString(date);
        dest.writeString(summary);
        dest.writeString(category);
    }

    public void readFromParcel(Parcel in) {
        newsId = in.readInt();
        title = in.readString();
        publisher = in.readString();
        date = in.readString();
        summary = in.readString();
        category = in.readString();
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public NewsItem createFromParcel(Parcel in) {
            return new NewsItem(in.readInt(), in.readString(), in.readString(), in.readString());
        }

        public NewsItem[] newArray(int size) {
            return new NewsItem[size];
        }
    };
}
