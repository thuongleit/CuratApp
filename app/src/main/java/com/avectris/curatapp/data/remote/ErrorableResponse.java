package com.avectris.curatapp.data.remote;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by thuongle on 2/12/16.
 */
public class ErrorableResponse {

    @JsonProperty("response")
    String mResponse;
    @JsonProperty("error")
    String mErrorMsg;

    public boolean isSuccess() {
        return "Success".equals(mResponse.trim());
    }

    public void setResponse(String response) {
        this.mResponse = response;
    }

    public String getErrorMsg() {
        return mErrorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.mErrorMsg = errorMsg;
    }
}
