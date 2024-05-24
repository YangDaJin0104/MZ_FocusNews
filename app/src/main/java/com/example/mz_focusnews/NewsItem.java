//package com.example.mz_focusnews;
//
//import android.os.Parcel;
//import android.os.Parcelable;
//
//import androidx.annotation.NonNull;
//
//public class NewsItem implements Parcelable {
//
//    private int newsId;
//    private String title;
//    private String publisher;
//    private String summary;
//    private String date;
//    private String category;
//
//
//    // usage: 오늘/이주/이달의 뉴스
//    public NewsItem(int newsId, String title, String summary) {
//        this.newsId = newsId;
//        this.title = title;
//        this.summary = summary;
//    }
//
//    public NewsItem(int newsId, String title, String publisher, String summary, String date, String category) {
//        this.newsId = newsId;
//        this.title = title;
//        this.publisher = publisher;
//        this.date = date;
//        this.summary = summary;
//        this.category = category;
//    }
//
//    // 사용자 맞춤형 뉴스 추천
//    public NewsItem(int newsId, String title, String summary, String date, String category) {
//        this.newsId = newsId;
//        this.title = title;
//        this.summary = summary;
//        this.category = category;
//        this.date = date;
//    }
//
//    /**
//     * getter and setter
//     */
//    public int getNewsId() {
//        return newsId;
//    }
//
//    public void setNewsId(int newsId) {
//        this.newsId = newsId;
//    }
//
//    public String getTitle() {
//        return truncateTitle(title); // 뒤에 출판사 제거한 스트링 반환
//    }
//
//    public void setTitle(String title) {
//        this.title = title;
//    }
//
//    public String getPublisher() {
//        return publisher;
//    }
//
//    public void setPublisher(String publisher) {
//        this.publisher = publisher;
//    }
//
//    public String getDate() {
//        return date;
//    }
//
//    public void setDate(String date) {
//        this.date = date;
//    }
//
//    public String getSummary() {
//        return summary;
//    }
//
//    public void setSummary(String summary) {
//        this.summary = summary;
//    }
//
//    public String getCategory() {
//        return category;
//    }
//
//    public void setCategory(String category) {
//        this.category = category;
//    }
//
//    /**
//     * override
//     */
//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    @Override
//    public void writeToParcel(@NonNull Parcel dest, int flags) {
//        dest.writeInt(newsId);
//        dest.writeString(title);
//        dest.writeString(publisher);
//        dest.writeString(date);
//        dest.writeString(summary);
//        dest.writeString(category);
//    }
//
//    public void readFromParcel(Parcel in) {
//        newsId = in.readInt();
//        title = in.readString();
//        publisher = in.readString();
//        date = in.readString();
//        summary = in.readString();
//        category = in.readString();
//    }
//
//    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
//        public NewsItem createFromParcel(Parcel in) {
//            return new NewsItem(in.readInt(), in.readString(), in.readString());
//        }
//
//        public NewsItem[] newArray(int size) {
//            return new NewsItem[size];
//        }
//    };
//
//    private String truncateTitle(String title) {
//        int dashIndex = title.indexOf('-');
//        if (dashIndex != -1) {
//            // "-" 문자 앞의 문자열만 사용(출판사 제거)
//            return title.substring(0, dashIndex).trim();
//        }
//        return title;
//    }
//}
