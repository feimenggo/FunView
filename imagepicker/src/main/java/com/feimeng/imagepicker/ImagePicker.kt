package com.feimeng.imagepicker

import android.app.Activity
import androidx.fragment.app.Fragment
import com.feimeng.imagepicker.entity.SelectionSpec
import com.feimeng.imagepicker.ui.ImagePickerActivity

/**
 * Author: Feimeng
 * Time:   2020/3/25
 * Description: 图片选择器
 */
class ImagePicker private constructor() {
    companion object {
        const val PICKED_MEDIA = "PICKED_MEDIA"

        fun start(context: Activity, requestCode: Int, selectionSpec: SelectionSpec) {
            SelectionSpec.instance = selectionSpec
            ImagePickerActivity.start(context, requestCode)
        }

        fun start(context: Fragment, requestCode: Int, selectionSpec: SelectionSpec) {
            SelectionSpec.instance = selectionSpec
            ImagePickerActivity.start(context, requestCode)
        }
    }
}