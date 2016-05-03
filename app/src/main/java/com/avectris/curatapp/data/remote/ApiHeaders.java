package com.avectris.curatapp.data.remote;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class ApiHeaders implements Interceptor {

    private String mAuthToken;
    private String mApiCode;

    public void withSession(String token) {
        mAuthToken = token;
    }

    public void withApiCode(String apiCode) {
        mApiCode = apiCode;
    }

    public void removeApiCode() {
        mApiCode = null;
    }

    public void removeSession() {
        mAuthToken = null;
    }

    public void logout() {
        mAuthToken = null;
        mApiCode = null;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        Request.Builder builder = original.newBuilder().addHeader("Accept", "application/json");
        if (mAuthToken != null) {
            builder.addHeader("token", mAuthToken);
        }
        if (mApiCode != null) {
            builder.addHeader("API-Code", mApiCode);
        }
        builder.method(original.method(), original.body());
        Request request = builder.build();
        return chain.proceed(request);
    }

    public String getApiCode() {
        return mApiCode;
    }

    public String getAuthToken() {
        return mAuthToken;
    }

    public enum OS {
        ANDROID("ANDROID"),
        IOS("IOS");

        private final String mName;

        OS(String name) {
            mName = name;
        }

        public String getName() {
            return mName;
        }
    }
}
