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
import com.feimeng.imagepicker.entity.Album
import com.feimeng.imagepicker.loader.AlbumLoader
import java.lang.ref.WeakReference

class AlbumCollection : LoaderManager.LoaderCallbacks<Cursor> {
    private lateinit var mContext: WeakReference<Context>
    private lateinit var mLoaderManager: LoaderManager
    private var mCallbacks: AlbumCallbacks? = null
    var currentSelection: Int = 0
        private set

    fun onCreate(context: FragmentActivity, callbacks: AlbumCallbacks) {
        mContext = WeakReference(context)
        mLoaderManager = LoaderManager.getInstance(context)
        mCallbacks = callbacks
    }

    fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) return
        currentSelection = savedInstanceState.getInt(STATE_CURRENT_SELECTION)
    }

    fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(STATE_CURRENT_SELECTION, currentSelection)
    }

    fun onDestroy() {
        mLoaderManager.destroyLoader(LOADER_ID)
        mCallbacks = null
    }

    fun loadAlbums() {
        mLoaderManager.initLoader(LOADER_ID, null, this)
    }

    fun setStateCurrentSelection(currentSelection: Int) {
        this.currentSelection = currentSelection
    }

    override fun onCreateLoader(id: Int, args: Bundle?): androidx.loader.content.Loader<Cursor> {
        return AlbumLoader.newInstance(mContext.get())
    }

    override fun onLoadFinished(loader: androidx.loader.content.Loader<Cursor>, cursor: Cursor) {
        val list: MutableList<Album> = ArrayList(cursor.count)
        if (cursor.moveToFirst()) {
            do {
                try {
                    list.add(Album.valueOf(cursor))
                } catch (ignored: Exception) {
                }
            } while (cursor.moveToNext())
        }
        mCallbacks?.onAlbumLoad(list)
    }

    override fun onLoaderReset(loader: androidx.loader.content.Loader<Cursor>) {
        mCallbacks?.onAlbumReset()
    }

    interface AlbumCallbacks {
        fun onAlbumLoad(albums: List<Album>)

        fun onAlbumReset()
    }

    companion object {
        private const val LOADER_ID = 1
        private const val STATE_CURRENT_SELECTION = "state_current_selection"
    }
}
