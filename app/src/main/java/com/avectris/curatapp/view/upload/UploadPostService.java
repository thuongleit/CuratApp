package com.avectris.curatapp.view.upload;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;
import com.avectris.curatapp.CuratApp;
import com.avectris.curatapp.R;
import com.avectris.curatapp.config.Constant;
import com.avectris.curatapp.data.DataManager;
import com.avectris.curatapp.di.component.ApplicationComponent;
import com.avectris.curatapp.view.main.MainActivity;
import com.avectris.curatapp.vo.Account;
import rx.Observable;
import rx.Subscription;
import timber.log.Timber;

import java.util.ArrayList;

/**
 * Created by thuongle on 4/29/16.
 */
public class UploadPostService extends IntentService {
    public static final int REQUEST_ADD_TO_LIBRARY = 1;
    public static final int REQUEST_ADD_TO_SCHEDULE = 2;
    public static final int REQUEST_SELECT_EXACT_TIME = 3;

    public static final String EXTRA_UPLOAD_MODE = "exUploadMode";
    public static final String EXTRA_FILE_PATHS = "exFilePaths";
    public static final String EXTRA_UPLOAD_TIME = "exUploadTime";
    public static final String EXTRA_CAPTION = "exCaption";
    public static final String EXTRA_ACCOUNT_ID = "exAccountId";

    private DataManager mDataManager;
    private Subscription mSubscription;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public UploadPostService() {
        super(UploadPostService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        ApplicationComponent appComponent = ((CuratApp) getApplication()).getAppComponent();
        mDataManager = appComponent.dataManager();
        int uploadMode = intent.getIntExtra(EXTRA_UPLOAD_MODE, -1);
        if (uploadMode == -1) {
            Toast.makeText(getApplicationContext(), "Cannot upload post", Toast.LENGTH_SHORT).show();
            stopSelf();
        }
        ArrayList<String> filePaths = intent.getStringArrayListExtra(EXTRA_FILE_PATHS);
        String caption = intent.getStringExtra(EXTRA_CAPTION);
        String uploadTime = intent.getStringExtra(EXTRA_UPLOAD_TIME);
        Account account = intent.getParcelableExtra(EXTRA_ACCOUNT_ID);
        final int[] numberOfSuccess = {0};

        mSubscription = Observable
                .from(filePaths)
                .flatMap(path -> mDataManager.uploadPost(uploadMode, account, path, caption, uploadTime))
                .filter(response -> response.isSuccess())
                .count()
                .doOnNext(count -> numberOfSuccess[0]++)
                .subscribe(count -> {
                            //trigger to cache new business change
                            Timber.d("Upload posts success");
                            sendNotification(filePaths.size(), count);
                            this.stopSelf();
                        }
                        , e -> {
                            Timber.d(e, "Upload posts failed");
                            sendNotification(filePaths.size(), numberOfSuccess[0]);
                            this.stopSelf();
                        });
    }

    @Override
    public boolean stopService(Intent name) {
        if (mSubscription != null) {
            mSubscription.unsubscribe();
            mSubscription = null;
        }
        return super.stopService(name);
    }

    private void sendNotification(int total, int numberOfSuccess) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Upload Media")
                .setContentText(String.format("%s/%s uploaded success", numberOfSuccess, total))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());

        //notify UI when receive a notification
        Intent receiveNotification = new Intent(Constant.RECEIVE_NOTIFICATION);
        LocalBroadcastManager.getInstance(this).sendBroadcast(receiveNotification);
    }
}
