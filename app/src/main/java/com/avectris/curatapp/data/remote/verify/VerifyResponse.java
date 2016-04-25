package com.avectris.curatapp.data.remote.verify;

import com.avectris.curatapp.data.remote.ErrorableResponse;
import com.avectris.curatapp.vo.Account;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by thuongle on 2/12/16.
 */
@Deprecated
public class VerifyResponse extends ErrorableResponse {
    @JsonProperty("account")
    Account mAccount;

    public Account getAccount() {
        return mAccount;
    }

    public void setAccount(Account account) {
        this.mAccount = account;
    }
}
