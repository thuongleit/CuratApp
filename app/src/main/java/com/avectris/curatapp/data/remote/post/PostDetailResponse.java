package com.avectris.curatapp.data.remote.post;

import com.avectris.curatapp.data.remote.ErrorableResponse;
import com.avectris.curatapp.vo.Post;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by thuongle on 2/15/16.
 */
public class PostDetailResponse extends ErrorableResponse {

    @JsonProperty("postDetails")
    private Post mPost;

    public Post getPost() {
        return mPost;
    }

    public void setPost(Post post) {
        this.mPost = post;
    }
}
