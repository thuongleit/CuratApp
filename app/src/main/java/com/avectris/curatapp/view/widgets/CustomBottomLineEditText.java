package com.avectris.curatapp.view.widgets;

import android.content.Context;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.widget.EditText;

import com.avectris.curatapp.R;

/**
 * Created by thuongle on 1/13/16.
 */
public class CustomBottomLineEditText extends EditText {
    public CustomBottomLineEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        getBackground().setColorFilter(context.getResources().getColor(R.color.colorPrimaryDark), PorterDuff.Mode.SRC_IN);
    }
}
