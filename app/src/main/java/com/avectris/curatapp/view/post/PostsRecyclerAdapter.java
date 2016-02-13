package com.avectris.curatapp.view.post;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.avectris.curatapp.R;
import com.avectris.curatapp.vo.Post;
import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by thuongle on 2/13/16.
 */
public class PostsRecyclerAdapter extends RecyclerView.Adapter<PostsRecyclerAdapter.ViewHolder> {

    private final Context mContext;
    private final String mClient;
    private final List<Post> mPosts;

    public PostsRecyclerAdapter(Context context, String client, List<Post> posts) {
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

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        private void bind(Post post) {
            Glide.with(mContext).load(post.getMedia().getOrginThumb()).into(mImagePicture);
            mTextCaption.setText(Html.fromHtml("<b>@" + mClient + "</b> " + "This is just a text that comes from nowhere"));
            mTextDueTime.setText("Due " + post.getExecuteTime());
        }
    }
}
