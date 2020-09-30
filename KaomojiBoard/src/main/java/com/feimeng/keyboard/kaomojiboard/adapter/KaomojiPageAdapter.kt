package com.feimeng.keyboard.kaomojiboard.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.feimeng.keyboard.kaomojiboard.R
import com.feimeng.keyboard.kaomojiboard.model.Kaomoji
import com.feimeng.keyboard.kaomojiboard.model.KaomojiPackage
import com.feimeng.keyboard.kaomojiboard.model.KaomojiPage
import com.feimeng.keyboard.kaomojiboard.ui.KaomojiAction
import com.feimeng.keyboard.kaomojiboard.ui.widget.GridInset
import kotlin.math.ceil

/**
 * Author: Feimeng
 * Time:   2020/3/25
 * Description:
 */
class KaomojiPageAdapter(private val row: Int, private val column: Int, private val action: KaomojiAction) : RecyclerView.Adapter<KaomojiPageAdapter.ViewHolder>() {
    private var mVP: ViewPager2? = null

    private var mData = ArrayList<KaomojiPage>()

    private var mKaomojiPackages: List<KaomojiPackage>? = null
    private var mKaomojiPackagePageIndex: Array<Int>? = null

    fun bind(vp: ViewPager2): KaomojiPageAdapter {
        mVP = vp
        return this
    }

    fun setData(list: List<KaomojiPackage>) {
        mKaomojiPackages = list
        mKaomojiPackagePageIndex = Array(list.size, init = { 0 })
        // 将颜文字包列表平铺
        val sizePerPage = row * column
        mData = ArrayList()
        for ((packageIndex, kaomojiPackage) in list.withIndex()) {
            var pageData: ArrayList<Kaomoji>? = null
            val pageCount = ceil(kaomojiPackage.list.size / sizePerPage.toDouble()).toInt()
            mKaomojiPackagePageIndex!![packageIndex] = mData.size
            for ((pageIndex, data) in kaomojiPackage.list.withIndex()) {
                if (pageIndex % sizePerPage == 0) {
                    pageData = ArrayList()
                    mData.add(KaomojiPage(kaomojiPackage, pageData, packageIndex, pageIndex / sizePerPage, pageCount))
                }
                pageData!!.add(data)
            }
        }
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    fun getKaomojiPage(pageIndex: Int): KaomojiPage {
        return mData[pageIndex]
    }

    fun getCurrentKaomojiPackage(): KaomojiPackage {
        return mData[mVP!!.currentItem].kaomojiPackage
    }

    fun setCurrentKaomojiPackage(kaomojiPackage: KaomojiPackage) {
        val index = mKaomojiPackages?.indexOf(kaomojiPackage) ?: -1
        if (index > -1) mVP?.setCurrentItem(mKaomojiPackagePageIndex!![index], false)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.kb_item_kaomoji_page, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val context = holder.itemView.context
        holder.item = mData[position]
        holder.adapter.setData(holder.item.data)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
        } else {
            holder.item = mData[position]
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        lateinit var item: KaomojiPage
        val rv: RecyclerView = itemView as RecyclerView
        val adapter: KaomojiPageCellAdapter = KaomojiPageCellAdapter(action)

        init {
            val context = itemView.context
            rv.itemAnimator = null
            rv.setHasFixedSize(true)
            rv.layoutManager = GridLayoutManager(context, 4)
            rv.addItemDecoration(GridInset(4, 0,
                    context.resources.getDimensionPixelOffset(R.dimen.kaomojiVerticalSpace), false))
            rv.adapter = adapter
        }
    }
}