package com.avectris.curatapp.view.main;

import android.content.Context;
import android.graphics.Typeface;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.avectris.curatapp.vo.Account;

import java.util.List;

/**
 * Created by thuongle on 2/13/16.
 */
public class SpinnerArrayAdapter extends ArrayAdapter<Account> {

    private final List<Account> mAccounts;
    private OnItemClickListener mOnItemClickListener;

    public SpinnerArrayAdapter(Context context, int resource, List<Account> accounts) {
        super(context, resource, accounts);
        mAccounts = accounts;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView textView = (TextView) super.getView(position, convertView, parent);
        setTypeface(textView);
        return textView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        TextView textView = (TextView) super.getView(position, convertView, parent);
        setTypeface(textView);
        textView.setOnClickListener(v -> {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(mAccounts.get(position), position);
                View root = textView.getRootView();
                root.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK));
                root.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK));
            }
        });
        return textView;
    }

    private void setTypeface(TextView textView) {
        Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/Muli-Regular.ttf");
        textView.setTypeface(typeface);
    }

    public void setOnItemClickListener(OnItemClickListener clickListener) {
        this.mOnItemClickListener = clickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(Account account, int position);
    }
}
