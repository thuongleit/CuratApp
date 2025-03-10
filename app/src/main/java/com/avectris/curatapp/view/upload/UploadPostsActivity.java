package com.avectris.curatapp.view.upload;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.avectris.curatapp.R;
import com.avectris.curatapp.data.DataManager;
import com.avectris.curatapp.util.AppUtils;
import com.avectris.curatapp.view.base.ToolbarActivity;
import com.avectris.curatapp.view.main.SpinnerArrayAdapter;
import com.avectris.curatapp.view.widget.MuliEmojiEditText;
import com.avectris.curatapp.vo.Account;
import com.vanniktech.emoji.EmojiPopup;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import icepick.Icepick;
import icepick.State;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by thuongle on 4/29/16.
 */
public class UploadPostsActivity extends ToolbarActivity {
    public static final String EXTRA_FILE_PATHS = "exFilePaths";
    public static final String EXTRA_CAPTION = "exCaption";

    @Bind(R.id.text_description)
    TextView mTextDescription;
    @Bind(R.id.input_caption)
    MuliEmojiEditText mInputCaption;
    @Bind(R.id.button_add_to_schedule)
    Button mButtonAddToSchedule;
    @Bind(R.id.button_select_exact_time)
    Button mButtonSelectTime;
    @Bind(R.id.button_add_to_library)
    Button mButtonUploadLibrary;
    @Bind(R.id.spinner_list_account)
    Spinner mSpinner;
    @Bind(R.id.toggle_emoji)
    ImageView mToggleEmoji;
    @Bind(R.id.root)
    CoordinatorLayout mRootLayout;

    @State
    ArrayList<String> mFilePaths;
    @State
    String mCaptionText;

    private DataManager mDataManager;
    private Account mSelectedAccount;
    private Context mContext;
    private EmojiPopup mEmojiPopup;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_upload_post;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Upload Media");

        mDataManager = getApp().getAppComponent().dataManager();
        mContext = this;

        if (savedInstanceState == null) {
            mFilePaths = getIntent().getStringArrayListExtra(EXTRA_FILE_PATHS);
            mCaptionText = getIntent().getStringExtra(EXTRA_CAPTION);
        } else {
            Icepick.restoreInstanceState(this, savedInstanceState);
        }

        // Get intent, action and MIME type from sharing intent if have
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        if (type != null) {
            mFilePaths = new ArrayList<>();
            if (type.startsWith("image/") || type.startsWith("video/mp4")) {
                if ((Intent.ACTION_SEND.equals(action))) {
                    handleSendImage(intent); // Handle single image being sent
                } else if (Intent.ACTION_SEND_MULTIPLE.equals(action)) {
                    handleSendMultipleImages(intent); // Handle multiple images being sent
                }
            }
        }
        if (mCaptionText != null) {
            mInputCaption.setText(mCaptionText);
        }

        if (mFilePaths == null || mFilePaths.isEmpty()) {
            Toast.makeText(this, "You haven't chosen any files", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            mTextDescription.setText(String.format(mTextDescription.getText().toString(), mFilePaths.size()));

            mDataManager
                    .loadAccounts()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(accounts -> {
                                setupSpinner(accounts);
                                mButtonAddToSchedule.setEnabled(true);
                                mButtonSelectTime.setEnabled(true);
                                mButtonUploadLibrary.setEnabled(true);
                            },
                            e -> {
                                Toast.makeText(UploadPostsActivity.this, "Cannot get account list", Toast.LENGTH_SHORT).show();
                                finish();
                            });

            mEmojiPopup = EmojiPopup.Builder.fromRootView(mRootLayout).build(mInputCaption);
        }
    }

    void handleSendImage(Intent intent) {
        Uri uri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
        if (uri != null) {
            // Update UI to reflect image being shared
            String pathFromURI = AppUtils.getPathFromURI(mContext, uri);
            mFilePaths.add(pathFromURI);
        } else {
            Toast.makeText(mContext, "Something went wrong. Please try again later", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    void handleSendMultipleImages(Intent intent) {
        ArrayList<Uri> uris = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
        if (uris != null) {
            // Update UI to reflect multiple images being shared
            for (Uri uri : uris) {
                mFilePaths.add(AppUtils.getPathFromURI(mContext, uri));
            }
        } else {
            Toast.makeText(mContext, "Something went wrong. Please try again later", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    public void onBackPressed() {
        if (mEmojiPopup.isShowing()) {
            mEmojiPopup.dismiss();
            mToggleEmoji.setImageResource(R.drawable.ic_mood);
        } else {
            super.onBackPressed();
        }
    }

    @OnClick(R.id.button_add_to_library)
    void addToLibrary() {
        Intent intent = new Intent(this, UploadPostService.class);
        intent.putExtra(UploadPostService.EXTRA_UPLOAD_MODE, UploadPostService.REQUEST_ADD_TO_LIBRARY);
        intent.putExtra(UploadPostService.EXTRA_FILE_PATHS, mFilePaths);
        intent.putExtra(UploadPostService.EXTRA_CAPTION, mInputCaption.getText().toString().trim().replace("\n", " "));
        intent.putExtra(UploadPostService.EXTRA_ACCOUNT_ID, mSelectedAccount);
        startService(intent);
        Toast.makeText(UploadPostsActivity.this, "The task is running background", Toast.LENGTH_SHORT).show();
        finish();
    }

    @OnClick(R.id.button_select_exact_time)
    void addOnExactTime() {
        Calendar selectCal = Calendar.getInstance();
        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        final int curHour = calendar.get(Calendar.HOUR_OF_DAY);
        final int curMinute = calendar.get(Calendar.MINUTE);

        DatePickerDialog.OnDateSetListener dateListener = (view, year1, monthOfYear, dayOfMonth) -> {
            selectCal.set(Calendar.YEAR, year1);
            selectCal.set(Calendar.MONTH, monthOfYear);
            selectCal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            TimePickerDialog.OnTimeSetListener timeListener = (timeView, hourOfDay, minute) -> {
                selectCal.set(Calendar.HOUR_OF_DAY, hourOfDay);
                selectCal.set(Calendar.MINUTE, minute);
                //04/21/2016 5:10 PM
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
                String timeString = sdf.format(selectCal.getTime());
                Timber.v(timeString);

                Intent intent = new Intent(mContext, UploadPostService.class);
                intent.putExtra(UploadPostService.EXTRA_UPLOAD_MODE, UploadPostService.REQUEST_SELECT_EXACT_TIME);
                intent.putExtra(UploadPostService.EXTRA_FILE_PATHS, mFilePaths);
                intent.putExtra(UploadPostService.EXTRA_CAPTION, mInputCaption.getText().toString().trim());
                intent.putExtra(UploadPostService.EXTRA_ACCOUNT_ID, mSelectedAccount);
                intent.putExtra(UploadPostService.EXTRA_UPLOAD_TIME, timeString);
                startService(intent);
                Toast.makeText(UploadPostsActivity.this, "The task is running background", Toast.LENGTH_SHORT).show();
                finish();
            };
            TimePickerDialog timePickerDialog = new TimePickerDialog(mContext, R.style.styleTimePicker,
                    timeListener, curHour, curMinute, true);
            timePickerDialog.show();
        };
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, R.style.styleTimePicker, dateListener, year, month, day);
        datePickerDialog.show();
    }

    @OnClick(R.id.button_add_to_schedule)
    void addToSchedule() {
        Intent intent = new Intent(this, UploadPostService.class);
        intent.putExtra(UploadPostService.EXTRA_UPLOAD_MODE, UploadPostService.REQUEST_ADD_TO_SCHEDULE);
        intent.putExtra(UploadPostService.EXTRA_FILE_PATHS, mFilePaths);
        intent.putExtra(UploadPostService.EXTRA_CAPTION, mInputCaption.getText().toString().trim().replace("\n", " "));
        intent.putExtra(UploadPostService.EXTRA_ACCOUNT_ID, mSelectedAccount);
        startService(intent);
        Toast.makeText(UploadPostsActivity.this, "The task is running background", Toast.LENGTH_SHORT).show();
        finish();
    }

    @OnClick(R.id.toggle_emoji)
    void toggleEmoji() {
        if (mEmojiPopup.isShowing()) {
            mEmojiPopup.dismiss();
            mToggleEmoji.setImageResource(R.drawable.ic_mood);
        } else {
            mEmojiPopup.toggle(); // Toggles visibility of the Popup
            mToggleEmoji.setImageResource(R.drawable.ic_keyboard);
        }
    }

    private void setupSpinner(List<Account> accounts) {
        SpinnerArrayAdapter adapter = new SpinnerArrayAdapter(this, R.layout.view_item_spinner_account, accounts);
        mSpinner.setAdapter(adapter);
        int selected = 0;
        for (int i = 0, size = accounts.size(); i < size; i++) {
            if (accounts.get(i).current) {
                selected = i;
                mSelectedAccount = accounts.get(selected);
                break;
            }
        }
        mSpinner.setSelection(selected);
        adapter.setOnItemClickListener((account, position) -> {
            mSelectedAccount = account;
            mSpinner.setSelection(position);
        });
    }
}
