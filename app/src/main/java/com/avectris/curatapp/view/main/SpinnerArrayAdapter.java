package com.avectris.curatapp.view.main;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by thuongle on 2/13/16.
 */
class SpinnerArrayAdapter extends ArrayAdapter<String> {

    private Context mContext;
    private OnItemClickListener mOnItemClickListener;

    public SpinnerArrayAdapter(Context context, int resource, List<String> objects) {
        super(context, resource, objects);
        this.mContext = context;
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
            if(mOnItemClickListener != null){
                mOnItemClickListener.onItemClick(position);
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

    interface OnItemClickListener{
        void onItemClick(int position);
    }
}
