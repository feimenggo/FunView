package com.feimeng.kaomojiboard.loader.impl

import android.content.Context
import com.feimeng.kaomojiboard.loader.KaomojiLoader
import com.feimeng.kaomojiboard.model.Kaomoji

/**
 * Author: Feimeng
 * Time:   2020/3/27
 * Description: 默认的颜文字加载器
 */
class DefaultKaomojiLoader : KaomojiLoader {
    private val mData: ArrayList<Kaomoji> = ArrayList()

    init {
        for (poi in 1..16) {
            mData.add(Kaomoji.valueOf("(～￣▽￣)～"))
        }
        for (poi in 1..16) {
            mData.add(Kaomoji.valueOf("(￣３￣)a"))
        }
        for (poi in 1..10) {
            mData.add(Kaomoji.valueOf("ψ(｀∇´)ψ"))
        }
    }

    override fun load(context: Context, categoryName: String): List<Kaomoji> {
        return mData
    }
}