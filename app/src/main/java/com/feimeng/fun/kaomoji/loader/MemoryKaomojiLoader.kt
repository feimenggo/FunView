package com.feimeng.`fun`.kaomoji.loader

import android.content.Context
import com.feimeng.keyboard.kaomojiboard.loader.KaomojiLoader
import com.feimeng.keyboard.kaomojiboard.model.Kaomoji
import com.feimeng.keyboard.kaomojiboard.model.KaomojiPackage
import java.util.*

/**
 * Author: Feimeng
 * Time:   2020/3/27
 * Description: 默认的颜文字加载器
 */
class MemoryKaomojiLoader : KaomojiLoader {
    private val mData: ArrayList<Kaomoji> = ArrayList()
    private val mData2: ArrayList<Kaomoji> = ArrayList()
    private val mData3: ArrayList<Kaomoji> = ArrayList()

    init {
        for (poi in 1..16) {
            mData.add(Kaomoji.valueOf("(～￣▽￣)～$poi"))
        }
        for (poi in 1..16) {
            mData.add(Kaomoji.valueOf("(￣３￣)a$poi"))
        }
        for (poi in 1..10) {
            mData.add(Kaomoji.valueOf("ψ(｀∇´)ψ$poi"))
        }


        for (poi in 1..8) {
            mData2.add(Kaomoji.valueOf("cute $poi"))
        }

        for (poi in 1..16) {
            mData3.add(Kaomoji.valueOf("live $poi"))
        }
        for (poi in 1..10) {
            mData3.add(Kaomoji.valueOf("live3 $poi"))
        }
    }

    override fun load(context: Context, categoryName: String): List<KaomojiPackage> {
        val list = ArrayList<KaomojiPackage>()
        list.add(KaomojiPackage.create("默认", "╰(￣▽￣)╭", mData))
        list.add(KaomojiPackage.create("可爱", "⊙﹏⊙|||", mData2))
        list.add(KaomojiPackage.create("生活", "(*￣∇￣*)", mData3))
        return list
    }
}