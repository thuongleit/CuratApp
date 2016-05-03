/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.avectris.curatapp.data;

import com.avectris.curatapp.config.Config;
import com.avectris.curatapp.config.Constant;
import com.avectris.curatapp.data.remote.ApiHeaders;
import com.avectris.curatapp.data.remote.PostService;
import com.avectris.curatapp.data.remote.SessionService;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.CallAdapter;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

@Module
public class ApiModule {

    public ApiModule() {
    }

    @Provides
    @Singleton
    public Converter.Factory provideConverter() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return JacksonConverterFactory.create(mapper);
    }

    @Provides
    @Singleton
    public CallAdapter.Factory provideCallAdapter() {
        return RxJavaCallAdapterFactory.create();
    }

    @Provides
    @Singleton
    public Retrofit provideRestAdapter(OkHttpClient okHttpClient, Converter.Factory converter, CallAdapter.Factory callAdapter, Config config) {
        String url;
        if (config.isUseBeta()) {
            url = Constant.BETA_API_END_POINT;
        } else {
            url = Constant.API_END_POINT;
        }
        return new Retrofit.Builder()
                .baseUrl(url)
                .client(okHttpClient)
                .addConverterFactory(converter)
                .addCallAdapterFactory(callAdapter)
                .build();
    }

    @Provides
    @Singleton
    public OkHttpClient provideOkHttpClient(ApiHeaders apiHeaders) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        // add your other interceptors …
        builder.interceptors().clear();
        builder.interceptors().add(apiHeaders);

        return builder.build();
    }

    @Provides
    @Singleton
    public SessionService provideSessionService(Retrofit retrofit) {
        return retrofit.create(SessionService.class);
    }

    @Provides
    @Singleton
    public PostService providePostService(Retrofit retrofit){
        return retrofit.create(PostService.class);
    }

    @Provides
    @Singleton
    public ApiHeaders provideApiHeaders(){
        return new ApiHeaders();
    }
}
