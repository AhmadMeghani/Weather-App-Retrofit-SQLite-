package com.abcx.retrofit;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface JsonPlaceHolderApi {

    @GET("weather")
    Call<Example> getPosts(
            @Query("q") String cityName,
            @Query("appid") String appID
    );


    @GET("posts/{id}/comments")
    Call<List<Comment>> getComments(@Path("id")int postId);

    @POST("posts")
    Call<Post>createPost(@Body Post post);

    @FormUrlEncoded
    @POST("posts")
    Call<Post>createPost(
            @Field("userID") int userID,
            @Field("title") String title,
            @Field("body") String text
    );
}
