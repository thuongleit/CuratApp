package com.avectris.curatapp.view.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by thuongle on 2/12/16.
 */
public class HelveticaNeueTextView extends TextView {
    public HelveticaNeueTextView(Context context) {
        super(context);
        setTypeface();
    }

    public HelveticaNeueTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setTypeface();
    }

    public HelveticaNeueTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setTypeface();
    }

    public HelveticaNeueTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setTypeface();
    }

    private void setTypeface(){
        setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/HelveticaNeue-Bold.otf"));
    }
}
