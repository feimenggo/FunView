package com.feimeng.imagepicker.ui

import com.feimeng.imagepicker.collection.SelectedItemCollection
import com.feimeng.imagepicker.entity.Album

/**
 * Author: Feimeng
 * Time:   2020/3/26
 * Description:
 */
interface ImagePickerAction {
    fun selectionCollection(): SelectedItemCollection

    fun onSelectAlbum(album: Album, position: Int)

    fun onSelectionChange()

    fun onCaptureImage()
}