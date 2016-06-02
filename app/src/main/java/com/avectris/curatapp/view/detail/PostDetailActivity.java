package com.avectris.curatapp.view.detail;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatButton;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.avectris.curatapp.R;
import com.avectris.curatapp.data.DataManager;
import com.avectris.curatapp.util.DialogFactory;
import com.avectris.curatapp.view.base.ToolbarActivity;
import com.avectris.curatapp.view.upload.UploadPostsActivity;
import com.avectris.curatapp.view.verify.VerifyActivity;
import com.avectris.curatapp.view.widget.SquareVideoView;
import com.avectris.curatapp.vo.Media;
import com.avectris.curatapp.vo.Post;
import com.danikula.videocache.CacheListener;
import com.danikula.videocache.HttpProxyCacheServer;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.utils.DiskCacheUtils;
import com.tbruyelle.rxpermissions.RxPermissions;

import java.io.File;
import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import icepick.State;
import timber.log.Timber;


/**
 * Created by thuongle on 2/15/16.
 */
public class PostDetailActivity extends ToolbarActivity implements PostDetailView, CacheListener {
    public static final String EXTRA_POST_ID = "EXTRA_POST_ID";
    public static final String EXTRA_API_CODE = "EXTRA_API_CODE";
    private static final String VIDEO_PATTERN = ".mp4";
    public static final String EXTRA_IS_POSTED = "exIssPosted";
    public static final String EXTRA_POST_POSITION = "exPostPosition";

    @Bind(R.id.text_caption)
    TextView mTextCaption;
    @Bind(R.id.image_picture)
    ImageView mImagePicture;
    @Bind(R.id.button_post_now)
    AppCompatButton mButtonPostNow;
    @Bind(R.id.button_reschedule)
    Button mButtonReschedule;
    @Bind(R.id.video_view)
    SquareVideoView mVideoView;
    @Bind(R.id.progress_bar)
    ProgressBar mProgressWheel;
    @Bind(R.id.button_track_post)
    Button mButtonTrack;

    @Inject
    PostDetailPresenter mPostDetailPresenter;
    @Inject
    DisplayImageOptions mDisplayImageOptions;
    @Inject
    HttpProxyCacheServer mHttpProxyCacheServer;
    @Inject
    DataManager mDataManager;

    private Post mPost;
    private String mPostId;
    private String mApiCode;
    private File mCacheVideo;
    private boolean mVideoMode;
    private boolean isPermissionApproved;
    @State
    boolean mIsPostedPost;
    private ProgressDialog mProgressDialog;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_post_detail;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getComponent().inject(this);
        if (mDataManager.getActiveUser() == null) {
            //no user active
            Intent intent = new Intent(this, VerifyActivity.class);
            startActivity(intent);
            finish();
        }
        mPostDetailPresenter.attachView(this);
        setTitle("Post Review");
        mSupportActionBar.setDisplayHomeAsUpEnabled(true);

        //fix for request permission in Android M
        RxPermissions.getInstance(this)
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(granted -> {
                    if (granted) { // Always true pre-M
                        // I can control this permission now
                        isPermissionApproved = true;
                    } else {
                        // Oups permission denied
                        DialogFactory.createGenericErrorDialog(PostDetailActivity.this,
                                R.string.error_message_ignore_permission).show();
                        isPermissionApproved = false;
                    }
                });

        if (savedInstanceState == null) {
            mPostId = getIntent().getStringExtra(EXTRA_POST_ID);
            mApiCode = getIntent().getStringExtra(EXTRA_API_CODE);
            mIsPostedPost = getIntent().getBooleanExtra(EXTRA_IS_POSTED, false);
            int position = getIntent().getIntExtra(EXTRA_POST_POSITION, -1);
            if (position == -1) {
                Timber.d("Something went wrong when parse intent");
                finish();
            } else {
                if (mIsPostedPost && position != 0) {//need to hide track button when it is not the first post of scheduled posts
                    mButtonTrack.setVisibility(View.GONE);
                }
            }
        } else {
            mPostId = savedInstanceState.getString(EXTRA_POST_ID);
            mApiCode = savedInstanceState.getString(EXTRA_API_CODE);
            mIsPostedPost = savedInstanceState.getBoolean(EXTRA_IS_POSTED);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        mPostDetailPresenter.detachView();
        if (mVideoView != null) {
            mVideoView.stopPlayback();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPostDetailPresenter.getPostDetail(mApiCode, mPostId);
        mVideoView.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mVideoView.isPlaying()) {
            mVideoView.pause();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(EXTRA_POST_ID, mPostId);
        outState.putString(EXTRA_API_CODE, mApiCode);
        outState.putBoolean(EXTRA_IS_POSTED, mIsPostedPost);
    }

    @Override
    public void onNetworkError() {
        onRequestFailed(getString(R.string.dialog_message_no_internet_working));
    }

    @Override
    public void onGeneralError() {
        onRequestFailed(getString(R.string.dialog_error_general_message));
    }

    @Override
    public void onPostDetailReturn(Post post) {
        mPost = post;
        if (mPost.isPosted()) {
            mButtonPostNow.setText(R.string.button_post_again);
        }
        Media media = post.getMedia();
        if (media != null) {
            //if media is a video
            String originMedia = media.getOriginMedia();
            if (originMedia.endsWith(VIDEO_PATTERN)) {
                setButtonEnable(false);
                mVideoMode = true;
                mVideoView.setVisibility(View.VISIBLE);
                mProgressWheel.setVisibility(View.VISIBLE);
                mProgressWheel.setProgress(0);
                mImagePicture.setVisibility(View.GONE);

                MediaController vidControl = new MediaController(this);
                vidControl.setAnchorView(mVideoView);
                mVideoView.setMediaController(vidControl);
                mVideoView.requestFocus();

                String proxyUrl = mHttpProxyCacheServer.getProxyUrl(originMedia);
                mHttpProxyCacheServer.registerCacheListener(this, originMedia);
                mVideoView.setVideoPath(proxyUrl);
                mVideoView.start();

            } else {
                mTextCaption.setText(media.getCaptionText());
                ImageLoader.getInstance().displayImage(originMedia, mImagePicture, mDisplayImageOptions);
                mImagePicture.setBackgroundColor(getResources().getColor(android.R.color.transparent));
            }
        } else {
            mImagePicture.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        }
    }

    @Override
    public void onRequestFailed(String errorMsg) {
        DialogFactory.createGenericErrorDialog(this, errorMsg).show();
        setButtonEnable(false);
    }

    @Override
    public void setButtonEnable(boolean enabled) {
        mButtonPostNow.setEnabled(enabled);
        if (mIsPostedPost) {
            mButtonReschedule.setVisibility(View.VISIBLE);
            mButtonReschedule.setEnabled(enabled);
        }
    }

    @Override
    public void onUpdatePostSuccess(Intent shareIntent) {
        startActivity(shareIntent);
    }

    @Override
    public void onTrackPostSuccess(String message) {
        DialogFactory.createGenericErrorDialog(this, message).show();
        mButtonTrack.setVisibility(View.GONE);
    }

    @Override
    public void showProgress(boolean show) {
        if (mProgressDialog == null) {
            mProgressDialog = DialogFactory.createProgressDialog(this, "Processing...");
        }
        if (show) {
            mProgressDialog.show();
        } else {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onTrackPostFailed(String message) {
        DialogFactory.createGenericErrorDialog(this, message).show();
    }

    @OnClick(R.id.button_post_now)
    void postToInstagram() {
        Intent intent = getPackageManager().getLaunchIntentForPackage("com.instagram.android");
        if (intent != null) {
            if (isPermissionApproved) {
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.setPackage("com.instagram.android");
                try {
                    if (mVideoMode) {
                        if (mCacheVideo == null) {
                            throw new Exception();
                        }
                        //workaround for cached video
                        //issue: in the first time of download, the file path is not correct
                        //should remove .download
                        // TODO: 4/6/16 deal with file type # .mp4 later
                        if (mCacheVideo.getAbsolutePath().endsWith(".mp4.download")) {
                            mCacheVideo = new File(mCacheVideo.getAbsolutePath().replace(".mp4.download", ".mp4"));
                        }

                        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(mCacheVideo));
                        shareIntent.setType("video/mp4");
                    } else {
                        shareIntent.putExtra(Intent.EXTRA_STREAM,
                                Uri.parse(MediaStore.Images.Media.insertImage(

                                        getContentResolver(), DiskCacheUtils.findInCache(mPost.getMedia().getOriginMedia(),
                                                ImageLoader.getInstance().getDiscCache()).getAbsolutePath(),
                                        mPost.getMedia().getCaptionText(), mPost.getMedia().getCaptionText())));
                        shareIntent.setType("image/*");
                    }
                    copyTextToClipboard();

                    if (mPost.isPosted()) {//if post already posted, no need to update api
                        onUpdatePostSuccess(shareIntent);
                    } else {
                        mPostDetailPresenter.updatePost(mApiCode, mPostId, shareIntent);
                    }
                    //change the post from scheduled post to posted post instantly (to enable track button)
                    mIsPostedPost = true;
                } catch (Exception e) {
                    Timber.d(e, "Cannot perform post now");
                    DialogFactory.createGenericErrorDialog(this, "Your media is still downloading...").show();
                }
            } else {
                DialogFactory.createGenericErrorDialog(PostDetailActivity.this,
                        R.string.error_message_ignore_permission).show();
            }
        } else {
            // bring user to the market to download the app.
            // or let them choose an app?
            intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setData(Uri.parse("market://details?id=" + "com.instagram.android"));
            startActivity(intent);
        }
    }

    @OnClick(R.id.button_reschedule)
    void reschedulePost() {
        try {
            String path = null;
            if (mVideoMode) {
                if (mCacheVideo == null) {
                    throw new Exception();
                }
                //workaround for cached video
                //issue: in the first time of download, the file path is not correct
                //should remove .download
                // TODO: 4/6/16 deal with file type # .mp4 later
                if (mCacheVideo.getAbsolutePath().endsWith(".mp4.download")) {
                    path = new File(mCacheVideo.getAbsolutePath().replace(".mp4.download", ".mp4")).getAbsolutePath();
                } else {
                    path = mCacheVideo.getAbsolutePath();
                }
            } else {
                path = DiskCacheUtils.findInCache(mPost.getMedia().getOriginMedia(),
                        ImageLoader.getInstance().getDiscCache()).getAbsolutePath();
            }
            if (path == null) {
                DialogFactory.createGenericErrorDialog(this, "Cannot perform action right now. Try again later").show();
            } else {
                ArrayList<String> paths = new ArrayList<>();
                paths.add(path);
                Intent intent = new Intent(this, UploadPostsActivity.class);
                intent.putExtra(UploadPostsActivity.EXTRA_FILE_PATHS, paths);
                String caption = mTextCaption.getText().toString();
                if (!TextUtils.isEmpty(caption)) {
                    intent.putExtra(UploadPostsActivity.EXTRA_CAPTION, caption);
                }
                startActivity(intent);
            }
        } catch (Exception e) {
            Timber.d(e, "Cannot perform post now");
            DialogFactory.createGenericErrorDialog(this, "Your media is still downloading...").show();
        }
    }

    @OnClick(R.id.button_track_post)
    public void trackPost() {
        if (!mIsPostedPost) { //it is always scheduled post
            DialogFactory.createSimpleOkErrorDialog(this, "Tracking Post", "After clicking this button," +
                    " CuraTapp server will fetch latest post from Instagram, link it with the post here, and track it." +
                    "\n\nSo, please post to Instagram first, then return to CuraTapp and click 'Track' button again.").show();
        } else {//it is the first post of posted post (recent post)
            if (mPost.isPosted()) {//if it is a posted post, already post to IG
                //this button is always enabled in position != 0
                mPostDetailPresenter.trackPost(mPost);
            } else {
                DialogFactory.createSimpleOkErrorDialog(this, "Tracking Post", "You have't posted this post to IG yet!").show();
            }
        }
    }

    private void copyTextToClipboard() {
        CharSequence text = mTextCaption.getText();
        if (!TextUtils.isEmpty(text)) {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            clipboard.setText(text);
            Toast.makeText(PostDetailActivity.this, "Caption copied to the clipboard", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCacheAvailable(File cacheFile, String url, int percentsAvailable) {
        if (mProgressWheel != null) {
            mProgressWheel.setProgress(percentsAvailable);
        }
        if (percentsAvailable == 100) {
            mCacheVideo = cacheFile;
            if (mProgressWheel != null) {
                mProgressWheel.setVisibility(View.GONE);
            }
            if (mButtonPostNow != null) {
                setButtonEnable(true);
            }
            if (mButtonReschedule != null) {
                setButtonEnable(true);
            }
        }
    }
}

