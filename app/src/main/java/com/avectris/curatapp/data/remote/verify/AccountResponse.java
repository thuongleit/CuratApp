package com.avectris.curatapp.data.remote.verify;

import com.avectris.curatapp.data.remote.ErrorableResponse;
import com.avectris.curatapp.vo.Account;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

/**
 * Created by thuongle on 4/25/16.
 */
public class AccountResponse extends ErrorableResponse {

    @JsonProperty("accounts")
    public ArrayList<Account> accounts;
}
