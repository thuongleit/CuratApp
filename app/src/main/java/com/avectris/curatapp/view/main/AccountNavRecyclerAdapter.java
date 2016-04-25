package com.avectris.curatapp.view.main;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.avectris.curatapp.R;
import com.avectris.curatapp.vo.Account;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by thuongle on 2/13/16.
 */
class AccountNavRecyclerAdapter extends RecyclerView.Adapter<AccountNavRecyclerAdapter.ViewHolder> {

    private final Context mContext;
    private final List<Account> mAccounts;
    private OnAccountNavItemClickListener mItemClickListener;

    AccountNavRecyclerAdapter(Context context, List<Account> accounts) {
        mContext = context;
        mAccounts = accounts;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.view_item_account_nav_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(mAccounts.get(position));
    }

    @Override
    public int getItemCount() {
        return (mAccounts == null || mAccounts.isEmpty()) ? 0 : mAccounts.size();
    }

    public void setOnItemClickListener(OnAccountNavItemClickListener listener) {
        this.mItemClickListener = listener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final View mView;
        @Bind(R.id.view_account_status)
        View viewAccountStatus;
        @Bind(R.id.text_account_name)
        TextView mTextAccountName;
        @Bind(R.id.text_account_status)
        TextView mTextAccountStatus;
        @Bind(R.id.switch_on_off_notification)
        SwitchCompat mSwitchNotification;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            this.mView = view;
            this.mView.setOnClickListener(v -> {
                if (mItemClickListener != null) {
                    mItemClickListener.onViewClick(getAdapterPosition());
                }
            });
            this.mSwitchNotification.setOnClickListener(v -> {
                if (mItemClickListener != null) {
                    boolean isChecked = mSwitchNotification.isChecked();
                    mItemClickListener.onSwitchControlClick(mSwitchNotification, getAdapterPosition(), isChecked);
                }
            });
        }

        public void bind(Account account) {
            if (account.current) {
                ((CardView) mView).setCardBackgroundColor(mContext.getResources().getColor(R.color.colorPrimaryAlpha));
            }
            mTextAccountName.setText(account.name);
            if (account.current) {
                viewAccountStatus.setBackgroundResource(R.drawable.shape_circle_spot_account_active);
                int activeColor = mContext.getResources().getColor(R.color.black_87);
                mTextAccountStatus.setText("Active");
                mTextAccountName.setTextColor(activeColor);
                mTextAccountStatus.setTextColor(activeColor);
            } else {
                viewAccountStatus.setBackgroundResource(R.drawable.shape_circle_spot_account_deactive);
                int inactiveColor = mContext.getResources().getColor(R.color.black_54);
                mTextAccountName.setTextColor(inactiveColor);
                mTextAccountStatus.setTextColor(inactiveColor);
                mTextAccountStatus.setText("Inactive");
            }

            if (account.enableNotification) {
                mSwitchNotification.setChecked(true);
            } else {
                mSwitchNotification.setChecked(false);
            }
        }
    }

    interface OnAccountNavItemClickListener {

        void onViewClick(int position);

        void onSwitchControlClick(SwitchCompat mSwitchNotification, int position, boolean isChecked);
    }
}
