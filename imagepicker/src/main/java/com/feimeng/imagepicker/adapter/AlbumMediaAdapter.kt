package com.feimeng.imagepicker.adapter

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.feimeng.imagepicker.R
import com.feimeng.imagepicker.entity.AlbumMedia
import com.feimeng.imagepicker.entity.Const
import com.feimeng.imagepicker.entity.IncapableCause
import com.feimeng.imagepicker.entity.SelectionSpec
import com.feimeng.imagepicker.ui.ImagePickerAction
import java.util.*
import kotlin.collections.ArrayList

/**
 * Author: Feimeng
 * Time:   2020/3/25
 * Description:
 */
class AlbumMediaAdapter(private val action: ImagePickerAction) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        private const val VIEW_TYPE_CAPTURE = 0x01
        private const val VIEW_TYPE_MEDIA = 0x02

        private const val CHANGE_CHECK_STATUS = 0x01
    }

    private var mData: List<AlbumMedia> = ArrayList()

    fun setData(albumMedias: List<AlbumMedia>) {
        val diff = DiffUtil.calculateDiff(Diff(albumMedias, mData))
        diff.dispatchUpdatesTo(this)
        mData = albumMedias
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    fun getItem(position: Int): AlbumMedia {
        return mData[position]
    }

    fun notifySelectionChanged() {
        notifyItemRangeChanged(0, itemCount, CHANGE_CHECK_STATUS)
        action.onSelectionChange()
    }

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position).isCapture) VIEW_TYPE_CAPTURE else VIEW_TYPE_MEDIA
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_CAPTURE -> {
                CaptureViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.ip_item_picker_media_capture_photo, parent, false))
            }
            VIEW_TYPE_MEDIA -> {
                MediaViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.ip_item_picker_media, parent, false))
            }
            else -> throw IllegalArgumentException("except VIEW_TYPE_CAPTURE(0x01) or VIEW_TYPE_MEDIA(0x02), but is $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val context = holder.itemView.context
        if (holder is CaptureViewHolder) {

        } else if (holder is MediaViewHolder) {
            holder.item = mData[position]
            holder.updateImage(context)
            holder.updateCheck()
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, payloads: List<Any>) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
        } else {
            if (holder is MediaViewHolder) {
                holder.item = mData[position]
                for (payload in payloads) {
                    if (CHANGE_CHECK_STATUS == payload) holder.updateCheck()
                }
            }
        }
    }

    fun resetData() {
        val size = mData.size
        mData = Collections.emptyList()
        notifyItemRangeRemoved(0, size)
    }

    private inner class MediaViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        lateinit var item: AlbumMedia
        var image: ImageView = itemView.findViewById(R.id.image)
        var check: TextView = itemView.findViewById(R.id.check)
        val videoDuration: TextView = itemView.findViewById(R.id.videoDuration)

        init {
            itemView.setOnClickListener(this)
        }

        fun updateCheck() {
            val checkedNum: Int = action.selectionCollection().checkedNumOf(item)
            if (checkedNum == Const.UNCHECKED) {
                check.isSelected = false
                if (action.selectionCollection().maxSelectableReached()) {
                    check.isEnabled = false
                    image.alpha = 0.3f
                } else {
                    check.isEnabled = true
                    image.alpha = 1.0f
                }
            } else {
                check.text = checkedNum.toString()
                check.isSelected = true
                check.isEnabled = true
                image.alpha = 1.0f
            }
        }

        fun updateImage(context: Context) {
            if (item.isGif) {
                SelectionSpec.instance.imageEngine.loadGifThumbnail(context, 0, ColorDrawable(Color.GRAY), image, item.contentUri)
            } else {
                SelectionSpec.instance.imageEngine.loadThumbnail(context, 0, ColorDrawable(Color.GRAY), image, item.contentUri)
            }
            if (item.isVideo) {
                videoDuration.visibility = View.VISIBLE
                videoDuration.text = DateUtils.formatElapsedTime(item.duration / 1000)
            } else {
                videoDuration.visibility = View.GONE
            }
        }

        override fun onClick(v: View) {
            val checkedNum: Int = action.selectionCollection().checkedNumOf(item)
            if (checkedNum == Const.UNCHECKED) {
                if (assertAddSelection(itemView.context)) {
                    action.selectionCollection().add(item)
                    notifySelectionChanged()
                }
            } else {
                action.selectionCollection().remove(item)
                notifySelectionChanged()
            }
        }

        private fun assertAddSelection(context: Context): Boolean {
            val cause = action.selectionCollection().isAcceptable(item)
            return IncapableCause.handleCause(context, cause)
        }
    }

    private inner class CaptureViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            action.onCaptureImage()
        }
    }

    class Diff(private val newList: List<AlbumMedia>, private val oldList: List<AlbumMedia>) : DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldList.size
        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val beanOld: AlbumMedia = oldList[oldItemPosition]
            val beanNew: AlbumMedia = newList[newItemPosition]
            return beanOld === beanNew || beanOld.id == beanNew.id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return true
        }
    }
}