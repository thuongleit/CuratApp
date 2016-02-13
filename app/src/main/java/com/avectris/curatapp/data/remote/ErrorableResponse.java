package com.avectris.curatapp.data.remote;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by thuongle on 2/12/16.
 */
public class ErrorableResponse {

    @JsonProperty("mSuccess")
    boolean mSuccess;
    @JsonProperty("error")
    String mErrorMsg;

    public boolean isSuccess() {
        return mSuccess;
    }

    public void setSuccess(boolean success) {
        this.mSuccess = success;
    }

    public String getErrorMsg() {
        return mErrorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.mErrorMsg = errorMsg;
    }
}
