package com.avectris.curatapp.view.widget;

import android.content.Context;
import android.graphics.PorterDuff;
import android.util.AttributeSet;

import com.avectris.curatapp.R;

/**
 * Created by thuongle on 1/13/16.
 */
public class CustomBottomLineEditText extends MuliEditText {
    public CustomBottomLineEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        getBackground().setColorFilter(context.getResources().getColor(R.color.colorPrimaryDark), PorterDuff.Mode.SRC_IN);
    }
}
