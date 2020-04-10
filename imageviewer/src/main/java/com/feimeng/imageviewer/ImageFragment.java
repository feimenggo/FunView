package com.feimeng.imageviewer;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.feimeng.imageviewer.config.ContentViewOriginModel;
import com.feimeng.imageviewer.config.ViewerConfig;
import com.feimeng.imageviewer.tools.DisplayListenerAdapter;

import java.util.Objects;

import me.panpf.sketch.Sketch;
import me.panpf.sketch.SketchImageView;
import me.panpf.sketch.SketchView;
import me.panpf.sketch.cache.DiskCache;
import me.panpf.sketch.decode.ImageAttrs;
import me.panpf.sketch.drawable.SketchGifDrawable;
import me.panpf.sketch.request.CancelCause;
import me.panpf.sketch.request.DisplayOptions;
import me.panpf.sketch.request.DownloadProgressListener;
import me.panpf.sketch.request.ErrorCause;
import me.panpf.sketch.request.ImageFrom;
import me.panpf.sketch.request.LoadListener;
import me.panpf.sketch.request.LoadRequest;
import me.panpf.sketch.request.LoadResult;
import me.panpf.sketch.state.StateImage;
import me.panpf.sketch.util.SketchUtils;

public class ImageFragment extends Fragment {
    private FrameLayout mLoadingView;
    private DragView mDragView;
    private SketchImageView mImageView;

    private int type;
    private int position;
    private String url;
    private boolean shouldShowAnimation = false;
    private ContentViewOriginModel contentViewOriginModel;

    public DragView getDragView() {
        return mDragView;
    }

    public static ImageFragment newInstance(String url, int position, int type, boolean shouldShowAnimation, ContentViewOriginModel contentViewOriginModel) {
        Bundle args = new Bundle();
        args.putInt("type", type);
        args.putInt("position", position);
        args.putString("url", url);
        args.putBoolean("shouldShowAnimation", shouldShowAnimation);
        args.putParcelable("model", contentViewOriginModel);
        ImageFragment fragment = new ImageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = requireArguments();
        type = arguments.getInt("type");
        position = arguments.getInt("position");
        url = arguments.getString("url");
        shouldShowAnimation = arguments.getBoolean("shouldShowAnimation");
        contentViewOriginModel = arguments.getParcelable("model");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Context context = requireContext();

        View view = inflater.inflate(R.layout.fragment_image, container, false);
        mLoadingView = view.findViewById(R.id.loading);
        mDragView = view.findViewById(R.id.drag);
        mDragView.setPhoto(type == ViewerConfig.PHOTO);

        if (ImageActivity.iProgress != null) {
            ImageActivity.iProgress.attach(position, mLoadingView);
        }

        if (type == ViewerConfig.VIDEO) {
            if (ImageViewer.onProvideViewListener == null) {
                throw new RuntimeException("you should set onProvideViewListener first if you use VIDEO");
            }
            if (mDragView.getContentParentView().getChildCount() <= 0) {
                mDragView.addContentChildView(ImageViewer.onProvideViewListener.provideView());
                SketchImageView photoView = new SketchImageView(context);
                mDragView.addContentChildView(photoView);
                ImageViewer.onProvideViewListener = null;
            }
        } else {
            mImageView = new SketchImageView(context);
            mImageView.getOptions().setDecodeGifImage(true);
            mImageView.setZoomEnabled(true);
            mDragView.addContentChildView(mImageView);
            Objects.requireNonNull(mImageView.getZoomer()).getBlockDisplayer().setPause(!isVisibleToUser());
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Context context = requireContext();

        if (ImageViewer.onLoadPhotoBeforeShowBigImageListener != null) {
            if (mDragView.getContentView() instanceof SketchImageView) {
                ImageViewer.onLoadPhotoBeforeShowBigImageListener.loadView((SketchImageView) mDragView.getContentView(), position);
            } else if (mDragView.getContentParentView().getChildAt(1) instanceof SketchImageView) {
                ImageViewer.onLoadPhotoBeforeShowBigImageListener.loadView((SketchImageView) mDragView.getContentParentView().getChildAt(1), position);
                mDragView.getContentParentView().getChildAt(1).setVisibility(View.VISIBLE);
            }
        }
        mDragView.setOnShowFinishListener(new DragView.OnShowFinishListener() {
            @Override
            public void showFinish(DragView view, boolean showImmediately) {
                if (type == ViewerConfig.VIDEO) {
                    mLoadingView.setVisibility(View.VISIBLE);
                    if (ImageActivity.iProgress != null) {
                        ImageActivity.iProgress.onStart(position);
                    }
                    if (ImageViewer.onShowToMaxFinishListener != null) {
                        ImageViewer.onShowToMaxFinishListener.onShowToMax(mDragView,
                                (SketchImageView) mDragView.getContentParentView().getChildAt(1),
                                ImageActivity.iProgress.getProgressView(position));
                    }
                } else if (type == ViewerConfig.PHOTO && view.getContentView() instanceof SketchImageView) {
                    showImage();
                }
            }
        });
        mDragView.setOnDragListener(new DragView.OnDragListener() {
            @Override
            public void onDrag(DragView view, float moveX, float moveY) {
                if (ImageActivity.iIndicator != null) {
                    ImageActivity.iIndicator.move(moveX, moveY);
                }
            }
        });
        mDragView.setOnFinishListener(new DragView.OnFinishListener() {
            @Override
            public void callFinish() {
                if (getContext() instanceof ImageActivity) {
                    ((ImageActivity) getContext()).finishView();
                }
                if (ImageViewer.onFinishListener != null) {
                    ImageViewer.onFinishListener.finish(mDragView);
                }
            }
        });
        mDragView.setOnReleaseListener(new DragView.OnReleaseListener() {
            @Override
            public void onRelease(boolean isToMax, boolean isToMin) {
                if (ImageActivity.iIndicator != null) {
                    ImageActivity.iIndicator.fingerRelease(isToMax, isToMin);
                }
            }
        });

        if (((ImageActivity) requireActivity()).isNeedAnimationForClickPosition(position)) { // 过渡图片，需要显示动画
            ((ImageActivity) requireActivity()).refreshNeedAnimationForClickPosition();
            mDragView.putData(contentViewOriginModel.getLeft(), contentViewOriginModel.getTop(), contentViewOriginModel.getWidth(), contentViewOriginModel.getHeight());
            mDragView.show(!shouldShowAnimation);
        } else {
            DiskCache diskCache = Sketch.with(context).getConfiguration().getDiskCache();
            if (diskCache.exist(url)) {
                showImage();
            } else {
                loadWithoutCache();
            }
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDragView.notifySizeConfig();
    }

    private void showImage() {
        loadRequest = Sketch.with(requireContext()).load(url, new LoadListener() {
            @Override
            public void onStarted() {
            }

            @Override
            public void onCompleted(@NonNull LoadResult result) {
                if (result.getGifDrawable() != null) {
                    result.getGifDrawable().followPageVisible(true, true);
                }
                int w = result.getBitmap().getWidth();
                int h = result.getBitmap().getHeight();
                mDragView.notifySize(w, h);
                Sketch.with(requireContext()).display(url, mImageView).loadingImage(new StateImage() {
                    @Nullable
                    @Override
                    public Drawable getDrawable(@NonNull Context context, @NonNull SketchView sketchView, @NonNull DisplayOptions displayOptions) {
                        return mImageView.getDrawable(); // 解决显示的时候闪烁问题
                    }
                }).commit();
            }

            @Override
            public void onError(@NonNull ErrorCause cause) {
            }

            @Override
            public void onCanceled(@NonNull CancelCause cause) {
            }
        }).commit();
    }

    /**
     * 由于图片框架原因  这里需要使用两种不同的加载方式
     * 如果有缓存  直接可显示
     * 如果没缓存 则需要等待加载完毕  才能够将图片显示在view上
     */
    private void loadImage() {
        DiskCache diskCache = Sketch.with(requireContext()).getConfiguration().getDiskCache();
        if (diskCache.exist(url)) {
            loadWithCache();
        } else {
            loadWithoutCache();
        }
    }

    private void loadWithCache() {
        mImageView.setDisplayListener(new DisplayListenerAdapter() {
            @Override
            public void onCompleted(@NonNull Drawable drawable, @NonNull ImageFrom imageFrom, @NonNull ImageAttrs imageAttrs) {
                int w = drawable.getIntrinsicWidth();
                int h = drawable.getIntrinsicHeight();
                //如果有缓存  直接将大小变为最终大小而不是去调用notifySize来更新 并且是直接显示不进行动画
                mDragView.putData(
                        contentViewOriginModel.getLeft(), contentViewOriginModel.getTop(),
                        contentViewOriginModel.getWidth(), contentViewOriginModel.getHeight(),
                        w, h);
                mDragView.show(true);
            }
        });
        mImageView.displayImage(url);
    }

    private LoadRequest loadRequest;

    private void loadWithoutCache() {
        loadRequest = Sketch.with(requireContext()).load(url, new LoadListener() {
            @Override
            public void onStarted() {
                if (ImageActivity.iProgress != null) {
                    Log.d("nodawang", "onStarted thread:" + Thread.currentThread());
                    mLoadingView.setVisibility(View.VISIBLE);
                    ImageActivity.iProgress.onStart(position);
                }
            }

            @Override
            public void onCompleted(@NonNull LoadResult result) {
                mLoadingView.setVisibility(View.GONE);
                if (ImageActivity.iProgress != null) {
                    Log.d("nodawang", "onCompleted thread:" + Thread.currentThread());
                    ImageActivity.iProgress.onFinish(position);
                }
                if (result.getGifDrawable() != null) {
                    result.getGifDrawable().followPageVisible(true, true);
                }
                int w = result.getBitmap().getWidth();
                int h = result.getBitmap().getHeight();
                mDragView.notifySize(w, h);
                mImageView.displayImage(url);
            }

            @Override
            public void onError(@NonNull ErrorCause cause) {
                if (ImageActivity.iProgress != null) {
                    ImageActivity.iProgress.onFailed(position);
                }
            }

            @Override
            public void onCanceled(@NonNull CancelCause cause) {
            }
        }).downloadProgressListener(new DownloadProgressListener() {
            @Override
            public void onUpdateDownloadProgress(int totalLength, int completedLength) {
                if (ImageActivity.iProgress != null) {
                    Log.d("nodawang", "totalLength:" + totalLength + " completedLength:" + completedLength);
                    int ratio = (int) (completedLength / (float) totalLength * 100);
                    ImageActivity.iProgress.onProgress(position, ratio);
                }
            }
        }).commit();
    }

    @Override
    public void onDestroyView() {
        if (loadRequest != null) {
            loadRequest.cancel(CancelCause.ON_DETACHED_FROM_WINDOW);
            loadRequest = null;
        }
        super.onDestroyView();
    }

    public void backToMin() {
        if (mDragView != null) mDragView.backToMin();
    }

    /**
     * SketchImageView 生命周期处理
     */

    @Override
    public void onPause() {
        super.onPause();
        if (getUserVisibleHint()) {
            onUserVisibleChanged(false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getUserVisibleHint()) {
            onUserVisibleChanged(true);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isResumed()) {
            onUserVisibleChanged(isVisibleToUser);
        }
    }

    public boolean isVisibleToUser() {
        return isResumed() && getUserVisibleHint();
    }

    protected void onUserVisibleChanged(boolean isVisibleToUser) {
        // 不可见的时候暂停分块显示器，节省内存，可见的时候恢复
        if (mImageView != null && mImageView.isZoomEnabled()) {
            mImageView.getZoomer().getBlockDisplayer().setPause(!isVisibleToUser);
            Drawable lastDrawable = SketchUtils.getLastDrawable(mImageView.getDrawable());
            if (lastDrawable != null && (lastDrawable instanceof SketchGifDrawable)) {
                ((SketchGifDrawable) lastDrawable).followPageVisible(isVisibleToUser, false);
            }
        }
    }
}
