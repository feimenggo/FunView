package com.feimeng.diffuseimageview;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.Nullable;


/**
 * 带弥散阴影的ImageView
 */
public class DiffuseImageView extends FrameLayout {
    private final ImageView mOriginImageView;
    private final ImageView mDiffuseImageView;
    private int mDiffuseAlpha = 45;
    private int mDiffuseBlurRadius = 15;
    private int mDiffusePadding = dp2px(30);

    private Bitmap mImageBitmap;

    public DiffuseImageView(Context context) {
        this(context, null);
    }

    public DiffuseImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DiffuseImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mDiffuseImageView = new ImageView(context);
        mDiffuseImageView.setScaleType(ImageView.ScaleType.FIT_XY);
        LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.BOTTOM;
        attachViewToParent(mDiffuseImageView, 0, lp);
        mOriginImageView = new ImageView(context);
        mOriginImageView.setScaleType(ImageView.ScaleType.FIT_XY);
        lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.bottomMargin = mDiffusePadding;
        attachViewToParent(mOriginImageView, 1, lp);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
//        Log.d("nodawang", "onLayout changed:" + changed + " left:" + left + " top:" + top + " right:" + right + " bottom:" + bottom);
        setupDiffuseImage();
    }

    private void setupDiffuseImage() {
        if (mImageBitmap == null) return;
        new Thread(() -> {
            try {
                int width = getWidth();
                int height = getHeight();
                int padding = width / 10;
                Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                Paint paint = new Paint();
                paint.setAlpha(mDiffuseAlpha);
                paint.setMaskFilter(new BlurMaskFilter(mDiffuseBlurRadius, BlurMaskFilter.Blur.NORMAL));
                canvas.drawBitmap(mImageBitmap, null, new RectF(padding, 0, width - padding, height - mDiffusePadding * 0.66F), paint);
                Bitmap diffuseBitmap = blur(getContext(), bitmap, 25);
                post(() -> mDiffuseImageView.setImageBitmap(diffuseBitmap));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void setDiffuseAlpha(int diffuseAlpha) {
        mDiffuseAlpha = diffuseAlpha;
    }

    public void setDiffuseBlurRadius(int diffuseBlurRadius) {
        mDiffuseBlurRadius = diffuseBlurRadius;
    }

    public void setDiffusePadding(int diffusePadding) {
        mDiffusePadding = diffusePadding;
        ((LayoutParams) mOriginImageView.getLayoutParams()).bottomMargin = mDiffusePadding;
        mOriginImageView.requestLayout();
    }

    public void setImageBitmap(Bitmap imageBitmap) {
        mImageBitmap = imageBitmap;
        mDiffuseImageView.setImageDrawable(null);
        mOriginImageView.setImageBitmap(imageBitmap);
    }

    private int dp2px(float dpValue) {
        final float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public Bitmap blur(Context context, Bitmap bitmap, int blur) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Bitmap inputBitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
        Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap);
        RenderScript renderScript = RenderScript.create(context);
        ScriptIntrinsicBlur scriptIntrinsicBlur = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
        Allocation inputAllocation = Allocation.createFromBitmap(renderScript, inputBitmap);
        Allocation outputAllocation = Allocation.createFromBitmap(renderScript, outputBitmap);
        scriptIntrinsicBlur.setRadius((float) blur);
        scriptIntrinsicBlur.setInput(inputAllocation);
        scriptIntrinsicBlur.forEach(outputAllocation);
        outputAllocation.copyTo(outputBitmap);
        return outputBitmap;
    }
}