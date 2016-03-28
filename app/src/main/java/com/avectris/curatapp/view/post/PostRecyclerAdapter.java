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

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by thuongle on 2/13/16.
 */
public class PostRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;

    @Inject
    DisplayImageOptions mDisplayImageOptions;

    private final Context mContext;
    private final List<Post> mPosts;
    // The minimum amount of items to have below your current scroll position
    // before canLoad more.
    private int visibleThreshold = 5;
    private boolean canLoad;
    private OnLoadMoreListener onLoadMoreListener;

    public PostRecyclerAdapter(Application application, Context context, RecyclerView recyclerView, List<Post> posts) {
        ((CuratApp) application).getAppComponent().inject(this);
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
            ((PostViewHolder) holder).bind(mPosts.get(position));
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

    class PostViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.image_picture)
        ImageView mImagePicture;
        @Bind(R.id.text_caption)
        TextView mTextCaption;
        @Bind(R.id.text_due_time)
        TextView mTextDueTime;

        private OnRecyclerItemClickListener mItemClickListener = null;

        public PostViewHolder(View view) {
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
            Media media = post.getMedia();
            if (media != null) {
                ImageLoader.getInstance().displayImage(media.getOriginThumb(), mImagePicture, mDisplayImageOptions);
                mImagePicture.setBackgroundColor(mContext.getResources().getColor(android.R.color.transparent));
                mTextCaption.setText(media.getCaptionText());
            } else {
                mImagePicture.setBackgroundColor(mContext.getResources().getColor(R.color.colorPrimary));
                mTextCaption.setText("");
            }
            mTextDueTime.setText("Due " + post.getExecDate());

            this.setItemClickListener(() -> {
                Intent intent = new Intent(mContext, PostDetailActivity.class);
                intent.putExtra(PostDetailActivity.EXTRA_POST_ID, post.getId());
                mContext.startActivity(intent);
            });
        }
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public ProgressWheel progressBar;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = (ProgressWheel) v.findViewById(R.id.progress_wheel);
        }
    }
}
