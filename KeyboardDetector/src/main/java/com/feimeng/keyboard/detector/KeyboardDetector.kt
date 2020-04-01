package com.feimeng.keyboard.detector

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.PopupWindow


/**
 * Author: Feimeng
 * Time:   2020/3/31
 * Description: 键盘检测器，此类通过PopupWindow在键盘显示和隐藏的时候计算window的高度变化得出键盘高度
 */
class KeyboardDetector(private var activity: Activity) : PopupWindow(activity) {
    private var mTargetView = TargetView() // 用于计算键盘高度的View
    private var mActivityView: View = activity.findViewById(android.R.id.content) // 关联的Activity的高度
    private var mKeyboardHeight = arrayOf(0, 0) // 缓存的键盘高度 [0]:portrait [1]:landscape
    private var mOrientation: Int = Configuration.ORIENTATION_UNDEFINED // 设备方向

    // 键盘状态变化监听
    private var mKeyboardChangeListenerListener: MutableList<OnKeyboardChangeListener>? = null

    init {
        contentView = mTargetView
        width = 0
        height = WindowManager.LayoutParams.MATCH_PARENT
        setBackgroundDrawable(ColorDrawable(0))
        softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE or WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE
        inputMethodMode = INPUT_METHOD_NEEDED
        mActivityView = activity.findViewById(android.R.id.content)
    }

    fun start(hold: View) {
        hold.post {
            if (!isShowing && mActivityView.windowToken != null) {
                showAtLocation(mActivityView, Gravity.NO_GRAVITY, 0, 0)
            }
        }
    }

    fun destroy() {
        mKeyboardChangeListenerListener?.clear()
        mKeyboardChangeListenerListener = null
        dismiss()
    }

    fun addKeyboardChangeListener(listener: OnKeyboardChangeListener) {
        if (mKeyboardChangeListenerListener == null) mKeyboardChangeListenerListener = ArrayList()
        mKeyboardChangeListenerListener!!.add(listener)
    }

    private fun handleKeyboardHeight() {
//        val screenSize = Point()
//        activity.windowManager.defaultDisplay.getRealSize(screenSize)
//        val validSize = Point()
//        activity.windowManager.defaultDisplay.getSize(validSize)
        val activitySize = Rect()
        activity.window.decorView.getWindowVisibleDisplayFrame(activitySize)
        val contentSect = Rect()
        mTargetView.getWindowVisibleDisplayFrame(contentSect)
//        Log.d("nodawang", "screenSize:$screenSize activitySize:$activitySize keyboardSize:$contentSect")
        val keyboardHeight: Int = activitySize.bottom - contentSect.bottom
        if (keyboardHeight == 0) {
            notifyKeyboardHeightChanged(false)
        } else {
            if (mKeyboardHeight[getHeightIndex()] != keyboardHeight) {
                mKeyboardHeight[getHeightIndex()] = keyboardHeight
                saveKeyboardHeight(keyboardHeight)
            }
            notifyKeyboardHeightChanged(true)
        }
    }

    private fun saveKeyboardHeight(height: Int) {
        getSP().edit().putInt(mOrientation.toString(), height).apply()
//        Log.d("nodawang", "保存键盘高度 orientation:${mOrientation} height:${height}")
    }

    private fun getHeightIndex(): Int {
        return if (mOrientation == Configuration.ORIENTATION_LANDSCAPE) 1 else 0
    }

    private fun getSP(): SharedPreferences {
        return activity.getSharedPreferences("KeyboardDetector", Context.MODE_PRIVATE)
    }

    private fun notifyKeyboardHeightChanged(visible: Boolean) {
        if (mKeyboardChangeListenerListener == null) return
        val height = mKeyboardHeight[getHeightIndex()]
        for (listener in mKeyboardChangeListenerListener!!) {
            listener.onKeyboardHeightChanged(visible, height, mOrientation)
        }
    }

    private inner class TargetView : View(activity) {
        override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
//            Log.d("nodawang", "onSizeChanged w:$w h:$h oldw:$oldw oldh:$oldh")
            if (oldw == 0 && oldh == 0) { // 初始化数据
                mOrientation = getScreenOrientation()
                val sp = getSP()
                mKeyboardHeight[0] = sp.getInt(Configuration.ORIENTATION_PORTRAIT.toString(), 0)
                mKeyboardHeight[1] = sp.getInt(Configuration.ORIENTATION_LANDSCAPE.toString(), 0)
//                Log.d("nodawang", "从缓存获取键盘高度 portrait:${mKeyboardHeight[0]} landscape:${mKeyboardHeight[1]}")
            }
            handleKeyboardHeight()
        }

        private fun getScreenOrientation(): Int {
            return activity.resources.configuration.orientation
        }
    }
}