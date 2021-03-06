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
 * distributed under the License is distributed on an &quot;AS IS&quot; BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.feimeng.imagepicker.entity


import android.annotation.SuppressLint
import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.IntDef
import com.feimeng.imagepicker.R
import com.feimeng.imagepicker.ui.dialog.IncapableDialog
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy.SOURCE

@Suppress("DEPRECATED_JAVA_ANNOTATION")
class IncapableCause {
    private var mForm = TOAST
    private var mTitle: String? = null
    private var mMessage: String? = null

    @Retention(SOURCE)
    @IntDef(TOAST, DIALOG, NONE)
    annotation class Form {

    }

    constructor(message: String) {
        mMessage = message
    }

    constructor(title: String, message: String) {
        mTitle = title
        mMessage = message
    }

    constructor(@Form form: Int, message: String) {
        mForm = form
        mMessage = message
    }

    constructor(@Form form: Int, title: String, message: String) {
        mForm = form
        mTitle = title
        mMessage = message
    }

    companion object {
        const val TOAST = 0x00
        const val DIALOG = 0x01
        const val NONE = 0x02

        @SuppressLint("InflateParams")
        fun handleCause(context: Context, cause: IncapableCause?): Boolean {
            if (cause == null) return true
            when (cause.mForm) {
                NONE -> {
                }
                DIALOG -> {
                    val incapableDialog = IncapableDialog.newInstance(cause.mTitle!!, cause.mMessage!!)
                    incapableDialog.show((context as androidx.fragment.app.FragmentActivity).supportFragmentManager,
                            IncapableDialog::class.java.name)
                }
                TOAST -> {
                    val toast: Toast = Toast.makeText(context, cause.mMessage, Toast.LENGTH_SHORT)
                    val viewGroup = LayoutInflater.from(context).inflate(R.layout.ip_view_toast, null) as ViewGroup
                    val msg = viewGroup.getChildAt(0) as TextView
                    msg.text = cause.mMessage
                    toast.view = viewGroup
                    toast.setGravity(Gravity.CENTER, 0, 0)
                    toast.show()
                }
            }
            return false
        }
    }
}
