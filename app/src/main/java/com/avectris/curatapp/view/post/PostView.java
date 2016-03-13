package com.avectris.curatapp.view.post;

import com.avectris.curatapp.view.base.ErrorView;
import com.avectris.curatapp.vo.Post;

import java.util.List;

/**
 * Created by thuongle on 2/13/16.
 */
public interface PostView extends ErrorView {

    void onPostsReturn(List<Post> posts);

    void onEmptyPostsReturn();

    void onPostsReturnAfterRefresh(List<Post> posts);

    void shouldRemoveEmptyView();

    void shouldRemoveErrorView();

    void showNetworkFailedInRefresh();

    void shouldStopPullRefresh();

    void showProgress(boolean show);

    void onRemoveBottomProgressBar();

}
