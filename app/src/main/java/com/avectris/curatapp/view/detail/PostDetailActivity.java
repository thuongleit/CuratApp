package com.avectris.curatapp.view.detail;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.avectris.curatapp.R;
import com.avectris.curatapp.view.base.ToolbarActivity;
import com.avectris.curatapp.vo.Post;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.utils.DiskCacheUtils;

import java.io.FileNotFoundException;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.OnClick;


/**
 * Created by thuongle on 2/15/16.
 */
public class PostDetailActivity extends ToolbarActivity implements PostDetailView {
    public static final String EXTRA_POST_ID = "EXTRA_POST_ID";
    public static final String EXTRA_API_CODE = "EXTRA_API_CODE";

    @Bind(R.id.text_caption)
    TextView mTextCaption;
    @Bind(R.id.image_picture)
    ImageView mImagePicture;
    @Bind(R.id.text_due_time)
    TextView mTextDueTime;

    @Inject
    PostDetailPresenter mPostDetailPresenter;
    @Inject
    DisplayImageOptions mDisplayImageOptions;
    private Post mPost;

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

        String postId = getIntent().getStringExtra(EXTRA_POST_ID);
        mPostDetailPresenter.getPostDetail(postId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_post_detail, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPostDetailPresenter.detachView();
    }

    @Override
    public void showNetworkFailed() {
        onRequestFailed(getString(R.string.dialog_message_no_internet_working));
    }

    @Override
    public void showGenericError() {
        onRequestFailed(getString(R.string.dialog_error_general_message));
    }

    @Override
    public void onPostDetailReturn(Post post) {
        mPost = post;
        mTextCaption.setText(post.getMedia().getCaptionText());
        mTextDueTime.setText("Due " + post.getExecDate());
        ImageLoader.getInstance().displayImage(post.getMedia().getOriginMedia(), mImagePicture, mDisplayImageOptions);
    }

    @Override
    public void onRequestFailed(String errorMsg) {
        Toast.makeText(PostDetailActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.button_post_now)
    public void postToInstagram() {
        Intent intent = getPackageManager().getLaunchIntentForPackage("com.instagram.android");
        if (intent != null) {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.setPackage("com.instagram.android");
            try {
                shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), DiskCacheUtils.findInCache(mPost.getMedia().getOriginMedia(), ImageLoader.getInstance().getDiscCache()).getAbsolutePath(), mPost.getMedia().getCaptionText(), mPost.getMedia().getCaptionText())));
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            shareIntent.setType("image/jpeg");

            startActivity(shareIntent);
        } else {
            // bring user to the market to download the app.
            // or let them choose an app?
            intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setData(Uri.parse("market://details?id=" + "com.instagram.android"));
            startActivity(intent);
        }
    }
}

