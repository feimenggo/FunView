package com.feimeng.loading;

import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;

/**
 * Author: Feimeng
 * Time:   2019/11/1
 * Description: 加载状态管理器
 */
public class Loading {
    public static final int STATUS_LOADING = 1;
    public static final int STATUS_LOAD_FAILED = 3;
    public static final int STATUS_EMPTY_DATA = 4;
    public static final int STATUS_LOAD_SUCCESS = 2;

    private static volatile Loading mDefault;
    private Adapter mAdapter;

    /**
     * Provides view to show current loading status
     */
    public interface Adapter {
        /**
         * get view for current status
         *
         * @param loading     Status
         * @param convertView The old view to reuse, if possible.
         * @param status      current status
         * @return status view to show. Maybe convertView for reuse.
         * @see Status
         */
        View getView(Status loading, View convertView, int status);
    }

    private Loading() {
    }

    /**
     * Create a new Loading different from the default one
     *
     * @param adapter another adapter different from the default one
     * @return Loading
     */
    public static Loading from(Adapter adapter) {
        Loading loading = new Loading();
        loading.mAdapter = adapter;
        return loading;
    }

    /**
     * get default Loading object for global usage in whole app
     *
     * @return default Loading object
     */
    public static Loading getDefault() {
        if (mDefault == null) {
            synchronized (Loading.class) {
                if (mDefault == null) {
                    mDefault = new Loading();
                }
            }
        }
        return mDefault;
    }

    /**
     * init the default loading status view creator ({@link Adapter})
     *
     * @param adapter adapter to create all status views
     */
    public static void initDefault(Adapter adapter) {
        getDefault().mAdapter = adapter;
    }

    /**
     * Loading(loading status view) wrap the whole activity
     * wrapper is android.R.id.content
     *
     * @param activity current activity object
     * @return holder of Loading
     */
    public Status wrap(Activity activity) {
        ViewGroup wrapper = activity.findViewById(android.R.id.content);
        return new Status(mAdapter, activity, wrapper);
    }

    /**
     * Loading(loading status view) wrap the specific view.
     *
     * @param view view to be wrapped
     * @return Status
     */
    public Status wrap(View view) {
        FrameLayout wrapper = new FrameLayout(view.getContext());
        ViewGroup.LayoutParams lp = view.getLayoutParams();
        if (lp != null) {
            wrapper.setLayoutParams(lp);
        }
        if (view.getParent() != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            int index = parent.indexOfChild(view);
            parent.removeView(view);
            parent.addView(wrapper, index);
        }
        LayoutParams newLp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        wrapper.addView(view, newLp);
        return new Status(mAdapter, view.getContext(), wrapper);
    }

    public Status into(ViewGroup view) {
        return new Status(mAdapter, view.getContext(), view);
    }

    public Status into(ViewGroup view, ViewGroup.LayoutParams layoutParams) {
        return new Status(mAdapter, view.getContext(), view, layoutParams);
    }

    /**
     * Loading holder<br>
     * create by {@link Loading#wrap(Activity)} or {@link Loading#wrap(View)}<br>
     * the core API for showing all status view
     */
    public static class Status {
        private Adapter mAdapter;
        private Context mContext;
        private Runnable mRetryTask;
        private int mCurState = STATUS_LOAD_SUCCESS;
        private View mCurStatusView;
        private ViewGroup mWrapper;
        private Object mData;
        private SparseArray<View> mStatusViews = new SparseArray<>(4);
        private ViewGroup.LayoutParams mViewLayout;
        private int mSuccessRemoveDuration = 600;

        private Status(Adapter adapter, Context context, ViewGroup wrapper) {
            this(adapter, context, wrapper, null);
        }

        private Status(Adapter adapter, Context context, ViewGroup wrapper, ViewGroup.LayoutParams viewLayout) {
            this.mAdapter = adapter;
            this.mContext = context;
            this.mWrapper = wrapper;
            this.mViewLayout = viewLayout;
        }

        public Status setSuccessHideDuration(int successRemoveDuration) {
            mSuccessRemoveDuration = successRemoveDuration;
            return this;
        }

        public int getCurState() {
            return mCurState;
        }

        /**
         * set retry task when user click the retry button in load failed page
         *
         * @param task when user click in load failed UI, run this task
         * @return this
         */
        public Status withRetry(Runnable task) {
            mRetryTask = task;
            return this;
        }

        /**
         * set extension data
         *
         * @param data extension data
         * @return this
         */
        public Status withData(Object data) {
            this.mData = data;
            return this;
        }

        /**
         * show UI for status: {@link #STATUS_LOADING}
         */
        public Status showLoading() {
            showLoadingStatus(STATUS_LOADING);
            return this;
        }

        public Status showLoading(Object withData) {
            withData(withData);
            showLoading();
            return this;
        }

        /**
         * show UI for status: {@link #STATUS_LOAD_SUCCESS}
         */
        public void showLoadSuccess() {
            showLoadingStatus(STATUS_LOAD_SUCCESS);
        }

        public void showLoadSuccess(int delay) {
            getWrapper().postDelayed(new Runnable() {
                @Override
                public void run() {
                    showLoadSuccess();
                }
            }, delay);
        }

        public void showLoadSuccess(final Runnable animFinish) {
            showLoadingStatus(STATUS_LOAD_SUCCESS);
            if (mSuccessRemoveDuration > 0 && animFinish != null) {
                getWrapper().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        animFinish.run();
                    }
                }, mSuccessRemoveDuration);
            }
        }

        public void showLoadSuccess(int delay, final Runnable animFinish) {
            getWrapper().postDelayed(new Runnable() {
                @Override
                public void run() {
                    showLoadSuccess(animFinish);
                }
            }, delay);
        }

        /**
         * show UI for status: {@link #STATUS_LOAD_FAILED}
         */
        public void showLoadFailed() {
            showLoadingStatus(STATUS_LOAD_FAILED);
        }

        public void showLoadFailed(Object withData) {
            withData(withData);
            showLoadFailed();
        }

        /**
         * show UI for status: {@link #STATUS_EMPTY_DATA}
         */
        public void showEmpty() {
            showLoadingStatus(STATUS_EMPTY_DATA);
        }

        public void showEmpty(Object withData) {
            withData(withData);
            showEmpty();
        }

        /**
         * Show specific status UI
         *
         * @param status status
         * @see #showLoading()
         * @see #showLoadFailed()
         * @see #showLoadSuccess()
         * @see #showEmpty()
         */
        public void showLoadingStatus(int status) {
            if (mCurState == status || !validate()) return;
            mCurState = status;
            if (mCurState == STATUS_LOAD_SUCCESS) {
                if (mSuccessRemoveDuration > 0) {
                    LayoutTransition transition = new LayoutTransition();
                    transition.setAnimator(LayoutTransition.DISAPPEARING, ObjectAnimator.ofPropertyValuesHolder(
                            (View) null,
                            PropertyValuesHolder.ofFloat("alpha", 1.0f, 0.0f)
                    ));
                    transition.setDuration(LayoutTransition.DISAPPEARING, mSuccessRemoveDuration);
                    transition.addTransitionListener(new LayoutTransition.TransitionListener() {
                        @Override
                        public void startTransition(LayoutTransition transition, ViewGroup container, View view, int transitionType) {
                        }

                        @Override
                        public void endTransition(LayoutTransition transition, ViewGroup container, View view, int transitionType) {
                            mWrapper.setLayoutTransition(null);
                        }
                    });
                    transition.setAnimateParentHierarchy(false);
                    mWrapper.setLayoutTransition(transition);
                }
                mWrapper.removeView(mCurStatusView);
            } else {
                // first try to reuse status view
                View convertView = mStatusViews.get(status);
                if (convertView == null) {
                    //secondly try to reuse current status view
                    convertView = mCurStatusView;
                }
                try {
                    // call customer adapter to get UI for specific status. convertView can be reused
                    View view = mAdapter.getView(this, convertView, status);
                    if (view == null) {
                        printLog(mAdapter.getClass().getName() + ".getView returns null");
                        return;
                    }
                    if (view != mCurStatusView || mWrapper.indexOfChild(view) < 0) {
                        if (mCurStatusView != null) {
                            mWrapper.removeView(mCurStatusView);
                        }
                        mWrapper.addView(view);
                        if (mViewLayout != null) {
                            view.setLayoutParams(mViewLayout);
                        }
//                        else {
//                            ViewGroup.LayoutParams lp = view.getLayoutParams();
//                            if (lp != null) {
//                                lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
//                                lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
//                            }
//                        }
                    } else if (mWrapper.indexOfChild(view) != mWrapper.getChildCount() - 1) {
                        // make sure loading status view at the front
                        view.bringToFront();
                    }
                    mCurStatusView = view;
                    mStatusViews.put(status, view);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        public void retry() {
            if (getRetryTask() != null) {
                showLoading();
                getRetryTask().run();
            }
        }

        private boolean validate() {
            if (mAdapter == null) {
                printLog("Loading.Adapter is not specified.");
            }
            if (mContext == null) {
                printLog("Context is null.");
            }
            if (mWrapper == null) {
                printLog("The mWrapper of loading status view is null.");
            }
            return mAdapter != null && mContext != null && mWrapper != null;
        }

        public Context getContext() {
            return mContext;
        }

        /**
         * get wrapper
         *
         * @return container of gloading
         */
        public ViewGroup getWrapper() {
            return mWrapper;
        }

        /**
         * get retry task
         *
         * @return retry task
         */
        public Runnable getRetryTask() {
            return mRetryTask;
        }

        /**
         * get extension data
         *
         * @param <T> return type
         * @return data
         */
        public <T> T getData() {
            try {
                return (T) mData;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private static void printLog(String msg) {
        Log.e("Loading", msg);
    }
}
