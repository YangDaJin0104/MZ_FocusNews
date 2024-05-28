package com.example.mz_focusnews.UsersDB;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UserApi {

    @GET("/api/users/{userId}")
    Call<User> getUser(@Path("userId") String userId);

    @GET("/api/users")
    Call<List<User>> getAllUsers();

    @POST("/api/users")
    Call<User> createUser(@Body User user);

    @PUT("/api/users/{userId}")
    Call<User> updateUser(@Path("userId") String userId, @Body User userDetails);

    @DELETE("/api/users/{userId}")
    Call<Void> deleteUser(@Path("userId") String userId);

    @FormUrlEncoded
    @POST("/api/users/saveKeyword")
    Call<Void> saveKeyword(@Field("userId") String userId, @Field("keyword") String keyword, @Field("keywordIndex") int keywordIndex);
}

