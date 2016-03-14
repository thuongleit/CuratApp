package com.avectris.curatapp.data.remote.post;

import com.avectris.curatapp.data.remote.ErrorableResponse;
import com.avectris.curatapp.data.remote.vo.AccountPost;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by thuongle on 2/13/16.
 */
public class PostResponse extends ErrorableResponse {
    @JsonProperty("account")
    AccountPost mResult;

    public AccountPost getResult() {
        return mResult;
    }

    public void setResult(AccountPost result) {
        this.mResult = result;
    }
}
