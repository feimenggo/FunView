package com.feimeng.imageviewer;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.feimeng.imageviewer.config.ContentViewOriginModel;
import com.feimeng.imageviewer.config.ViewerConfig;
import com.feimeng.imageviewer.interfaces.CircleIndexIndicator;
import com.feimeng.imageviewer.interfaces.DefaultPercentProgress;
import com.feimeng.imageviewer.interfaces.IIndicator;
import com.feimeng.imageviewer.interfaces.IProgress;
import com.feimeng.imageviewer.tools.SizeKit;

import java.util.ArrayList;
import java.util.List;

import me.panpf.sketch.Sketch;
import me.panpf.sketch.SketchImageView;

public class ImageViewer {
    private Context mContext;
    private ViewerConfig mConfig;

    public ImageViewer(Context context) {
        mContext = context;
        mConfig = new ViewerConfig();
    }

    public ImageViewer urls(String imageUrl) {
        this.mConfig.setImageUrls(new String[]{imageUrl});
        return this;
    }

    public ImageViewer fullscreen(boolean isFullScreen) {
        this.mConfig.setFullScreen(isFullScreen);
        return this;
    }

    public ImageViewer indicatorVisibility(int visibility) {
        this.mConfig.setIndicatorVisibility(visibility);
        return this;
    }

    public ImageViewer urls(String[] imageUrls) {
        this.mConfig.setImageUrls(imageUrls);
        return this;
    }

    public ImageViewer type(int type) {
        this.mConfig.setType(type);
        return this;
    }

    public ImageViewer immersive(boolean immersive) {
        this.mConfig.setImmersive(immersive);
        return this;
    }

    public ImageViewer position(int position) {
        return position(position, 0);
    }

    public ImageViewer position(int position, int headerSize) {
        this.mConfig.setHeaderSize(headerSize);
        this.mConfig.setPosition(position - headerSize);
        return this;
    }

    public ImageViewer views(View view) {
        View[] views = new View[1];
        views[0] = view;
        return views(views);
    }


    public ImageViewer views(RecyclerView recyclerView, @IdRes int viewId) {
        List<View> originImageList = new ArrayList<>();
        int childCount = recyclerView.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View originImage = (recyclerView.getChildAt(i).findViewById(viewId));
            if (originImage != null) {
                originImageList.add(originImage);
            }
        }
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        int firstPos = 0, lastPos = 0;
        int totalCount = layoutManager.getItemCount() - mConfig.getHeaderSize();
        if (layoutManager instanceof GridLayoutManager) {
            GridLayoutManager gridLayMan = (GridLayoutManager) layoutManager;
            firstPos = gridLayMan.findFirstVisibleItemPosition();
            lastPos = gridLayMan.findLastVisibleItemPosition();
        } else if (layoutManager instanceof LinearLayoutManager) {
            LinearLayoutManager linLayMan = (LinearLayoutManager) layoutManager;
            firstPos = linLayMan.findFirstVisibleItemPosition();
            lastPos = linLayMan.findLastVisibleItemPosition();
        }
        fillPlaceHolder(originImageList, totalCount, firstPos, lastPos);
        View[] views = new View[originImageList.size()];
        for (int i = 0; i < originImageList.size(); i++) {
            views[i] = originImageList.get(i);
        }
        return views(views);
    }

    private void fillPlaceHolder(List<View> originImageList, int totalCount, int firstPos, int lastPos) {
        if (firstPos > 0) {
            for (int pos = firstPos; pos > 0; pos--) {
                originImageList.add(0, null);
            }
        }
        if (lastPos < totalCount) {
            for (int i = (totalCount - 1 - lastPos); i > 0; i--) {
                originImageList.add(null);
            }
        }
    }

    public ImageViewer views(View[] views) {
        int statusBarHeight = SizeKit.getStatusBarHeight(mContext);
        List<ContentViewOriginModel> list = new ArrayList<>();
        for (View imageView : views) {
            ContentViewOriginModel imageBean = new ContentViewOriginModel();
            if (imageView == null) {
                imageBean.left = 0;
                imageBean.top = 0;
                imageBean.width = 0;
                imageBean.height = 0;
            } else {
                int[] location = new int[2];
                imageView.getLocationOnScreen(location);
                imageBean.left = location[0];
                imageBean.top = location[1] - statusBarHeight;
                imageBean.width = imageView.getWidth();
                imageBean.height = imageView.getHeight();
            }
            list.add(imageBean);
        }
        mConfig.setContentViewOriginModels(list);
        return this;
    }


    public ImageViewer start() {
        if (!mConfig.isImmersive()) {
            Window window = getWindow(mContext);
            if ((window.getAttributes().flags & WindowManager.LayoutParams.FLAG_FULLSCREEN)
                    == WindowManager.LayoutParams.FLAG_FULLSCREEN) {
                mConfig.setFullScreen(true);
            }
            if (!mConfig.isFullScreen()) {
                window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                    window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                }
            }
        }

        if (ImageActivity.iIndicator == null) {
            setIndicator(new CircleIndexIndicator());
        }
        if (ImageActivity.iProgress == null) {
            setProgress(new DefaultPercentProgress());
        }
        ImageActivity.startImageActivity(scanForActivity(mContext), mConfig);
        return this;
    }

    Window getWindow(Context context) {
        if (getAppCompActivity(context) != null) {
            return getAppCompActivity(context).getWindow();
        } else {
            return scanForActivity(context).getWindow();
        }
    }

    AppCompatActivity getAppCompActivity(Context context) {
        if (context == null) return null;
        if (context instanceof AppCompatActivity) {
            return (AppCompatActivity) context;
        } else if (context instanceof ContextThemeWrapper) {
            return getAppCompActivity(((ContextThemeWrapper) context).getBaseContext());
        }
        return null;
    }

    Activity scanForActivity(Context context) {
        if (context == null) return null;

        if (context instanceof Activity) {
            return (Activity) context;
        } else if (context instanceof ContextWrapper) {
            return scanForActivity(((ContextWrapper) context).getBaseContext());
        }
        return null;
    }

    public ImageViewer setProgress(IProgress on) {
        ImageActivity.iProgress = on;
        return this;
    }

    public ImageViewer setIndicator(IIndicator on) {
        ImageActivity.iIndicator = on;
        return this;
    }

    public ImageViewer loadPhotoBeforeShowBigImage(OnLoadPhotoBeforeShowBigImageListener on) {
        onLoadPhotoBeforeShowBigImageListener = on;
        return this;
    }

    public ImageViewer onVideoLoadEnd(OnShowToMaxFinishListener on) {
        onShowToMaxFinishListener = on;
        return this;
    }

    public ImageViewer onFinish(OnFinishListener on) {
        onFinishListener = on;
        return this;
    }

    public ImageViewer onProvideVideoView(OnProvideViewListener on) {
        onProvideViewListener = on;
        return this;
    }

    public static OnLoadPhotoBeforeShowBigImageListener onLoadPhotoBeforeShowBigImageListener;
    public static OnShowToMaxFinishListener onShowToMaxFinishListener;
    public static OnProvideViewListener onProvideViewListener;
    public static OnFinishListener onFinishListener;

    public interface OnLoadPhotoBeforeShowBigImageListener {
        void loadView(SketchImageView sketchImageView, int position);
    }

    public interface OnProvideViewListener {
        View provideView();
    }

    public interface OnShowToMaxFinishListener {
        void onShowToMax(DragView dragView, SketchImageView sketchImageView, View progressView);
    }

    public interface OnFinishListener {
        void finish(DragView dragView);
    }

    public static void cleanMemory(@NonNull Context context) {
        Sketch.with(context).getConfiguration().getDiskCache().clear();
        Sketch.with(context).getConfiguration().getBitmapPool().clear();
        Sketch.with(context).getConfiguration().getMemoryCache().clear();
    }
}
