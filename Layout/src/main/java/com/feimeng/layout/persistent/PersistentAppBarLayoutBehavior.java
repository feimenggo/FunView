package com.feimeng.layout.persistent;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.OverScroller;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.appbar.AppBarLayout;

import java.lang.reflect.Field;

/**
 * 解决AppBarLayout滚动抖动问题
 */
public class PersistentAppBarLayoutBehavior extends AppBarLayout.Behavior {
    public PersistentAppBarLayoutBehavior() {
        super();
    }

    public PersistentAppBarLayoutBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(@NonNull CoordinatorLayout parent, @NonNull AppBarLayout child, MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            Object scroller = getSuperSuperField(this, "scroller");
            if (scroller instanceof OverScroller) {
                OverScroller overScroller = (OverScroller) scroller;
                overScroller.abortAnimation();
            }
        }
        return super.onInterceptTouchEvent(parent, child, ev);
    }

    private Object getSuperSuperField(Object paramClass, String paramString) {
        try {
            Field field = paramClass.getClass().getSuperclass().getSuperclass().getSuperclass().getDeclaredField(paramString);
            field.setAccessible(true);
            return field.get(paramClass);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
