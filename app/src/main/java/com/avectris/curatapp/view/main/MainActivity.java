package com.avectris.curatapp.view.main;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.avectris.curatapp.R;
import com.avectris.curatapp.config.Constant;
import com.avectris.curatapp.data.DataManager;
import com.avectris.curatapp.service.RegistrationIntentService;
import com.avectris.curatapp.util.DialogFactory;
import com.avectris.curatapp.view.base.ToolbarActivity;
import com.avectris.curatapp.view.post.PostFragment;
import com.avectris.curatapp.view.upload.UploadPostsActivity;
import com.avectris.curatapp.view.verify.VerifyActivity;
import com.avectris.curatapp.vo.Account;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import timber.log.Timber;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ToolbarActivity implements NavigationView.OnNavigationItemSelectedListener, AccountView {
    private static final String EXTRA_FRAGMENT_UPCOMING = "exUpcoming";
    private static final String EXTRA_ACCOUNT_POSITION = "exAccountPos";
    private static final String EXTRA_FRAGMENT_POSTED = "exPosted";

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final int REQUEST_GALLERY_INTENT = 1;
    private static final int REQUEST_GALLERY_KITKAT_INTENT = 2;

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
    private int mCurrentPosition;
    private SwipeRefreshLayout mSwipeLayoutOnNav;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            mCurrentPosition = 0;
            mUpcomingFragment = PostFragment.createInstance(Constant.UPCOMING_CONTENT_MODE);
            mPostedFragment = PostFragment.createInstance(Constant.POSTED_CONTENT_MODE);
        } else {
            mCurrentPosition = getIntent().getIntExtra(EXTRA_ACCOUNT_POSITION, 0);
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
        outState.putInt(EXTRA_ACCOUNT_POSITION, mCurrentPosition);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_action_log_out:
                mAccountPresenter.logout();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ArrayList<String> filePaths = new ArrayList<>();
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_GALLERY_INTENT:
                    if (data.getData() != null) {
                        filePaths.add(getRealPathFromUri(data.getData()));
                    } else {
                        ClipData clipData;
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                            clipData = data.getClipData();
                        } else {
                            Toast.makeText(mContext, R.string.error_not_support_multiple_files, Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (clipData != null) {
                            for (int i = 0, size = clipData.getItemCount(); i < size; i++) {
                                filePaths.add(getRealPathFromUri(clipData.getItemAt(i).getUri()));
                            }
                        }
                    }
                    break;
                case REQUEST_GALLERY_KITKAT_INTENT:
                    //user just choose 1 file
                    if (data.getData() != null) {
                        filePaths.add(getRealPathFromUri(data.getData()));
                    } else { //users choose multiple files
                        ClipData clipData = null;
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                            clipData = data.getClipData();
                        }

                        if (clipData != null) {
                            for (int i = 0, size = clipData.getItemCount(); i < size; i++) {
                                filePaths.add(getRealPathFromUri(clipData.getItemAt(i).getUri()));
                            }
                        }
                    }
                    break;

            }
            Intent newIntent = new Intent(mContext, UploadPostsActivity.class);
            newIntent.putExtra(UploadPostsActivity.EXTRA_FILE_PATHS, filePaths);
            startActivity(newIntent);
        }
    }

    private String getRealPathFromUri(Uri uri) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            String id = uri.getLastPathSegment().split(":")[1];
            final String[] imageColumns = {MediaStore.Images.Media.DATA};
            final String imageOrderBy = null;

            Uri newUri = getUri();
            String path = null;

            Cursor imageCursor = getContentResolver().query(newUri, imageColumns,
                    MediaStore.Images.Media._ID + "=" + id, null, imageOrderBy);

            if (imageCursor.moveToFirst()) {
                path = imageCursor.getString(imageCursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            Timber.d("path %s", path); // use path

            imageCursor.close();
            return path;
        } else {
            String[] projection = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
            String path = null;
            if (cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndex(projection[0]);
                path = cursor.getString(columnIndex); // returns null
            }
            Timber.d("path %s", path);
            cursor.close();

            return path;
        }
    }

    // By using this method get the Uri of Internal/External Storage for Media
    private Uri getUri() {
        String state = Environment.getExternalStorageState();
        if (!state.equalsIgnoreCase(Environment.MEDIA_MOUNTED))
            return MediaStore.Images.Media.INTERNAL_CONTENT_URI;

        return MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
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
    public void refreshPage() {
        Intent reload = getReloadIntent();
        finish();
        startActivity(reload);
    }

    @Override
    public void onEnableDisableNotificationFailed(SwitchCompat view, boolean state) {
        if (view != null) {
            new Handler().postDelayed(() -> view.setChecked(state), 200);
        }
        Toast.makeText(MainActivity.this, "Request failed", Toast.LENGTH_LONG).show();
    }

    @Override
    public void removeSwipeLayout() {
        if (mSwipeLayoutOnNav.isRefreshing()) {
            mSwipeLayoutOnNav.setRefreshing(false);
        }
    }

    @Override
    public void logout() {
        Toast.makeText(MainActivity.this, "Logged out", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(mContext, VerifyActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onRequestFailed(String message) {
        DialogFactory.createGenericErrorDialog(this, message).show();
    }

    @OnClick(R.id.fab)
    void onFabClick() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            Intent intent = new Intent();
            intent.setType("image/* video/mp4");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_GALLERY_INTENT);
        } else {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/* video/mp4");
            intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"image/*", "video/mp4"});
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            startActivityForResult(intent, REQUEST_GALLERY_KITKAT_INTENT);
        }
    }

    private void setupSpinnerAccount(List<Account> accounts) {
        SpinnerArrayAdapter adapter = new SpinnerArrayAdapter(this, R.layout.view_item_spinner_account, accounts);
        mSpinner.setAdapter(adapter);
        int selected = 0;
        for (int i = 0, size = accounts.size(); i < size; i++) {
            if (accounts.get(i).current) {
                selected = i;
                mCurrentPosition = i;
                break;
            }
        }
        mSpinner.setSelection(selected);
        adapter.setOnItemClickListener((account, position) -> {
            if (mCurrentPosition != position) {
                mAccountPresenter.chooseAccount(account);
                mCurrentPosition = position;
            }
        });
    }

    private void setupAccountNavView(List<Account> accounts) {
        AccountNavRecyclerAdapter adapter = new AccountNavRecyclerAdapter(this, accounts);
        adapter.setOnItemClickListener(new AccountNavRecyclerAdapter.OnAccountNavItemClickListener() {
            @Override
            public void onViewClick(Account account, int position) {
                if (mCurrentPosition != position) {
                    mAccountPresenter.chooseAccount(account);
                    mCurrentPosition = position;
                }
                if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
                    mDrawerLayout.closeDrawer(GravityCompat.START);
                }
            }

            @Override
            public void onSwitchControlClick(SwitchCompat compat, Account account, boolean isChecked) {
                if (isChecked) {
                    mAccountPresenter.enablePushNotification(compat, account);
                } else {
                    mAccountPresenter.disablePushNotification(compat, account);
                }
            }
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

        mSwipeLayoutOnNav = (SwipeRefreshLayout) headerView.findViewById(R.id.swipe_refresh_layout);
        mSwipeLayoutOnNav.setColorSchemeResources(R.color.pink, R.color.green, R.color.blue);
        mSwipeLayoutOnNav.setOnRefreshListener(() -> {
            mAccountPresenter.fetchAccounts();
        });
        mRecyclerViewOnNav.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                mSwipeLayoutOnNav.setEnabled(((LinearLayoutManager) mRecyclerViewOnNav.getLayoutManager())
                        .findFirstCompletelyVisibleItemPosition() == 0);
            }
        });
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
        DialogFactory.createGenericErrorDialog(this, R.string.dialog_message_no_internet_working).show();
    }

    @Override
    public void onGeneralError() {
        DialogFactory.createSimpleOkErrorDialog(this, R.string.title_general_error, R.string.message_general_error).show();
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