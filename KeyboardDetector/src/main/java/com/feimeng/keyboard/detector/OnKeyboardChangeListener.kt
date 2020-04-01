package com.feimeng.keyboard.detector

/**
 * Author: Feimeng
 * Time:   2020/3/31
 * Description: 键盘状态变化监听
 */
interface OnKeyboardChangeListener {

    /**
     * 当键盘显隐和高度变化的时候调用
     *
     * @param visible       键盘是否可见
     * @param height        键盘高度的像素
     * @param orientation   键盘方向 取值：Configuration.ORIENTATION_PORTRAIT or Configuration.ORIENTATION_LANDSCAPE
     */
    fun onKeyboardHeightChanged(visible: Boolean, height: Int, orientation: Int)
}