package com.feimeng.imagepicker.adapter

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.feimeng.imagepicker.R
import com.feimeng.imagepicker.entity.Album
import com.feimeng.imagepicker.entity.SelectionSpec
import com.feimeng.imagepicker.ui.ImagePickerAction
import java.util.*
import kotlin.collections.ArrayList

/**
 * Author: Feimeng
 * Time:   2020/3/25
 * Description:
 */
class AlbumAdapter(private val action: ImagePickerAction) : RecyclerView.Adapter<AlbumAdapter.ViewHolder>() {
    private var mData: List<Album> = ArrayList()

    fun setData(value: List<Album>) {
        val diff = DiffUtil.calculateDiff(Diff(value, mData))
        diff.dispatchUpdatesTo(this)
        mData = value
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_picker_album, parent, false), action)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val context = holder.itemView.context
        holder.item = mData[position]
        SelectionSpec.instance.imageEngine.loadThumbnail(context, 0, ColorDrawable(Color.GRAY), holder.image, Uri.parse(holder.item.coverPath))
        holder.title.text = "${holder.item.getDisplayName(context)} (${holder.item.count})"
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
        } else {
            holder.item = mData[position]
        }
    }

    fun setSelection(currentSelection: Int) {

    }

    fun resetData() {
        val size = mData.size
        mData = Collections.emptyList()
        notifyItemRangeRemoved(0, size)
    }

    class ViewHolder(item: View, private val action: ImagePickerAction) : RecyclerView.ViewHolder(item), View.OnClickListener {
        lateinit var item: Album
        var image: ImageView = item.findViewById(R.id.image)
        var title: TextView = item.findViewById(R.id.title)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            action.onSelectAlbum(item, adapterPosition)
        }
    }

    class Diff(private val newList: List<Album>, private val oldList: List<Album>) : DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldList.size
        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val beanOld: Album = oldList[oldItemPosition]
            val beanNew: Album = newList[newItemPosition]
            return beanOld === beanNew || beanOld.id == beanNew.id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return true
        }
    }
}