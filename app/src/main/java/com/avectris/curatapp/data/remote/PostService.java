package com.avectris.curatapp.data.remote;

import com.avectris.curatapp.data.remote.post.PostDetailResponse;
import com.avectris.curatapp.data.remote.post.PostResponse;

import retrofit.http.GET;
import retrofit.http.Query;
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
}
