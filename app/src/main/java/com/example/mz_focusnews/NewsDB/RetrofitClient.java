package com.example.mz_focusnews.NewsDB;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static RetrofitClient instance = null;
    private NewsApi newsApi;

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
                .baseUrl("http://10.0.2.2:8081") // 에뮬레이터에서 호스트 머신의 IP 주소
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // NewsApi 인스턴스 생성
        newsApi = retrofit.create(NewsApi.class);
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
}
