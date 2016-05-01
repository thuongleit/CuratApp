package com.avectris.curatapp.data.remote;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by thuongle on 2/12/16.
 */
public class ErrorableResponse {

    @JsonProperty("response")
    String mResponse;
    @JsonProperty("message")
    String mErrorMsg;
    @JsonProperty("errors")
    String mErrors;

    public boolean isSuccess() {
        return "Success".equals(mResponse.trim());
    }

    public void setResponse(String response) {
        this.mResponse = response;
    }

    public String getMessage() {
        return (mErrorMsg != null) ? mErrorMsg : mErrors;
    }

    public void setErrorMsg(String errorMsg) {
        this.mErrorMsg = errorMsg;
    }

    public void setErrors(String errors) {
        this.mErrors = errors;
    }
}
