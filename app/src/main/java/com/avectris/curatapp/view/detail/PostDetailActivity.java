package com.avectris.curatapp.view.detail;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatButton;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.avectris.curatapp.R;
import com.avectris.curatapp.util.DialogFactory;
import com.avectris.curatapp.view.base.ToolbarActivity;
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

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;


/**
 * Created by thuongle on 2/15/16.
 */
public class PostDetailActivity extends ToolbarActivity implements PostDetailView, CacheListener {
    public static final String EXTRA_POST_ID = "EXTRA_POST_ID";
    public static final String EXTRA_API_CODE = "EXTRA_API_CODE";
    private static final String VIDEO_PATTERN = ".mp4";

    @Bind(R.id.text_caption)
    TextView mTextCaption;
    @Bind(R.id.image_picture)
    ImageView mImagePicture;
    @Bind(R.id.button_post_now)
    AppCompatButton mButtonPostNow;
    @Bind(R.id.video_view)
    SquareVideoView mVideoView;
    @Bind(R.id.progress_bar)
    ProgressBar mProgressWheel;

    @Inject
    PostDetailPresenter mPostDetailPresenter;
    @Inject
    DisplayImageOptions mDisplayImageOptions;
    @Inject
    HttpProxyCacheServer mHttpProxyCacheServer;

    private Post mPost;
    private String mPostId;
    private String mApiCode;
    private File mCacheVideo;
    private boolean mVideoMode;
    private boolean isPermissionApproved;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_post_detail;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getComponent().inject(this);
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
        } else {
            mPostId = savedInstanceState.getString(EXTRA_POST_ID);
            mApiCode = savedInstanceState.getString(EXTRA_API_CODE);
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
    }

    @Override
    public void onUpdatePostSuccess() {
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

                    startActivity(shareIntent);
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

    @OnClick(R.id.button_post_now)
    public void postToInstagram() {
        if (mPost.isPosted()) {//if post already posted, no need to update api
            onUpdatePostSuccess();
        } else {
            mPostDetailPresenter.updatePost(mApiCode, mPostId);
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
        }
    }
}

