package com.feimeng.keyboard.kaomojiboard.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.feimeng.keyboard.kaomojiboard.R
import com.feimeng.keyboard.kaomojiboard.model.Kaomoji
import com.feimeng.keyboard.kaomojiboard.ui.KaomojiAction

/**
 * Author: Feimeng
 * Time:   2020/3/25
 * Description:
 */
class KaomojiPageCellAdapter(private val action: KaomojiAction) : RecyclerView.Adapter<KaomojiPageCellAdapter.ViewHolder>() {
    private var mData: List<Kaomoji> = ArrayList()

    fun setData(value: List<Kaomoji>) {
        mData = value
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.kb_item_kaomoji_page_cell, parent, false) as TextView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val context = holder.itemView.context
        holder.item = mData[position]
        holder.tv.text = holder.item.body
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
        } else {
            holder.item = mData[position]
        }
    }

    inner class ViewHolder(val tv: TextView) : RecyclerView.ViewHolder(tv), View.OnClickListener {
        lateinit var item: Kaomoji

        init {
            tv.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            action.clickKaomoji(item, adapterPosition)
        }
    }
}