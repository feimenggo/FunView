package com.feimeng.view;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.AttrRes;
import androidx.annotation.ColorInt;


public class RecyclerFastScrollerUtils {
    public static void setViewBackground(View view, Drawable background) {
        view.setBackground(background);
    }

    public static boolean isRTL(Context context) {
        Configuration config = context.getResources().getConfiguration();
        return config.getLayoutDirection() == View.LAYOUT_DIRECTION_LTR;
    }

    @ColorInt
    public static int resolveColor(Context context, @AttrRes int color) {
        TypedArray a = context.obtainStyledAttributes(new int[]{color});
        int resId = a.getColor(0, 0);
        a.recycle();
        return resId;
    }

    public static int convertDpToPx(Context context, float dp) {
        return (int) (dp * context.getResources().getDisplayMetrics().density + 0.5f);
    }
}
