package com.avectris.curatapp.view.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.avectris.curatapp.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by thuongle on 1/14/16.
 */
public abstract class ToolbarActivity extends BaseActivity {
    protected ActionBar mSupportActionBar;
    @Bind(R.id.toolbar)
    protected Toolbar mToolbar;
    @Bind(R.id.text_toolbar_title)
    protected TextView mToolbarTitle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        ButterKnife.bind(this);
        setupToolbar();
    }

    protected abstract int getLayoutId();

    private void setupToolbar() {
        setSupportActionBar(mToolbar);

        mSupportActionBar = getSupportActionBar();
        mSupportActionBar.setDisplayShowHomeEnabled(false);
        mToolbar.setNavigationIcon(R.drawable.ic_menu);
        mSupportActionBar.setTitle(null);
    }

    protected void setTitle(String title) {
        mToolbarTitle.setText(title);
    }
}
