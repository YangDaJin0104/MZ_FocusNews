package com.example.mz_focusnews.UsersDB;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.List;

public class User implements Parcelable {
    private String userId;
    private String userName;
    private String userPw;
    private int locationPermission; // 1 또는 0으로 저장됨
    private int alarmPermission; // 1 또는 0으로 저장됨
    private int quizScore;
    private List<String> interestCategory;
    private Double latitude;
    private Double longitude;

    public User(String userId, String userName, String userPw, int locationPermission, int alarmPermission, int quizScore, List<String> interestCategory, Double latitude, Double longitude) {
        this.userId = userId;
        this.userName = userName;
        this.userPw = userPw;
        this.locationPermission = locationPermission;
        this.alarmPermission = alarmPermission;
        this.quizScore = quizScore;
        this.interestCategory = interestCategory;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPw() {
        return userPw;
    }

    public void setUserPw(String userPw) {
        this.userPw = userPw;
    }

    public int getLocationPermission() {
        return locationPermission;
    }

    public void setLocationPermission(int locationPermission) {
        this.locationPermission = locationPermission;
    }

    public int getAlarmPermission() {
        return alarmPermission;
    }

    public void setAlarmPermission(int alarmPermission) {
        this.alarmPermission = alarmPermission;
    }

    public int getQuizScore() {
        return quizScore;
    }

    public void setQuizScore(int quizScore) {
        this.quizScore = quizScore;
    }

    public List<String> getInterestCategory() {
        return interestCategory;
    }

    public void setInterestCategory(List<String> interestCategory) {
        this.interestCategory = interestCategory;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    protected User(Parcel in) {
        userId = in.readString();
        userName = in.readString();
        userPw = in.readString();
        locationPermission = in.readInt();
        alarmPermission = in.readInt();
        quizScore = in.readInt();
        interestCategory = in.createStringArrayList();
        if (in.readByte() == 0) {
            latitude = null;
        } else {
            latitude = in.readDouble();
        }
        if (in.readByte() == 0) {
            longitude = null;
        } else {
            longitude = in.readDouble();
        }
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(userId);
        parcel.writeString(userName);
        parcel.writeString(userPw);
        parcel.writeInt(locationPermission);
        parcel.writeInt(alarmPermission);
        parcel.writeInt(quizScore);
        parcel.writeStringList(interestCategory);
        if (latitude == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeDouble(latitude);
        }
        if (longitude == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeDouble(longitude);
        }
    }
}
