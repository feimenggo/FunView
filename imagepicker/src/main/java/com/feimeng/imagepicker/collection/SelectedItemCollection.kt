/*
 * Copyright 2017 Zhihu Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.feimeng.imagepicker.collection


import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.net.Uri
import android.os.Bundle
import com.feimeng.imagepicker.R
import com.feimeng.imagepicker.entity.AlbumMedia
import com.feimeng.imagepicker.entity.Const
import com.feimeng.imagepicker.entity.IncapableCause
import com.feimeng.imagepicker.entity.SelectionSpec
import com.feimeng.imagepicker.util.PhotoMetadataUtils
import java.util.*

class SelectedItemCollection(private val mContext: Context) {
    private var mAlbumMedia: MutableSet<AlbumMedia>? = null
    private var collectionType = COLLECTION_UNDEFINED

    val dataWithBundle: Bundle
        get() {
            val bundle = Bundle()
            bundle.putParcelableArrayList(STATE_SELECTION, ArrayList(mAlbumMedia!!))
            bundle.putInt(STATE_COLLECTION_TYPE, collectionType)
            return bundle
        }

    val isEmpty: Boolean
        get() = mAlbumMedia == null || mAlbumMedia!!.isEmpty()

    fun onCreate(bundle: Bundle?) {
        if (bundle == null) {
            mAlbumMedia = LinkedHashSet()
        } else {
            val saved = bundle.getParcelableArrayList<AlbumMedia>(STATE_SELECTION)
            mAlbumMedia = LinkedHashSet(saved!!)
            collectionType = bundle.getInt(STATE_COLLECTION_TYPE, COLLECTION_UNDEFINED)
        }
    }

    fun setDefaultSelection(urises: List<AlbumMedia>) {
        mAlbumMedia!!.addAll(urises)
    }

    fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelableArrayList(STATE_SELECTION, ArrayList(mAlbumMedia!!))
        outState.putInt(STATE_COLLECTION_TYPE, collectionType)
    }

    fun add(albumMedia: AlbumMedia): Boolean {
        if (typeConflict(albumMedia)) {
            throw IllegalArgumentException("Can't select images and videos at the same time.")
        }
        val added = mAlbumMedia!!.add(albumMedia)
        if (added) {
            if (collectionType == COLLECTION_UNDEFINED) {
                if (albumMedia.isImage) {
                    collectionType = COLLECTION_IMAGE
                } else if (albumMedia.isVideo) {
                    collectionType = COLLECTION_VIDEO
                }
            } else if (collectionType == COLLECTION_IMAGE) {
                if (albumMedia.isVideo) {
                    collectionType = COLLECTION_MIXED
                }
            } else if (collectionType == COLLECTION_VIDEO) {
                if (albumMedia.isImage) {
                    collectionType = COLLECTION_MIXED
                }
            }
        }
        return added
    }

    fun remove(albumMedia: AlbumMedia): Boolean {
        val removed = mAlbumMedia!!.remove(albumMedia)
        if (removed) {
            if (mAlbumMedia!!.size == 0) {
                collectionType = COLLECTION_UNDEFINED
            } else {
                if (collectionType == COLLECTION_MIXED) {
                    refineCollectionType()
                }
            }
        }
        return removed
    }

    fun overwrite(albumMedia: ArrayList<AlbumMedia>, collectionType: Int) {
        if (albumMedia.size == 0) {
            this.collectionType = COLLECTION_UNDEFINED
        } else {
            this.collectionType = collectionType
        }
        mAlbumMedia!!.clear()
        mAlbumMedia!!.addAll(albumMedia)
    }


    fun asList(): ArrayList<AlbumMedia> {
        return ArrayList(mAlbumMedia!!)
    }

    fun asListOfUri(): ArrayList<Uri> {
        val uris = ArrayList<Uri>(mAlbumMedia!!.size)
        for (item in mAlbumMedia!!) {
            uris.add(item.contentUri)
        }
        return uris
    }

    fun asListOfUri(albumMedia: ArrayList<AlbumMedia>): ArrayList<Uri> {
        val uris = ArrayList<Uri>(albumMedia.size)
        for (item in albumMedia) {
            uris.add(item.contentUri)
        }
        return uris
    }

    fun isSelected(albumMedia: AlbumMedia): Boolean {
        return mAlbumMedia!!.contains(albumMedia)
    }

    @SuppressLint("ResourceType")
    fun isAcceptable(albumMedia: AlbumMedia): IncapableCause? {
        if (maxSelectableReached()) {
            val maxSelectable = currentMaxSelectable()
            val cause = try {
                mContext.resources.getQuantityString(
                        R.string.error_over_count,
                        maxSelectable,
                        maxSelectable
                )
            } catch (e: Resources.NotFoundException) {
                mContext.getString(
                        R.string.error_over_count,
                        maxSelectable
                )
            }
            return IncapableCause(cause)
        } else if (typeConflict(albumMedia)) {
            return IncapableCause(mContext.getString(R.string.error_type_conflict))
        }

        return PhotoMetadataUtils.isAcceptable(mContext, albumMedia)
    }

    fun maxSelectableReached(): Boolean {
        return mAlbumMedia!!.size == currentMaxSelectable()
    }

    // depends
    private fun currentMaxSelectable(): Int {
        val spec = SelectionSpec.instance
        return if (spec!!.maxSelectable > 0) {
            spec.maxSelectable
        } else if (collectionType == COLLECTION_IMAGE) {
            spec.maxImageSelectable
        } else if (collectionType == COLLECTION_VIDEO) {
            spec.maxVideoSelectable
        } else {
            spec.maxSelectable
        }
    }

    private fun refineCollectionType() {
        var hasImage = false
        var hasVideo = false
        for (i in mAlbumMedia!!) {
            if (i.isImage && !hasImage) hasImage = true
            if (i.isVideo && !hasVideo) hasVideo = true
        }
        if (hasImage && hasVideo) {
            collectionType = COLLECTION_MIXED
        } else if (hasImage) {
            collectionType = COLLECTION_IMAGE
        } else if (hasVideo) {
            collectionType = COLLECTION_VIDEO
        }
    }

    /**
     * Determine whether there will be conflict media types. A user can only select images and videos at the same time
     * while [SelectionSpec.mediaTypeExclusive] is set to false.
     */
    fun typeConflict(albumMedia: AlbumMedia): Boolean {
        return SelectionSpec.instance!!.mediaTypeExclusive && (albumMedia.isImage && (collectionType == COLLECTION_VIDEO || collectionType == COLLECTION_MIXED) || albumMedia.isVideo && (collectionType == COLLECTION_IMAGE || collectionType == COLLECTION_MIXED))
    }

    fun count(): Int {
        return mAlbumMedia!!.size
    }

    fun checkedNumOf(albumMedia: AlbumMedia): Int {
        val index = ArrayList(mAlbumMedia!!).indexOf(albumMedia)
        return if (index == -1) Const.UNCHECKED else index + 1
    }

    companion object {

        const val STATE_SELECTION = "state_selection"
        const val STATE_COLLECTION_TYPE = "state_collection_type"

        /**
         * Empty collection
         */
        const val COLLECTION_UNDEFINED = 0x00

        /**
         * Collection only with images
         */
        const val COLLECTION_IMAGE = 0x01

        /**
         * Collection only with videos
         */
        const val COLLECTION_VIDEO = 0x01 shl 1

        /**
         * Collection with images and videos.
         */
        const val COLLECTION_MIXED = COLLECTION_IMAGE or COLLECTION_VIDEO
    }
}
