package com.avectris.curatapp.di.module;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;

import com.avectris.curatapp.CuratApp;
import com.avectris.curatapp.R;
import com.avectris.curatapp.config.Config;
import com.avectris.curatapp.di.scope.ApplicationScope;
import com.danikula.videocache.HttpProxyCacheServer;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by thuongle on 1/13/16.
 */
@Module
public class ApplicationModule {
    private final CuratApp mApp;

    public ApplicationModule(CuratApp mApp) {
        this.mApp = mApp;
    }

    @Singleton
    @Provides
    Application provideApplication() {
        return mApp;
    }

    @ApplicationScope
    @Provides
    public Context provideContext() {
        return mApp;
    }

    @Singleton
    @Provides
    public Config config() {
        return new Config(mApp);
    }

    @Provides
    @Singleton
    DisplayImageOptions prodiveDisplayImageOptions() {
        return new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.bg_fade)
                .showImageForEmptyUri(R.drawable.bg_error)
                .showImageOnFail(R.drawable.bg_error)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.ARGB_8888)
                .build();
    }

    @Provides
    @Singleton
    HttpProxyCacheServer provideProxy() {
        return new HttpProxyCacheServer.Builder(mApp)
                .maxCacheFilesCount(20)
                .build();
    }
}
