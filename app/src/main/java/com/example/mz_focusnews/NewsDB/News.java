package com.example.mz_focusnews.NewsDB;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

public class News implements Parcelable {
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

    public News(Integer newsId, int view, String link, String summary, String title, String category, String date, Integer relatedNews1, Integer relatedNews2) {
        this.newsId = newsId;
        this.view = view;
        this.link = link;
        this.summary = summary;
        this.title = title;
        this.category = category;
        this.date = date;
        this.relatedNews1 = relatedNews1;
        this.relatedNews2 = relatedNews2;
    }

    public News(Integer newsId, int view, String link, String summary, String title, String category, String date, String imgUrl, Integer relatedNews1, Integer relatedNews2) {
        this.newsId = newsId;
        this.view = view;
        this.link = link;
        this.summary = summary;
        this.title = title;
        this.category = category;
        this.date = date;
        this.imgUrl = imgUrl;
        this.relatedNews1 = relatedNews1;
        this.relatedNews2 = relatedNews2;
    }
    public News() {

    }

    protected News(Parcel in) {
        if (in.readByte() == 0) {
            newsId = null;
        } else {
            newsId = in.readInt();
        }
        view = in.readInt();
        link = in.readString();
        summary = in.readString();
        title = in.readString();
        category = in.readString();
        date = in.readString();
        if (in.readByte() == 0) {
            relatedNews1 = null;
        } else {
            relatedNews1 = in.readInt();
        }
        if (in.readByte() == 0) {
            relatedNews2 = null;
        } else {
            relatedNews2 = in.readInt();
        }
    }

    public static final Creator<News> CREATOR = new Creator<News>() {
        @Override
        public News createFromParcel(Parcel in) {
            return new News(in);
        }

        @Override
        public News[] newArray(int size) {
            return new News[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        if (newsId == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(newsId);
        }
        parcel.writeInt(view);
        parcel.writeString(link);
        parcel.writeString(summary);
        parcel.writeString(title);
        parcel.writeString(category);
        parcel.writeString(date);
        if (relatedNews1 == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(relatedNews1);
        }
        if (relatedNews2 == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(relatedNews2);
        }
    }

    private String truncateTitle(String title) {
        int dashIndex = title.indexOf('-');
        if (dashIndex != -1) {
            // "-" 문자 앞의 문자열만 사용(출판사 제거)
            return title.substring(0, dashIndex).trim();
        }
        return title;
    }

    public static News parseNewsFromJSON(JSONObject newsData) throws JSONException {
        int newsId = newsData.getInt("news_id");
        int view = newsData.getInt("view");
        String link = newsData.getString("link");
        String summary = newsData.optString("summary", "No summary available");
        String title = newsData.getString("title");
        String category = newsData.optString("category", "Uncategorized");
        String date = newsData.getString("date");
        String image = newsData.getString("img_url");
        int relatedNews1 = newsData.optInt("related_news1", 0);
        int relatedNews2 = newsData.optInt("related_news2", 0);

        return new News(newsId, view, link, summary, title, category, date, image, relatedNews1, relatedNews2);
    }

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
        return truncateTitle(title);
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