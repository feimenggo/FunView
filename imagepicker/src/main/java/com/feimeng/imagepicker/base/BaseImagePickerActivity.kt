package com.feimeng.imagepicker.base

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.gyf.immersionbar.ImmersionBar

/**
 * Author: Feimeng
 * Time:   2020/3/25
 * Description: 基类
 */
abstract class BaseImagePickerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initStatusBar()
    }

    protected fun initStatusBar() {
        val bar = ImmersionBar.with(this)
        bar.statusBarColorInt(Color.TRANSPARENT)
        bar.statusBarDarkFont(true, 0.6f)
        bar.init()
    }
}