package com.feimeng.kaomojiboard.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.viewpager2.widget.ViewPager2
import com.feimeng.kaomojiboard.KaomojiBoard
import com.feimeng.kaomojiboard.R
import com.feimeng.kaomojiboard.adapter.KaomojiPageAdapter
import com.feimeng.kaomojiboard.base.BaseFragment
import com.feimeng.kaomojiboard.model.Kaomoji
import com.feimeng.kaomojiboard.model.KaomojiPage

/**
 * Author: Feimeng
 * Time:   2020/3/27
 * Description: 颜文字键盘 主界面
 */
class KaomojiFragment : BaseFragment(), KaomojiAction {
    private lateinit var mIndicateView: LinearLayout
    private lateinit var mCategoryView: LinearLayout
    private lateinit var mVP: ViewPager2
    private val mAdapter = KaomojiPageAdapter(this)

    private var mSize: Int = 16
    private val mData: MutableList<Kaomoji> = ArrayList()

    internal companion object {
        fun newInstance(): KaomojiFragment {
            return KaomojiFragment()
        }
    }

    override fun createView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.kb_fragment_kaomoji, container, false)
    }

    override fun initView(rootView: View, savedInstanceState: Bundle?) {
        mIndicateView = rootView.findViewById(R.id.indicate)
        mCategoryView = rootView.findViewById(R.id.category)
        mVP = rootView.findViewById(R.id.vp)
        mVP.adapter = mAdapter

    }

    override fun initData() {
        if (KaomojiBoard.instance == null) return
        mData.addAll(KaomojiBoard.instance!!.loader.load(requireContext(), ""))
        val page = ArrayList<KaomojiPage>()
        var pageData: ArrayList<Kaomoji>? = null
        for ((index, data) in mData.withIndex()) {
            if (index % mSize == 0) {
                pageData = ArrayList()
                page.add(KaomojiPage(pageData))
            }
            pageData!!.add(data)
        }
        mAdapter.setData(page)
    }

    override fun clickKaomoji(kaomoji: Kaomoji, position: Int) {
        Toast.makeText(context, kaomoji.body, Toast.LENGTH_SHORT).show()
    }
}