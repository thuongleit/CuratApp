package com.avectris.curatapp.di.component;

import android.app.Activity;
import android.content.Context;

import com.avectris.curatapp.di.module.ActivityModule;
import com.avectris.curatapp.di.scope.ActivityScope;
import com.avectris.curatapp.di.scope.PerActivity;
import com.avectris.curatapp.view.main.MainActivity;
import com.avectris.curatapp.view.post.PostedFragment;
import com.avectris.curatapp.view.splash.SplashActivity;
import com.avectris.curatapp.view.post.UpcomingFragment;
import com.avectris.curatapp.view.verify.VerifyActivity;

import dagger.Component;

/**
 * Created by thuongle on 1/13/16.
 */
@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = ActivityModule.class)
public interface ActivityComponent {

    @ActivityScope
    Context context();

    Activity activity();

    void inject(VerifyActivity activity);

    void inject(SplashActivity activity);

    void inject(UpcomingFragment fragment);

    void inject(PostedFragment fragment);

    void inject(MainActivity activity);
}
