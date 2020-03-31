package com.feimeng.keyboard.kaomojiboard.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

/**
 * Author: Feimeng
 * Time:   2020/3/27
 * Description: Fragment基类
 */
abstract class BaseFragment : Fragment() {
    private var mIsFirstLoad = true // 是否第一次加载

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = createView(inflater, container, savedInstanceState)
        initView(view, savedInstanceState)
        return view
    }

    override fun onResume() {
        super.onResume()
        if (mIsFirstLoad) { // 将数据加载逻辑放到onResume()方法中
            mIsFirstLoad = false
            initData()
            onVisible(true)
        } else {
            onVisible(false)
        }
    }

    override fun onPause() {
        super.onPause()
        onInvisible()
    }

    /**
     * 创建布局
     */
    protected abstract fun createView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View

    /**
     * 初始化view
     *
     * @param rootView 根布局
     */
    protected abstract fun initView(rootView: View, savedInstanceState: Bundle?)

    /**
     * 初始化数据
     */
    protected open fun initData() {}

    /**
     * 对用户可见
     *
     * @param isFirstVisible 是否是首次可见
     */
    fun onVisible(isFirstVisible: Boolean) {
//        L.e("nodawang", getClass().getSimpleName() + "  对用户可见");
    }

    /**
     * 对用户不可见
     */
    fun onInvisible() {
//        L.e("nodawang", getClass().getSimpleName() + "  对用户不可见");
    }
}