package com.feimeng.imageviewer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;

import com.feimeng.imageviewer.config.ContentViewOriginModel;
import com.feimeng.imageviewer.config.ViewerConfig;
import com.feimeng.imageviewer.interfaces.IIndicator;
import com.feimeng.imageviewer.interfaces.IProgress;
import com.feimeng.imageviewer.tools.NoScrollViewPager;

import java.util.ArrayList;
import java.util.List;

public class ImageActivity extends AppCompatActivity {
    private NoScrollViewPager mViewPager;
    List<ContentViewOriginModel> contentViewOriginModels;
    List<ImageFragment> fragmentList;
    ViewerConfig mViewerConfig;
    FrameLayout indicatorLayout;
    static IIndicator iIndicator;
    static IProgress iProgress;
    boolean isNeedAnimationForClickPosition = true;

    public static void startImageActivity(Activity activity, ViewerConfig viewerConfig) {
        Intent intent = new Intent(activity, ImageActivity.class);
        intent.putExtra("config", viewerConfig);
        activity.startActivity(intent);
        activity.overridePendingTransition(0, 0);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        mViewPager = findViewById(R.id.viewPager);
        indicatorLayout = findViewById(R.id.indicatorLayout);
        mViewerConfig = getIntent().getParcelableExtra("config");
//        indicatorLayout.setVisibility(mViewerConfig.getIndicatorVisibility());
        int currentPosition = mViewerConfig.getPosition();
        String[] imageUrls = mViewerConfig.getImageUrls();
        contentViewOriginModels = mViewerConfig.getContentViewOriginModels();
        fragmentList = new ArrayList<>();
        for (int i = 0; i < contentViewOriginModels.size(); i++) {
            ImageFragment imageFragment = ImageFragment.newInstance(
                    imageUrls[i], i, mViewerConfig.getType(), contentViewOriginModels.get(i)
            );
            fragmentList.add(imageFragment);
        }
        mViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return fragmentList.get(position);
            }

            @Override
            public int getCount() {
                return fragmentList.size();
            }
        });
        mViewPager.setCurrentItem(currentPosition);
        if (iIndicator != null && contentViewOriginModels.size() != 1) {
            iIndicator.attach(indicatorLayout);
            iIndicator.onShow(mViewPager);
        }
    }

    //用来判断第一次点击的时候是否需要动画  第一次需要动画  后续viewpager滑动回到该页面的时候  不做动画
    public boolean isNeedAnimationForClickPosition(int position) {
        return isNeedAnimationForClickPosition && mViewerConfig.getPosition() == position;
    }

    public void refreshNeedAnimationForClickPosition() {
        isNeedAnimationForClickPosition = false;
    }

    public void finishView() {
        if (ImageViewer.onFinishListener != null) {
            ImageViewer.onFinishListener.finish(fragmentList.get(mViewPager.getCurrentItem()).getDragView());
        }
        ImageViewer.onLoadPhotoBeforeShowBigImageListener = null;
        ImageViewer.onShowToMaxFinishListener = null;
        ImageViewer.onProvideViewListener = null;
        ImageViewer.onFinishListener = null;
        iIndicator = null;
        iProgress = null;
        finish();
        overridePendingTransition(0, 0);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            fragmentList.get(mViewPager.getCurrentItem()).backToMin();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
