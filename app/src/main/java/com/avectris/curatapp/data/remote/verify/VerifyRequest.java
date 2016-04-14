package com.avectris.curatapp.data.remote.verify;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by thuongle on 2/12/16.
 */
public class VerifyRequest {

    @JsonProperty("verification_code")
    String mVerifyCode;

    public VerifyRequest(String verifyCode) {
        this.mVerifyCode = verifyCode;
    }

    public String getVerifyCode() {
        return mVerifyCode;
    }

    public void setVerifyCode(String verifyCode) {
        this.mVerifyCode = verifyCode;
    }
}
