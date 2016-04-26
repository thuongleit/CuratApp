package com.avectris.curatapp.data.remote;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class ApiHeaders implements Interceptor {

    private String mEmail;
    private String mPassword;
    private String mDeviceId;
    private String mVersion;
    private String mOS;
    private String mAuthToken;
    private String mApiCode;

    public void setLoginHeaders(String email, String password, String token, String version) {
        mEmail = email;
        mPassword = password;
        mVersion = version;
        mOS = OS.ANDROID.getName();
        mAuthToken = null;
        mDeviceId = token;
        mApiCode = null;
    }

    public void buildSession(String authToken, String gcmToken, String version) {
        mAuthToken = authToken;
        mDeviceId = gcmToken;
        mVersion = version;
        mOS = OS.ANDROID.getName();
        mEmail = null;
        mPassword = null;
        mApiCode = null;
    }

    public void withApiCode(String apiCode, String token, String version) {
        mApiCode = apiCode;
        mDeviceId = token;
        mVersion = version;
        mOS = OS.ANDROID.getName();
        mEmail = null;
        mPassword = null;
        mAuthToken = null;
    }

    public void removeApiCode(){
        mApiCode = null;
    }

    public void logout() {
        mEmail = null;
        mPassword = null;
        mDeviceId = null;
        mOS = null;
        mVersion = null;
        mAuthToken = null;
        mApiCode = null;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        Request.Builder builder = original.newBuilder().addHeader("Accept", "application/json");
        if (mEmail != null) {
            builder.addHeader("email", mEmail);
        }
        if (mPassword != null) {
            builder.addHeader("password", mPassword);
        }
        if (mDeviceId != null) {
            builder.addHeader("deviceid", mDeviceId);
        }
        if (mOS != null) {
            builder.addHeader("os", mOS);
        }
        if (mVersion != null) {
            builder.addHeader("version", mVersion);
        }
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

    private enum OS {
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
