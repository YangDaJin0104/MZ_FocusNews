package com.example.mz_focusnews.NewsDB;

import com.example.mz_focusnews.UsersDB.UserApi;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static RetrofitClient instance = null;
    private NewsApi newsApi; // News
    private UserApi userApi; // User

    private RetrofitClient() {
        // 로그 인터셉터 설정
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        // OkHttpClient 설정
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build();

        // Retrofit 설정
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://43.201.173.245:8081") // 에뮬레이터에서 호스트 머신의 IP 주소
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // NewsApi 인스턴스 생성
        newsApi = retrofit.create(NewsApi.class);
        // UsersApi 인스턴스 생성
        userApi = retrofit.create(UserApi.class);

    }

    // RetrofitClient의 싱글톤 인스턴스를 반환
    public static synchronized RetrofitClient getInstance() {
        if (instance == null) {
            instance = new RetrofitClient();
        }
        return instance;
    }

    public NewsApi getNewsApi() {
        return newsApi;
    }
    public UserApi getUserApi() {
        return userApi;
    }
}
