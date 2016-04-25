package com.avectris.curatapp.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import timber.log.Timber;

/**
 * Created by thuongle on 4/25/16.
 */
public class AppUtils {

    private AppUtils() {
    }

    public static String getAppVersion(Context context) {
        PackageInfo pInfo = null;
        try {
            pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            Timber.d(e, e.getMessage());
        }
        return pInfo.versionName;
    }
}
