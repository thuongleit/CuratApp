package com.avectris.curatapp.view.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.SwitchCompat;
import android.util.AttributeSet;

/**
 * Created by thuongle on 3/8/16.
 */
public class MuliSwitchCompat extends SwitchCompat {

    public MuliSwitchCompat(Context context) {
        super(context);
        setTypeface();
    }

    public MuliSwitchCompat(Context context, AttributeSet attrs) {
        super(context, attrs);
        setTypeface();
    }

    public MuliSwitchCompat(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setTypeface();
    }

    private void setTypeface() {
        setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/Muli-Regular.ttf"));
    }
}
