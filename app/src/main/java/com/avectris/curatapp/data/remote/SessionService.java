package com.avectris.curatapp.data.remote;

import com.avectris.curatapp.data.remote.verify.AccountResponse;
import com.avectris.curatapp.data.remote.verify.LoginResponse;
import com.avectris.curatapp.data.remote.verify.VerifyRequest;
import com.avectris.curatapp.data.remote.verify.VerifyResponse;

import retrofit2.http.*;
import rx.Observable;

/**
 * Created by thuongle on 2/12/16.
 */
public interface SessionService {

    @Deprecated
    @POST("account/verify")
    Observable<VerifyResponse> verify(@Body VerifyRequest request);

    @GET("updatepushnotification")
    Observable<ErrorableResponse> enablePushNotification(@Query("device_id") String deviceId, @Query("schedule_id") String scheduleId);

    @GET("updatepushnotification")
    Observable<ErrorableResponse> disablePushNotification(@Query("remove_device_id") String removeDeviceId, @Query("schedule_id") String scheduleId);

    @GET("updatepushnotification")
    Observable<ErrorableResponse> updateDeviceId(@Query("device_id") String deviceId, @Query("remove_device_id") String removeDeviceId, @Query("schedule_id") String scheduleId);

    @POST("user/login")
    Observable<LoginResponse> login();

    @GET("accounts")
    Observable<AccountResponse> fetchAccounts();
}


