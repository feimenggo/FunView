package com.feimeng.keyboard.kaomojiboard.model

/**
 * Author: Feimeng
 * Time:   2020/3/27
 * Description: 一页显示的颜文字
 */
class KaomojiPage(val kaomojiPackage: KaomojiPackage, val data: ArrayList<Kaomoji>, val packageIndex: Int, val pageIndex: Int, val pageCount: Int) {
    fun get(position: Int): Kaomoji? {
        return if (position < data.size) data[position] else null
    }
}