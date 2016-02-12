package com.avectris.curatapp.view.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * Created by thuongle on 2/12/16.
 */
public class MuliEditText extends EditText {
    public MuliEditText(Context context) {
        super(context);
        setTypeface();
    }

    public MuliEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        setTypeface();
    }

    public MuliEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setTypeface();
    }

    public MuliEditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setTypeface();
    }

    private void setTypeface() {
        setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/Muli-Regular.ttf"));
    }
}
