package com.example.mz_focusnews;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class NewsItem implements Parcelable {

    // 뉴스 이미지 변수 추가 예정
    // private String image;

    private int newsId;
    private String title; // 뉴스 제목
    private String publisher; // 출판사
    private String time; // 게시 시간

    // 생성자
    public NewsItem(int newsId, String title, String publisher, String time) {
        this.newsId = newsId;
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

    public int getNewsId() {
        return newsId;
    }

    public void setNewsId(int newsId) {
        this.newsId = newsId;
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
        dest.writeString(time);
    }

    public void readFromParcel(Parcel in) {
        newsId = in.readInt();
        title = in.readString();
        publisher = in.readString();
        time = in.readString();
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
