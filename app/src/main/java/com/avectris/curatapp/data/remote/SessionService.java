package com.avectris.curatapp.data.remote;

import com.avectris.curatapp.data.remote.verify.VerifyRequest;
import com.avectris.curatapp.data.remote.verify.VerifyResponse;

import retrofit.http.Body;
import retrofit.http.POST;
import rx.Observable;

/**
 * Created by thuongle on 2/12/16.
 */
public interface SessionService {

    @POST("account/verify")
    Observable<VerifyResponse> verify(@Body VerifyRequest request);
}
