package com.feimeng.keyboard.detector

import android.animation.ValueAnimator
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.view.inputmethod.InputMethodManager

/**
 * Author: Feimeng
 * Time:   2020/3/31
 * Description: 键盘布局辅助类 实现了OnKeyboardChangeListener接口
 */
open class KeyboardLayoutHelper : OnKeyboardChangeListener {
    private var mInstantViews: MutableList<View>? = null // 瞬间移动
    private var mSmoothViews: MutableList<View>? = null // 平滑移动

    private var mSmoothAnimator: ValueAnimator? = null // 动画执行器
    private var mPanelAnimator: ValueAnimator? = null // 动画执行器

    private var mIgnoreInitChange: Boolean? = true // 是否忽略初始化的变化

    private var mKeyboardHeight: Int = 0

    private var mLayoutVisible: Boolean = false // 面板或者键盘是否显示
    private var mPanelVisible: Boolean = false // 面板是否显示
    private var mKeyboardVisible: Boolean = false // 键盘是否显示
    private var mInputManager: InputMethodManager? = null
    private var mPanelView: View? = null // 切换移动
    private var mPanelDistance: Int = 0
    private var mCallback: OnKeyboardCallback? = null

    fun addInstantView(view: View, marginBottom: Int = 0, marginBottomVisible: Int = 0): KeyboardLayoutHelper {
        if (mInstantViews == null) mInstantViews = ArrayList(1)
        view.setTag(R.id.animMarginBottom, marginBottom)
        view.setTag(R.id.animMarginBottomVisible, marginBottomVisible)
        mInstantViews!!.add(view)
        return this
    }

    open fun addSmoothView(vararg views: View): KeyboardLayoutHelper {
        if (mSmoothViews == null) mSmoothViews = ArrayList(views.size)
        mSmoothViews!!.addAll(views)
        return this
    }

    fun ignoreInitChange(ignore: Boolean): KeyboardLayoutHelper {
        mIgnoreInitChange = ignore
        return this
    }

    fun enablePanel(context: Context, panel: View, callback: OnKeyboardCallback): KeyboardLayoutHelper {
        mInputManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        mPanelView = panel
        mCallback = callback
        // 初始化状态
        panel.visibility = View.GONE
        return this
    }

    fun enablePanel(context: Context, panel: View, showDistance: Int, callback: OnKeyboardCallback): KeyboardLayoutHelper {
        mInputManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        mPanelView = panel
        mPanelDistance = showDistance
        mCallback = callback
        // 初始化位置
        panel.translationY = 10000.toFloat()
        return this
    }

    /**
     * 获取面板或者键盘是否显示
     */
    fun isShowing(): Boolean {
        return mLayoutVisible
    }

    /**
     * 获取面板是否可见
     */
    fun isPanelVisible(): Boolean {
        return mPanelVisible
    }

    /**
     * 显示面板
     */
    fun showPanel() {
        if (mInputManager == null) throw IllegalArgumentException("请先调用enablePanel()方法，激活面板功能")
        if (mPanelVisible) return
        onPanelVisibleChange(true)
        // 键盘打开状态下，关闭键盘
        if (mKeyboardVisible) {
            hideKeyboard()
        } else {
            showLayout()
        }
    }

    /**
     * 隐藏面板，隐藏后将激活键盘
     */
    fun hidePanel() {
        if (!mPanelVisible) return
        onPanelVisibleChange(false)
        if (!mKeyboardVisible) showKeyboard()
    }

    /**
     * 切换面板可见性
     */
    fun togglePanel() {
        if (mPanelVisible) {
            hidePanel()
        } else {
            showPanel()
        }
    }

    /**
     * 隐藏面板和键盘
     */
    fun hide() {
        if (mPanelVisible) onPanelVisibleChange(false)
        if (mKeyboardVisible) {
            hideKeyboard()
        } else {
            hideLayout()
        }
    }

    private fun showKeyboard() {
        mInputManager!!.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_IMPLICIT_ONLY)
    }

    private fun hideKeyboard() {
        mInputManager!!.hideSoftInputFromWindow(mPanelView!!.applicationWindowToken, 0)
    }

    private fun onPanelVisibleChange(visible: Boolean) {
        mPanelVisible = visible
        mCallback?.onPanelVisibility(visible)
    }

    private fun onKeyboardVisibleChange(visible: Boolean) {
        mKeyboardVisible = visible
        mCallback?.onKeyboardVisibility(visible, mKeyboardHeight)
    }

    private fun showLayout() {
        if (mLayoutVisible) return
        handleLayoutVisible(true, mKeyboardHeight)
    }

    private fun hideLayout() {
        if (!mLayoutVisible) return
        handleLayoutVisible(false, mKeyboardHeight)
    }

    /**
     * 键盘可见性改变
     */
    override fun onKeyboardHeightChanged(visible: Boolean, height: Int, orientation: Int) {
        mKeyboardHeight = height
        onKeyboardVisibleChange(visible)

        if (mIgnoreInitChange != null) {
            if (mIgnoreInitChange as Boolean) {
                mIgnoreInitChange = null
                return
            } else {
                mIgnoreInitChange = null
            }
        }
        if (visible) {
            if (mPanelVisible) onPanelVisibleChange(false)
            showLayout()
        } else {
            if (!mPanelVisible) hideLayout()
        }
    }

    /**
     * 处理布局可见性变动
     */
    private fun handleLayoutVisible(visible: Boolean, height: Int) {
        mSmoothAnimator?.end()
        mPanelAnimator?.end()

        mLayoutVisible = visible

        // 瞬间移动
        if (mInstantViews != null) {
            var handlerView: View? = null
            val endHeight = if (visible) height else 0
            for (view in mInstantViews!!) {
                if (handlerView == null) handlerView = view
                var marginBottom: Int
                if (visible) {
                    marginBottom = view.getTag(R.id.animMarginBottomVisible) as Int
                } else {
                    marginBottom = view.getTag(R.id.animMarginBottom) as Int
                }
                (view.layoutParams as ViewGroup.MarginLayoutParams).bottomMargin = endHeight + marginBottom
            }
            handlerView?.requestLayout()
        }

        // 平滑移动
        if (mSmoothViews != null) {
            if (visible) {
                mSmoothAnimator = ValueAnimator.ofInt(0, height)
                mSmoothAnimator!!.interpolator = LinearInterpolator()
                mSmoothAnimator!!.duration = 350
            } else {
                mSmoothAnimator = ValueAnimator.ofInt(height, 0)
                mSmoothAnimator!!.duration = 350
            }
            mSmoothAnimator!!.addUpdateListener { listener ->
                val value = listener.animatedValue as Int
                for (view in mSmoothViews!!) {
                    view.translationY = -value.toFloat()
                }
            }
            mSmoothAnimator!!.start()
        }

        // 面板式
        if (mPanelView != null) {
            if (mPanelDistance == 0) { // 显隐
                if (visible) {
                    if (mPanelView!!.layoutParams.height != height) {
                        mPanelView!!.layoutParams.height = height
                        mPanelView!!.requestLayout()
                    }
                    mPanelView!!.visibility = View.VISIBLE
                } else {
                    mPanelView!!.visibility = View.GONE
                }
            } else { // 移动
                if (visible) {
                    val panelView = mPanelView!!.findViewById<View>(R.id.keyboardDetectorPanel)
                    if (panelView.layoutParams.height != height) {
                        panelView.layoutParams.height = height
                        panelView.requestLayout()
                    }

                    mPanelAnimator = ValueAnimator.ofInt(mPanelDistance, 0)
                    mPanelAnimator!!.interpolator = DecelerateInterpolator()
                    mPanelAnimator!!.duration = 400
                    mPanelAnimator!!.startDelay = 80
                } else {
                    mPanelAnimator = ValueAnimator.ofInt(0, mPanelView!!.height)
                    mPanelAnimator!!.duration = 350
                }
                mPanelAnimator!!.addUpdateListener { listener ->
                    val value = listener.animatedValue as Int
                    mPanelView!!.translationY = value.toFloat()
                }
                mPanelAnimator!!.start()
            }
        }
    }

    interface OnKeyboardCallback {
        /**
         * 当键盘可见性变化时回调
         */
        fun onKeyboardVisibility(showing: Boolean, keyboardHeight: Int)

        /**
         * 当面板可见性变化时回调
         */
        fun onPanelVisibility(showing: Boolean)
    }
}