package com.avectris.curatapp.data.remote;

import com.avectris.curatapp.data.remote.verify.VerifyRequest;
import com.avectris.curatapp.data.remote.verify.VerifyResponse;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by thuongle on 2/12/16.
 */
public interface SessionService {

    @POST("account/verify")
    Observable<VerifyResponse> verify(@Body VerifyRequest request);

    @GET("updatepushnotification")
    Observable<ErrorableResponse> enablePushNotification(@Query("device_id") String deviceId, @Query("schedule_id") String scheduleId);

    @GET("updatepushnotification")
    Observable<ErrorableResponse> disablePushNotification(@Query("remove_device_id") String removeDeviceId, @Query("schedule_id") String scheduleId);

    @GET("updatepushnotification")
    Observable<ErrorableResponse> updateDeviceId(@Query("device_id") String deviceId, @Query("remove_device_id") String removeDeviceId, @Query("schedule_id") String scheduleId);
}


