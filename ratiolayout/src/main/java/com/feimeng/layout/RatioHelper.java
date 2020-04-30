package com.feimeng.layout;

import android.view.View;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 宽高比例计算类
 */
public class RatioHelper implements IRatio {
    public static final int BASE_HORIZONTAL = 0;
    public static final int BASE_VERTICAL = 1;

    @IntDef({BASE_HORIZONTAL, BASE_VERTICAL})
    @Retention(RetentionPolicy.SOURCE)
    public @interface RatioTarget {
    }

    private int ratioTarget;
    private int ratioX;
    private int ratioY;

    public RatioHelper() {
        this(BASE_HORIZONTAL, 1, 1);
    }

    public RatioHelper(int ratioTarget, int ratioX, int ratioY) {
        this.ratioTarget = ratioTarget;
        this.ratioX = Math.max(ratioX, 1);
        this.ratioY = Math.max(ratioY, 1);
    }

    int calHeightMeasureSpec(int width) {
        int height = width * ratioY / ratioX;
        return View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY);
    }

    int calWidthMeasureSpec(int height) {
        int width = height * ratioX / ratioY;
        return View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
    }

    public int getRatioTarget() {
        return ratioTarget;
    }

    public void setRatioTarget(@RatioTarget int ratioTarget) {
        this.ratioTarget = ratioTarget;
    }

    public int getRatioX() {
        return ratioX;
    }

    public void setRatioX(int ratioX) {
        this.ratioX = ratioX;
    }

    public int getRatioY() {
        return ratioY;
    }

    public void setRatioY(int ratioY) {
        this.ratioY = ratioY;
    }
}
