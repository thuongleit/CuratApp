package com.avectris.curatapp.view.detail;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.avectris.curatapp.R;
import com.avectris.curatapp.view.base.ToolbarActivity;
import com.avectris.curatapp.vo.Post;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import javax.inject.Inject;

import butterknife.Bind;


/**
 * Created by thuongle on 2/15/16.
 */
public class PostDetailActivity extends ToolbarActivity implements PostDetailView {
    public static final String EXTRA_POST_ID =
            "com.avectris.curatapp.view.detail.PostDetailActivity.EXTRA_POST_ID";
    public static final String EXTRA_CLIENT =
            "com.avectris.curatapp.view.detail.PostDetailActivity.EXTRA_CLIENT";

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
    private String mClient;

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
        mClient = getIntent().getStringExtra(EXTRA_CLIENT);
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
        mTextCaption.setText(Html.fromHtml("<b>@" + mClient + "</b>" + post.getMedia().getCaptionText()));
        mTextDueTime.setText("Due " + post.getExecDate());
        ImageLoader.getInstance().displayImage(post.getMedia().getOriginMedia(), mImagePicture, mDisplayImageOptions);
    }

    @Override
    public void onRequestFailed(String errorMsg) {
        Toast.makeText(PostDetailActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
    }
}

