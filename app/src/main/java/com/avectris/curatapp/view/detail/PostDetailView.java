package com.avectris.curatapp.view.detail;

import android.content.Intent;
import com.avectris.curatapp.view.base.ErrorView;
import com.avectris.curatapp.vo.Post;

/**
 * Created by thuongle on 2/15/16.
 */
interface PostDetailView extends ErrorView {

    void onPostDetailReturn(Post post);

    void onRequestFailed(String errorMsg);

    void setButtonEnable(boolean enabled);

    void onUpdatePostSuccess(Intent shareIntent);

    void onTrackPostSuccess(String message);

    void showProgress(boolean show);

    void onTrackPostFailed(String message);
}
