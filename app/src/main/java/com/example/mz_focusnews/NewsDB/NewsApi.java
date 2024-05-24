package com.example.mz_focusnews.NewsDB;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.DELETE;
import retrofit2.http.Path;

public interface NewsApi {
    @GET("/api/news")
    Call<List<News>> getAllNews();

    @GET("/api/news/category/{category}")
    Call<List<News>> getNewsByCategory(@Path("category") String category);

    @POST("/api/news")
    Call<News> createNews(@Body News news);

    @GET("/api/news/{id}")
    Call<News> getNewsById(@Path("id") Integer id);

    @PUT("/api/news/{id}")
    Call<News> updateNews(@Path("id") Integer id, @Body News news);

    @DELETE("/api/news/{id}")
    Call<Void> deleteNews(@Path("id") Integer id);
}
