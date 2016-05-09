package com.avectris.curatapp.view.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import com.vanniktech.emoji.EmojiEditText;

/**
 * Created by thuongle on 2/12/16.
 */
public class MuliEmojiEditText extends EmojiEditText {
    public MuliEmojiEditText(Context context) {
        super(context);
        setTypeface();
    }

    public MuliEmojiEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        setTypeface();
    }

    public MuliEmojiEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setTypeface();
    }

    private void setTypeface() {
        setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/Muli-Regular.ttf"));
    }
}
