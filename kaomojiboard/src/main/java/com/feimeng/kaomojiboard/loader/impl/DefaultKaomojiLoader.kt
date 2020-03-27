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
        mData.add(Kaomoji.valueOf("(～￣▽￣)～"))
        mData.add(Kaomoji.valueOf("(￣３￣)a"))
        mData.add(Kaomoji.valueOf("╰(￣▽￣)╭"))
        mData.add(Kaomoji.valueOf("(*￣∇￣*)"))
        mData.add(Kaomoji.valueOf("(￣▽￣)”"))
        mData.add(Kaomoji.valueOf("(～￣▽￣)～"))
        mData.add(Kaomoji.valueOf("(￣３￣)a"))
        mData.add(Kaomoji.valueOf("╰(￣▽￣)╭"))
        mData.add(Kaomoji.valueOf("(*￣∇￣*)"))
        mData.add(Kaomoji.valueOf("(￣▽￣)”"))
        mData.add(Kaomoji.valueOf("(～￣▽￣)～"))
        mData.add(Kaomoji.valueOf("(￣３￣)a"))
        mData.add(Kaomoji.valueOf("╰(￣▽￣)╭"))
        mData.add(Kaomoji.valueOf("(*￣∇￣*)"))
        mData.add(Kaomoji.valueOf("(￣▽￣)”"))
        mData.add(Kaomoji.valueOf("(～￣▽￣)～"))
        mData.add(Kaomoji.valueOf("(￣３￣)a"))
        mData.add(Kaomoji.valueOf("╰(￣▽￣)╭"))
        mData.add(Kaomoji.valueOf("(*￣∇￣*)"))
        mData.add(Kaomoji.valueOf("(￣▽￣)”"))
        mData.add(Kaomoji.valueOf("(～￣▽￣)～"))
        mData.add(Kaomoji.valueOf("(￣３￣)a"))
        mData.add(Kaomoji.valueOf("╰(￣▽￣)╭"))
        mData.add(Kaomoji.valueOf("(*￣∇￣*)"))
        mData.add(Kaomoji.valueOf("(￣▽￣)”"))
    }

    override fun load(context: Context, categoryName: String): List<Kaomoji> {
        return mData
    }
}