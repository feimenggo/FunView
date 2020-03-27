package com.feimeng.kaomojiboard.model

/**
 * Author: Feimeng
 * Time:   2020/3/27
 * Description: 一页显示的颜文字
 */
class KaomojiPage(private val data: ArrayList<Kaomoji>) {
    fun get(position: Int): Kaomoji {
        return data[position]
    }
}