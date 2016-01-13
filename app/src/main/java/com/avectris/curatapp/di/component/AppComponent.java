package com.avectris.curatapp.di.component;

import android.content.Context;

import com.avectris.curatapp.config.Config;
import com.avectris.curatapp.di.module.AppModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by thuongle on 1/13/16.
 */
@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {
    Context appContext();

    Config config();
}
