package com.feimeng.imagepicker.adapter

import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.github.chrisbanes.photoview.PhotoView
import com.feimeng.imagepicker.entity.AlbumMedia
import com.feimeng.imagepicker.entity.SelectionSpec

/**
 * Author: Feimeng
 * Time:   2020/3/26
 * Description:
 */
class PreviewPagerAdapter(private var albumMedias: List<AlbumMedia>) : PagerAdapter() {
    override fun getCount(): Int {
        return albumMedias.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val albumMedia = albumMedias[position]
        val context = container.context
        val photoView = PhotoView(context)
        if (albumMedia.isGif) {
            SelectionSpec.instance.imageEngine.loadGifImage(context, 0, 0, photoView, albumMedia.contentUri)
        } else {
            SelectionSpec.instance.imageEngine.loadImage(context, 0, 0, photoView, albumMedia.contentUri)
        }
        container.addView(photoView)
        return photoView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    fun getAlbumMedia(position: Int): AlbumMedia? {
        return albumMedias[position]
    }
}