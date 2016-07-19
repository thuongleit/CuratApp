package com.avectris.curatapp.di.component;

import android.app.Application;
import android.content.Context;

import com.avectris.curatapp.config.Config;
import com.avectris.curatapp.data.ApiModule;
import com.avectris.curatapp.data.DataManager;
import com.avectris.curatapp.data.remote.ApiHeaders;
import com.avectris.curatapp.data.remote.PostService;
import com.avectris.curatapp.data.remote.SessionService;
import com.avectris.curatapp.di.module.ApplicationModule;
import com.avectris.curatapp.di.scope.ApplicationScope;
import com.danikula.videocache.HttpProxyCacheServer;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import javax.inject.Singleton;

import dagger.Component;
import okhttp3.OkHttpClient;
import retrofit2.CallAdapter;
import retrofit2.Converter;
import retrofit2.Retrofit;

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

    ApiHeaders apiHeaders();

    DataManager dataManager();

    SessionService sessionService();

    PostService postService();

    DisplayImageOptions displayImageOptions();

    HttpProxyCacheServer proxy();

    Config config();

    Retrofit retroit();

    void inject(DataManager manager);
}
