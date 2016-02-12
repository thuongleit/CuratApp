package com.avectris.curatapp.view.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;

/**
 * Created by thuongle on 2/12/16.
 */
public class MuliAppCompatButton extends AppCompatButton {
    public MuliAppCompatButton(Context context) {
        super(context);
        setTypeface();
    }

    public MuliAppCompatButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        setTypeface();
    }

    public MuliAppCompatButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setTypeface();
    }

    private void setTypeface() {
        setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/Muli-Regular.ttf"));
    }
}
