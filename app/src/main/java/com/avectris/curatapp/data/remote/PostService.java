package com.avectris.curatapp.data.remote;

import com.avectris.curatapp.data.remote.post.PostDetailResponse;
import com.avectris.curatapp.data.remote.post.PostResponse;
import okhttp3.RequestBody;
import retrofit2.http.*;
import rx.Observable;

/**
 * Created by thuongle on 2/13/16.
 */
public interface PostService {

    @GET("account/upcoming_post")
    Observable<PostResponse> getUpcomingPosts(@Query("page") int pageNumber);

    @GET("account/passed_post")
    Observable<PostResponse> getPassedPosts(@Query("page") int pageNumber);

    @GET("posts/get")
    Observable<PostDetailResponse> getPostDetail(@Query("id") String postId);

    @GET("updateuserposted")
    Observable<ErrorableResponse> updateUserPosted(@Query("id") String postId, @Query("user_posted") int userPosted);

    @POST("account/deletepost")
    Observable<ErrorableResponse> deletePost();

    @Multipart
    @POST("post/uploadfile")
    Observable<ErrorableResponse> addPostToLibrary(@Part("uploadFile\"; filename=\"1.*\"") RequestBody file);

    @Multipart
    @POST("post/uploadaddtoscheduleexact")
    Observable<ErrorableResponse> addPostOnExactTime(@Part("uploadFile\"; filename=\"1.*\"") RequestBody file);

    @Multipart
    @POST("post/uploadaddtoschedule")
    Observable<ErrorableResponse> addPostToSchedule(@Part("uploadFile\"; filename=\"1.*\"") RequestBody file);
}
