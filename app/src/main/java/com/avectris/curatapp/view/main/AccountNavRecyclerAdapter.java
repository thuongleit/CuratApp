package com.avectris.curatapp.view.main;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.avectris.curatapp.R;
import com.avectris.curatapp.vo.Account;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by thuongle on 2/13/16.
 */
public class AccountNavRecyclerAdapter extends RecyclerView.Adapter<AccountNavRecyclerAdapter.ViewHolder> {

    private final Context mContext;
    private final List<Account> mAccounts;

    public AccountNavRecyclerAdapter(Context context, List<Account> accounts) {
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

    class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.view_account_status)
        View viewAccountStatus;
        @Bind(R.id.text_account_name)
        TextView mTextAccountName;
        @Bind(R.id.text_account_status)
        TextView mTextAccountStatus;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void bind(Account account) {
            mTextAccountName.setText(account.getName());
            if (account.isActive()) {
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
        }
    }
}
