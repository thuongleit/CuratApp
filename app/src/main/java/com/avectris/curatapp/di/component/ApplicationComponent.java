package com.avectris.curatapp.di.component;

import android.app.Application;
import android.content.Context;

import com.avectris.curatapp.data.DataManager;
import com.avectris.curatapp.data.remote.ApiHeaders;
import com.avectris.curatapp.data.remote.ApiModule;
import com.avectris.curatapp.data.remote.SessionService;
import com.avectris.curatapp.di.module.ApplicationModule;
import com.avectris.curatapp.di.scope.ApplicationScope;
import com.squareup.okhttp.OkHttpClient;

import javax.inject.Singleton;

import dagger.Component;
import retrofit.CallAdapter;
import retrofit.Converter;
import retrofit.Retrofit;

/**
 * Created by thuongle on 1/13/16.
 */
@Singleton
@Component(modules = {ApplicationModule.class, ApiModule.class})
public interface ApplicationComponent {
    Application application();

    @ApplicationScope
    Context context();

    Converter.Factory converter();

    CallAdapter.Factory callAdapter();

    Retrofit restAdapter();

    OkHttpClient okHttpClient();

    SessionService sessionService();

    ApiHeaders apiHeaders();

    DataManager dataManager();

    void inject(DataManager manager);
}
