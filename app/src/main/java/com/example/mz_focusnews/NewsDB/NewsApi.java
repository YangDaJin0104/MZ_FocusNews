package com.example.mz_focusnews.NewsDB;

import android.bluetooth.BluetoothProfile;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.DELETE;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface NewsApi {
    @GET("/api/news")
    Call<List<News>> getAllNews();

    @GET("/api/news/category/{category}")
    Call<List<News>> getNewsByCategory(@Path("category") String category, @Query("sort") String sort);

    @GET("/api/news/breaking")
    Call<List<News>> getBreakingNewsWithKeyword(@Query("limit") int limit, @Query("keyword") String keyword);

    @FormUrlEncoded
    @POST("/api/saveKeyword")
    Call<Void> saveKeyword(@Field("userId") String userId, @Field("keyword") String keyword);

    @GET("/api/news/searchByKeywords")
    Call<List<News>> getNewsByKeywords(@Query("userId") String userId);

    @POST("/api/news")
    Call<News> createNews(@Body News news);

    @GET("/api/news/{id}")
    Call<News> getNewsById(@Path("id") Integer id);

    @PUT("/api/news/{id}")
    Call<News> updateNews(@Path("id") Integer id, @Body News news);

    @DELETE("/api/news/{id}")
    Call<Void> deleteNews(@Path("id") Integer id);
}

