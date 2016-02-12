package com.avectris.curatapp.view.base;

import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.avectris.curatapp.CuratApp;
import com.avectris.curatapp.di.component.ActivityComponent;
import com.avectris.curatapp.di.component.DaggerActivityComponent;
import com.avectris.curatapp.di.module.ActivityModule;


/**
 * Created by thuongle on 12/23/15.
 */
public abstract class BaseActivity extends AppCompatActivity {
    private ActivityComponent mComponent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mComponent = DaggerActivityComponent
                .builder()
                .activityModule(new ActivityModule(this))
                .applicationComponent(getApp().getAppComponent()).build();
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void requestPermissionsSafely(String[] permissions, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, requestCode);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public boolean hasPermission(String permission) {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M ||
                checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }

    public ActivityComponent getComponent() {
        return mComponent;
    }

    public CuratApp getApp() {
        return (CuratApp) getApplication();
    }
}
