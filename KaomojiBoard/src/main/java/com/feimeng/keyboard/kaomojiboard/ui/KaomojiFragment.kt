package com.feimeng.keyboard.kaomojiboard.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.feimeng.keyboard.kaomojiboard.KaomojiBoard
import com.feimeng.keyboard.kaomojiboard.OnKaomojiClickEvent
import com.feimeng.keyboard.kaomojiboard.R
import com.feimeng.keyboard.kaomojiboard.adapter.KaomojiPageAdapter
import com.feimeng.keyboard.kaomojiboard.base.BaseFragment
import com.feimeng.keyboard.kaomojiboard.model.Kaomoji
import com.feimeng.keyboard.kaomojiboard.model.KaomojiPackage
import com.feimeng.keyboard.kaomojiboard.ui.widget.CategoryView
import com.feimeng.keyboard.kaomojiboard.ui.widget.IndicateView

/**
 * Author: Feimeng
 * Time:   2020/3/27
 * Description: 颜文字键盘 主界面
 */
class KaomojiFragment : BaseFragment(), KaomojiAction, CategoryView.OnClickKaomojiPackageListener {
    private lateinit var mIndicateView: IndicateView
    private lateinit var mCategoryView: CategoryView
    private lateinit var mVP: ViewPager2
    private val mAdapter = KaomojiPageAdapter(4, 4, this)
    private val mData: MutableList<KaomojiPackage> = ArrayList()

    private var mEvent: OnKaomojiClickEvent? = null

    internal companion object {
        fun newInstance(): KaomojiFragment {
            return KaomojiFragment()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnKaomojiClickEvent) {
            mEvent = context
        } else if (parentFragment is OnKaomojiClickEvent) {
            mEvent = parentFragment as OnKaomojiClickEvent
        }
    }

    override fun createView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.kb_fragment_kaomoji, container, false)
    }

    override fun initView(rootView: View, savedInstanceState: Bundle?) {
        mIndicateView = rootView.findViewById(R.id.indicate)
        mCategoryView = rootView.findViewById(R.id.category)
        mVP = rootView.findViewById(R.id.vp)
        mVP.adapter = mAdapter.bind(mVP)
        mIndicateView.bind(mVP, mAdapter)
        mCategoryView.bind(mVP, mAdapter)
        mCategoryView.setOnClickKaomojiPackageListener(this)
    }

    override fun initData() {
        if (KaomojiBoard.instance == null) return
        mData.addAll(KaomojiBoard.instance!!.loader.load(requireContext(), ""))
        mAdapter.setData(mData)
        mCategoryView.setData(mData)
    }

    override fun clickKaomoji(kaomoji: Kaomoji, position: Int) {
        mEvent?.onKaomojiClick(kaomoji)
    }

    override fun onClickKaomojiPackage(kaomojiPackage: KaomojiPackage, position: Int) {
        val currentKaomojiPackage = mAdapter.getCurrentKaomojiPackage()
        if (currentKaomojiPackage != kaomojiPackage) {
            mAdapter.setCurrentKaomojiPackage(kaomojiPackage)
        }
    }
}