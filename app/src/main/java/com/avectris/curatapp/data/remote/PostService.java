package com.avectris.curatapp.data.remote;

import com.avectris.curatapp.data.remote.post.PostDetailResponse;
import com.avectris.curatapp.data.remote.post.PostResponse;
import okhttp3.MultipartBody;
import retrofit2.http.*;
import rx.Observable;

/**
 * Created by thuongle on 2/13/16.
 */
public interface PostService {

    @GET("account/upcoming_post")
    Observable<PostResponse> getUpcomingPosts(@Query("page") int pageNumber, @Query("deviceid") String deviceId, @Query("version") String version, @Query("os") String os);

    @GET("account/passed_post")
    Observable<PostResponse> getPassedPosts(@Query("page") int pageNumber, @Query("deviceid") String deviceId, @Query("version") String version, @Query("os") String os);

    @GET("posts/get")
    Observable<PostDetailResponse> fetchPostDetail(@Query("id") String postId, @Query("deviceid") String deviceId, @Query("version") String version, @Query("os") String os);

    @GET("updateuserposted")
    Observable<ErrorableResponse> updateUserPosted(@Query("id") String postId, @Query("user_posted") int userPosted,  @Query("deviceid") String deviceId, @Query("version") String version, @Query("os") String os);

    @POST("account/deletepost")
    Observable<ErrorableResponse> deletePost(@Query("deviceid") String deviceId, @Query("version") String version, @Query("os") String os, @Query("postId") String postId);

    @Multipart
    @POST("post/uploadfile")
    Observable<ErrorableResponse> addPostToLibrary(@Query("accountId") String accountId,
                                                   @Query("deviceid") String deviceId,
                                                   @Query("version") String version,
                                                   @Query("os") String os,
                                                   @Query("caption") String caption,
                                                   @Part MultipartBody.Part file);

    @Multipart
    @POST("post/uploadaddtoscheduleexact")
    Observable<ErrorableResponse> addPostOnExactTime(@Query("accountId") String accountId,
                                                     @Query("deviceid") String deviceId,
                                                     @Query("version") String version,
                                                     @Query("os") String os,
                                                     @Query("caption") String caption,
                                                     @Query("exactDateTime") String updateTime,
                                                     @Part MultipartBody.Part file);

    @Multipart
    @POST("post/uploadaddtoschedule")
    Observable<ErrorableResponse> addPostToSchedule(@Query("accountId") String accountId,
                                                    @Query("deviceid") String deviceId,
                                                    @Query("version") String version,
                                                    @Query("os") String os,
                                                    @Query("caption") String caption,
                                                    @Part MultipartBody.Part file);
}
