/*
 * Copyright (C) 2014 nohana, Inc.
 * Copyright 2017 Zhihu Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an &quot;AS IS&quot; BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.feimeng.imagepicker.collection

import android.content.Context
import android.database.Cursor
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.loader.app.LoaderManager
import androidx.loader.content.Loader

import com.feimeng.imagepicker.entity.Album
import com.feimeng.imagepicker.entity.AlbumMedia
import com.feimeng.imagepicker.loader.AlbumMediaLoader

import java.lang.ref.WeakReference

class AlbumMediaCollection : LoaderManager.LoaderCallbacks<Cursor> {
    private var mContext: WeakReference<Context>? = null
    private var mLoaderManager: LoaderManager? = null
    private var mCallbacks: AlbumMediaCallbacks? = null

    private var mLoader: Loader<Cursor>? = null

    fun onCreate(context: FragmentActivity, callbacks: AlbumMediaCallbacks) {
        mContext = WeakReference(context)
        mLoaderManager = LoaderManager.getInstance(context)
        mCallbacks = callbacks
    }

    fun onDestroy() {
        mLoaderManager!!.destroyLoader(LOADER_ID)
        mCallbacks = null
    }

    @JvmOverloads
    fun load(target: Album?, enableCapture: Boolean = false) {
        val args = Bundle()
        args.putParcelable(ARGS_ALBUM, target)
        args.putBoolean(ARGS_ENABLE_CAPTURE, enableCapture)
        mLoader = if (mLoader == null) mLoaderManager!!.initLoader(LOADER_ID, args, this) else
            mLoaderManager!!.restartLoader(LOADER_ID, args, this)
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        val context = mContext!!.get()
        val album = requireNotNull(args!!.getParcelable<Album>(ARGS_ALBUM))

        return AlbumMediaLoader.newInstance(context, album,
                album.isAll && args.getBoolean(ARGS_ENABLE_CAPTURE, false))
    }

    override fun onLoadFinished(loader: Loader<Cursor>, cursor: Cursor) {
        mContext?.get() ?: return
        val list: MutableList<AlbumMedia> = ArrayList(cursor.count)
        if (cursor.moveToFirst()) {
            do {
                try {
                    list.add(AlbumMedia.valueOf(cursor))
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } while (cursor.moveToNext())
        }
        mCallbacks!!.onAlbumMediaLoad(list)
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {
        mContext?.get() ?: return
        mCallbacks!!.onAlbumMediaReset()
    }

    interface AlbumMediaCallbacks {

        fun onAlbumMediaLoad(albumMedias: List<AlbumMedia>)

        fun onAlbumMediaReset()
    }

    companion object {
        private const val LOADER_ID = 2
        private const val ARGS_ALBUM = "args_album"
        private const val ARGS_ENABLE_CAPTURE = "args_enable_capture"
    }
}
