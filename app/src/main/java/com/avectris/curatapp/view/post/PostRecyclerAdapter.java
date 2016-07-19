package com.avectris.curatapp.view.post;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.avectris.curatapp.CuratApp;
import com.avectris.curatapp.R;
import com.avectris.curatapp.util.OnLoadMoreListener;
import com.avectris.curatapp.view.OnRecyclerItemClickListener;
import com.avectris.curatapp.view.detail.PostDetailActivity;
import com.avectris.curatapp.vo.Media;
import com.avectris.curatapp.vo.Post;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.pnikosis.materialishprogress.ProgressWheel;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by thuongle on 2/13/16.
 */
class PostRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;
    private static final int PENDING_REMOVAL_TIMEOUT = 3000; // 3sec

    DisplayImageOptions mDisplayImageOptions;

    private final Context mContext;
    private final List<Post> mPosts;
    // The minimum amount of items to have below your current scroll position
    // before canLoad more.
    private int visibleThreshold = 5;
    private boolean canLoad;
    private OnLoadMoreListener onLoadMoreListener;
    private boolean mIsPosted;

    public PostRecyclerAdapter(Application application, Context context, RecyclerView recyclerView, List<Post> posts, boolean isPosted) {
        mIsPosted = isPosted;
        mDisplayImageOptions = ((CuratApp) application).getAppComponent().displayImageOptions();

        this.mContext = context;
        this.mPosts = posts;
        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView
                    .getLayoutManager();

            recyclerView
                    .addOnScrollListener(new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrolled(RecyclerView recyclerView,
                                               int dx, int dy) {
                            super.onScrolled(recyclerView, dx, dy);

                            int totalItemCount = linearLayoutManager.getItemCount();
                            int lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                            if (canLoad
                                    && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                                // End has been reached
                                // Do something
                                if (onLoadMoreListener != null) {
                                    onLoadMoreListener.onLoadMore();
                                }
                                canLoad = false;
                            }
                        }
                    });
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        if (viewType == VIEW_ITEM) {
            View v = LayoutInflater.from(mContext).inflate(R.layout.view_item_posts, parent, false);

            vh = new PostViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_progress_bar, parent, false);

            vh = new ProgressViewHolder(v);
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof PostViewHolder) {
            ((PostViewHolder) holder).bind(position, mPosts.get(position));
        } else {
            ((ProgressViewHolder) holder).progressBar.spin();
        }

    }

    public void canLoadMore(boolean canLoad) {
        this.canLoad = canLoad;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    @Override
    public int getItemViewType(int position) {
        return mPosts.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    @Override
    public int getItemCount() {
        return (mPosts == null || mPosts.isEmpty()) ? 0 : mPosts.size();
    }

    public void clear() {
        int size = mPosts.size();
        mPosts.clear();
        notifyItemRangeRemoved(0, size);
    }

    public void addItems(List<Post> posts) {
        int oldSize = mPosts.size();
        int newSize = posts.size();
        mPosts.clear();
        mPosts.addAll(posts);

        notifyItemRangeRemoved(0, oldSize);
        notifyItemRangeInserted(0, newSize);
        canLoad = false;
    }

    Post getItem(int position) {
        return mPosts.get(position);
    }

    void addItem(Post item, int previousPos) {
        mPosts.add(previousPos, item);
        notifyItemInserted(previousPos);
    }

    class PostViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.image_picture)
        ImageView mImagePicture;
        @Bind(R.id.text_caption)
        TextView mTextCaption;
        @Bind(R.id.text_due_time)
        TextView mTextDueTime;
        @Bind(R.id.text_posted)
        TextView mTextPosted;

        private OnRecyclerItemClickListener mItemClickListener = null;

        PostViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(v -> {
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClick();
                }
            });
        }

        void setItemClickListener(OnRecyclerItemClickListener itemClickListener) {
            this.mItemClickListener = itemClickListener;
        }

        public void bind(int position, Post post) {
            Media media = post.getMedia();
            if (post.isPosted()) {
                mTextPosted.setVisibility(View.VISIBLE);
            }
            if (media != null) {
                ImageLoader.getInstance().displayImage(media.getOriginThumb(), mImagePicture, mDisplayImageOptions);
                mImagePicture.setBackgroundColor(mContext.getResources().getColor(android.R.color.transparent));
                mTextCaption.setText(media.getCaptionText());
            } else {
                //if it is a ig post
                ImageLoader.getInstance().displayImage(post.getIgImageThumbnail(), mImagePicture, mDisplayImageOptions);
                mImagePicture.setBackgroundColor(mContext.getResources().getColor(android.R.color.transparent));
                mTextCaption.setText(post.getIgCaptionText());
            }
            mTextDueTime.setText("Due " + post.getExecDate());

            this.setItemClickListener(() -> {
                Intent intent = new Intent(mContext, PostDetailActivity.class);
                intent.putExtra(PostDetailActivity.EXTRA_POST_ID, post.getId());
                intent.putExtra(PostDetailActivity.EXTRA_IS_POSTED, mIsPosted);
                intent.putExtra(PostDetailActivity.EXTRA_POST_POSITION, position);
                mContext.startActivity(intent);
            });
        }
    }

    private static class ProgressViewHolder extends RecyclerView.ViewHolder {
        ProgressWheel progressBar;

        ProgressViewHolder(View v) {
            super(v);
            progressBar = (ProgressWheel) v.findViewById(R.id.progress_wheel);
        }
    }

    void remove(int position) {
        Post item = mPosts.get(position);
        if (mPosts.contains(item)) {
            mPosts.remove(position);
            notifyItemRemoved(position);
        }
    }
}
