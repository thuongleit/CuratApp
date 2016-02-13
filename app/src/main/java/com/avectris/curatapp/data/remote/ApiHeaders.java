package com.avectris.curatapp.data.remote;

import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

public class ApiHeaders implements Interceptor {

    private String mApiCode;

    public void withSession(String apiCode) {
        this.mApiCode = apiCode;
    }

    public void logout() {
        mApiCode = null;
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
