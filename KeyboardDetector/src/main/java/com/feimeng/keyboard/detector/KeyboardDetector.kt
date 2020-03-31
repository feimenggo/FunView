package com.feimeng.keyboard.detector

import android.app.Activity
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
 *
 */
class KeyboardDetector(private var activity: Activity) : PopupWindow(activity) {
    private var mTargetView = TargetView() // 用于计算键盘高度的View
    private var mActivityView: View = activity.findViewById(android.R.id.content) // 关联的Activity的高度
    private var mKeyboardHeight = arrayOf(0, 0) // 缓存的键盘高度 [0]:portrait [1]:landscape
    private var mOrientation: Int = Configuration.ORIENTATION_UNDEFINED // 设备方向

    // 键盘状态变化监听
    private var mKeyboardChangeListenerListener: OnKeyboardChangeListener? = null

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

    /**
     * Close the keyboard height provider,
     * this provider will not be used anymore.
     */
    fun close() {
        mKeyboardChangeListenerListener = null
        dismiss()
    }

    /**
     * Set the keyboard height observer to this provider. The
     * observer will be notified when the keyboard height has changed.
     * For example when the keyboard is opened or closed.
     *
     * @param listener The observer to be added to this provider.
     */
    fun setKeyboardChangeListener(listener: OnKeyboardChangeListener?) {
        this.mKeyboardChangeListenerListener = listener
    }

    /**
     * Popup window itself is as big as the window of the Activity.
     * The keyboard can then be calculated by extracting the popup view bottom
     * from the activity window height.
     */
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
            mKeyboardHeight[getHeightIndex()] = keyboardHeight
            notifyKeyboardHeightChanged(true)
        }
    }

    private fun getHeightIndex(): Int {
        return if (mOrientation == Configuration.ORIENTATION_LANDSCAPE) 1 else 0
    }

    private fun notifyKeyboardHeightChanged(visible: Boolean) {
        mKeyboardChangeListenerListener?.onKeyboardHeightChanged(visible, mKeyboardHeight[getHeightIndex()], mOrientation)
    }

    private inner class TargetView : View(activity) {
        override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
//            Log.d("nodawang", "onSizeChanged w:$w h:$h oldw:$oldw oldh:$oldh")
            if (oldw == 0) mOrientation = getScreenOrientation()
            handleKeyboardHeight()
        }

        private fun getScreenOrientation(): Int {
            return activity.resources.configuration.orientation
        }
    }
}