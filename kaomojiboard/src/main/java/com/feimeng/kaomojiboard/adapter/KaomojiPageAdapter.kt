package com.feimeng.kaomojiboard.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.feimeng.kaomojiboard.PageView
import com.feimeng.kaomojiboard.R
import com.feimeng.kaomojiboard.model.Kaomoji
import com.feimeng.kaomojiboard.model.KaomojiPage
import com.feimeng.kaomojiboard.ui.KaomojiAction

/**
 * Author: Feimeng
 * Time:   2020/3/25
 * Description:
 */
class KaomojiPageAdapter(private val action: KaomojiAction) : RecyclerView.Adapter<KaomojiPageAdapter.ViewHolder>() {
    private var mData: List<KaomojiPage> = ArrayList()

    fun setData(value: List<KaomojiPage>) {
        mData = value
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.kb_item_kaomoji_page, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val context = holder.itemView.context
        holder.item = mData[position]
        holder.pageView.setData(holder.item)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
        } else {
            holder.item = mData[position]
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), PageView.OnClickKaomojiListener {
        lateinit var item: KaomojiPage
        val pageView: PageView = itemView as PageView

        init {
            pageView.onClickKaomojiListener = this
        }

        override fun clickKaomoji(kaomoji: Kaomoji) {
            action.clickKaomoji(kaomoji, adapterPosition)
        }
    }
}