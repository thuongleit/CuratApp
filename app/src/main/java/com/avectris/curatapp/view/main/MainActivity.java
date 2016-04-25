package com.avectris.curatapp.view.main;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.*;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.avectris.curatapp.R;
import com.avectris.curatapp.config.Constant;
import com.avectris.curatapp.data.DataManager;
import com.avectris.curatapp.service.RegistrationIntentService;
import com.avectris.curatapp.view.base.ToolbarActivity;
import com.avectris.curatapp.view.post.PostFragment;
import com.avectris.curatapp.view.verify.VerifyActivity;
import com.avectris.curatapp.vo.Account;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import timber.log.Timber;


public class MainActivity extends ToolbarActivity implements NavigationView.OnNavigationItemSelectedListener, AccountView {
    private static final String EXTRA_FRAGMENT_UPCOMING = "exUpcoming";
    private static final String EXTRA_ACCOUNT_POSITION = "exAccountPos";
    private static final String EXTRA_FRAGMENT_POSTED = "exPosted";

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    @Bind(R.id.spinner_list_account)
    Spinner mSpinner;
    @Bind(R.id.nav_view)
    NavigationView mNavView;
    @Bind(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @Bind(R.id.view_pager)
    ViewPager mViewPager;
    @Bind(R.id.tab_layout)
    TabLayout mTabLayout;

    @Inject
    AccountPresenter mAccountPresenter;
    DataManager mDataManager;
    Context mContext;

    private RecyclerView mRecyclerViewOnNav;
    private Fragment mUpcomingFragment;
    private Fragment mPostedFragment;
    private ProgressDialog mProcessDialog;
    private int mCurrentAccount;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            mCurrentAccount = 0;
            mUpcomingFragment = PostFragment.createInstance(Constant.UPCOMING_CONTENT_MODE);
            mPostedFragment = PostFragment.createInstance(Constant.POSTED_CONTENT_MODE);
        } else {
            mCurrentAccount = getIntent().getIntExtra(EXTRA_ACCOUNT_POSITION, 0);
            mUpcomingFragment = getSupportFragmentManager().getFragment(savedInstanceState, EXTRA_FRAGMENT_UPCOMING);
            mPostedFragment = getSupportFragmentManager().getFragment(savedInstanceState, EXTRA_FRAGMENT_POSTED);
        }
        getComponent().inject(this);
        mAccountPresenter.attachView(this);
        setTitle(getString(R.string.title_scheduled_posts));
        mContext = this;

        setupNavigationView();
        setupTabLayout();
        mAccountPresenter.loadAccounts();

        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(EXTRA_ACCOUNT_POSITION, mCurrentAccount);
        //Save the fragment's instance
        getSupportFragmentManager().putFragment(outState, EXTRA_FRAGMENT_UPCOMING, mUpcomingFragment);
        getSupportFragmentManager().putFragment(outState, EXTRA_FRAGMENT_POSTED, mPostedFragment);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        mAccountPresenter.detachView();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return false;
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onAccountsReturn(List<Account> accounts) {
        setupSpinnerAccount(accounts);
        setupAccountNavView(accounts);
    }

    @Override
    public void onEmptyAccounts() {
        Timber.d("No Account active in database");
        Intent intent = new Intent(this, VerifyActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void reloadActivity(Account account) {
        Intent reload = getReloadIntent();
        startActivity(reload);
        finish();
    }

    @Override
    public void onEnableDisableNotificationFailed(SwitchCompat view, boolean state) {
        if (view != null) {
            new Handler().postDelayed(() -> view.setChecked(state), 200);
        }
        Toast.makeText(MainActivity.this, "Request failed", Toast.LENGTH_LONG).show();
    }

    private void setupSpinnerAccount(List<Account> accounts) {
        List<String> accountNames = buildAccountName(accounts);
        SpinnerArrayAdapter adapter = new SpinnerArrayAdapter(this, R.layout.view_item_spinner_account, accountNames);
        mSpinner.setAdapter(adapter);
        int selected = 0;
        for (int i = 0, size = accounts.size(); i < size; i++) {
            if (accounts.get(i).current) {
                selected = i;
                mCurrentAccount = i;
                break;
            }
        }
        mSpinner.setSelection(selected);
        adapter.setOnItemClickListener(position -> {
            if (mCurrentAccount != position) {
                mAccountPresenter.chooseAccount(accounts.get(position));
                mCurrentAccount = position;
            }
        });
    }

    private List<String> buildAccountName(List<Account> accounts) {
        List<String> accountNames = new ArrayList<>();
        for (Account account : accounts) {
            accountNames.add(account.name);
        }

        return accountNames;
    }

    private void setupAccountNavView(List<Account> accounts) {
        AccountNavRecyclerAdapter adapter = new AccountNavRecyclerAdapter(this, accounts);
        adapter.setOnItemClickListener(new AccountNavRecyclerAdapter.OnAccountNavItemClickListener() {
            @Override
            public void onViewClick(int position) {
                if (mCurrentAccount != position) {
                    mAccountPresenter.chooseAccount(accounts.get(position));
                    mCurrentAccount = position;
                }
                if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                    mDrawerLayout.closeDrawer(GravityCompat.START);
                }
            }

            @Override
            public void onSwitchControlClick(SwitchCompat view, int position, boolean isChecked) {
                if (isChecked) {
                    mAccountPresenter.enablePushNotification(view, accounts.get(position));
                } else {
                    mAccountPresenter.disablePushNotification(view, accounts.get(position));
                }
            }

//                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
//                builder.setMessage("Are you sure to delete this account?")
//                        .setPositiveButton("OK", (dialog, which) -> {
//                            mAccountPresenter.deleteAccount(accounts, position);
//                            dialog.dismiss();
//                        })
//                        .setNegativeButton("Cancel", (dialog, which) -> {
//                            dialog.dismiss();
//                        });
//                builder.create().show();
//                if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
//                    mDrawerLayout.closeDrawer(GravityCompat.START);
//                }
        });
        mRecyclerViewOnNav.setAdapter(adapter);
    }

    private void setupTabLayout() {
        final CustomPageAdapter adapter = new CustomPageAdapter(getSupportFragmentManager());
        String[] titles = getResources().getStringArray(R.array.tab_titles);
        Fragment[] fragments = new Fragment[]{mUpcomingFragment, mPostedFragment};
        adapter.addTabs(titles, fragments);
        mViewPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(mViewPager);

        for (int i = 0; i < mTabLayout.getTabCount(); i++) {
            mTabLayout.getTabAt(i).setCustomView(adapter.getCustomView(i));
        }
    }

    private void setupNavigationView() {
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        mDrawerLayout.setDrawerListener(drawerToggle);
        drawerToggle.syncState();
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        View headerView = getLayoutInflater().inflate(R.layout.nav_header_main, null);
        mRecyclerViewOnNav = (RecyclerView) headerView.findViewById(R.id.recycler_view_nav);

        mNavView.addView(headerView, layoutParams);
        mNavView.setNavigationItemSelectedListener(this);

        mRecyclerViewOnNav.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerViewOnNav.setHasFixedSize(true);
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Timber.e("This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    @Override
    public void onNetworkError() {

    }

    @Override
    public void onGeneralError() {

    }

    private class CustomPageAdapter extends FragmentStatePagerAdapter {
        private String[] titles;
        private Fragment[] fragments;

        CustomPageAdapter(FragmentManager fm) {
            super(fm);
        }

        void addTabs(String[] titles, Fragment[] fragments) {
            this.titles = titles;
            this.fragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return this.fragments[position];
        }

        @Override
        public int getCount() {
            return this.titles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return this.titles[position];
        }

        View getCustomView(int position) {
            TextView textTitle = (TextView) getLayoutInflater().inflate(R.layout.view_tabbar, null);
            textTitle.setText(getPageTitle(position));
            textTitle.setTextColor(getResources().getColorStateList(R.color.tabbar_text));
            if (position == 0) {
                textTitle.setSelected(true);
            }
            return textTitle;
        }
    }
}