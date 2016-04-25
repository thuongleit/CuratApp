package com.avectris.curatapp.data.remote.verify;

import com.avectris.curatapp.data.remote.ErrorableResponse;
import com.avectris.curatapp.vo.User;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by thuongle on 4/25/16.
 */
public class LoginResponse extends ErrorableResponse {

    @JsonProperty("user")
    public User user;
}
