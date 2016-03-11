package com.avectris.curatapp.view.post;

import com.avectris.curatapp.data.remote.vo.AccountPost;
import com.avectris.curatapp.view.base.ErrorView;

/**
 * Created by thuongle on 2/13/16.
 */
public interface PostView extends ErrorView {

    void showProgress(boolean show);

    void onEmptyPosts();

    void onPostsShow(AccountPost accountPost);

    void onRemoveBottomProgressBar();

    void onPostsShowAfterRefresh(AccountPost accountPost);

    void setNextPageLoaded(boolean loaded);

    void removeSwipePullRefresh();
}
