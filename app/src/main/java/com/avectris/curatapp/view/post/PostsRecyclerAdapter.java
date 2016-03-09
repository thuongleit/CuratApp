package com.avectris.curatapp.view.post;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.avectris.curatapp.CuratApp;
import com.avectris.curatapp.R;
import com.avectris.curatapp.view.OnRecyclerItemClickListener;
import com.avectris.curatapp.view.detail.PostDetailActivity;
import com.avectris.curatapp.vo.Post;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by thuongle on 2/13/16.
 */
public class PostsRecyclerAdapter extends RecyclerView.Adapter<PostsRecyclerAdapter.ViewHolder> {

    @Inject
    DisplayImageOptions mDisplayImageOptions;

    private final Context mContext;
    private final String mClient;
    private final List<Post> mPosts;

    public PostsRecyclerAdapter(Application application, Context context, String client, List<Post> posts) {
        ((CuratApp) application).getAppComponent().inject(this);
        this.mContext = context;
        mClient = client;
        this.mPosts = posts;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.view_item_posts, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(mPosts.get(position));
    }

    @Override
    public int getItemCount() {
        return (mPosts == null || mPosts.isEmpty()) ? 0 : mPosts.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.image_picture)
        ImageView mImagePicture;
        @Bind(R.id.text_caption)
        TextView mTextCaption;
        @Bind(R.id.text_due_time)
        TextView mTextDueTime;

        private OnRecyclerItemClickListener mItemClickListener = null;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(v -> {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClick();
                }
            });
        }

        public void setItemClickListener(OnRecyclerItemClickListener itemClickListener) {
            this.mItemClickListener = itemClickListener;
        }

        public void bind(Post post) {
            ImageLoader.getInstance().displayImage(post.getMedia().getOriginMedia(), mImagePicture, mDisplayImageOptions);
            mTextCaption.setText(Html.fromHtml(post.getMedia().getCaptionText()));
            mTextDueTime.setText("Due " + post.getExecDate());

            this.setItemClickListener(() -> {
                Intent intent = new Intent(mContext, PostDetailActivity.class);
                intent.putExtra(PostDetailActivity.EXTRA_POST_ID, post.getId());
                mContext.startActivity(intent);
            });
        }
    }
}
