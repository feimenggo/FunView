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
package com.feimeng.imagepicker.ui.dialog

import android.app.Dialog
import android.os.Bundle
import android.text.TextUtils
import androidx.appcompat.app.AlertDialog
import com.feimeng.imagepicker.R

class IncapableDialog : androidx.fragment.app.DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val title = arguments!!.getString(EXTRA_TITLE)
        val message = arguments!!.getString(EXTRA_MESSAGE)

        val builder = AlertDialog.Builder(activity!!)
        if (!TextUtils.isEmpty(title)) {
            builder.setTitle(title)
        }
        if (!TextUtils.isEmpty(message)) {
            builder.setMessage(message)
        }
        builder.setPositiveButton(R.string.button_ok) { dialog, _ -> dialog.dismiss() }

        return builder.create()
    }

    companion object {

        const val EXTRA_TITLE = "extra_title"
        const val EXTRA_MESSAGE = "extra_message"

        fun newInstance(title: String, message: String): IncapableDialog {
            val dialog = IncapableDialog()
            val args = Bundle()
            args.putString(EXTRA_TITLE, title)
            args.putString(EXTRA_MESSAGE, message)
            dialog.arguments = args
            return dialog
        }
    }
}
