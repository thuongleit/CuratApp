package com.avectris.curatapp.data.remote;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class ApiHeaders implements Interceptor {

    private String mApiCode;

    public void withSession(String apiCode) {
        this.mApiCode = apiCode;
    }

    public void logout() {
        mApiCode = null;
    }

    public String getApiCode() {
        return mApiCode;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        Request.Builder builder = original.newBuilder()
                .addHeader("Accept", "application/json");
        if (mApiCode != null) {
            builder.addHeader("API-Code", mApiCode);

        }
        builder.method(original.method(), original.body());
        Request request = builder.build();
        return chain.proceed(request);
    }
}
