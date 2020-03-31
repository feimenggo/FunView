package com.feimeng.keyboard.kaomojiboard.model

/**
 * Author: Feimeng
 * Time:   2020/3/27
 * Description: 颜文字包
 */
class KaomojiPackage private constructor(val name: String, val logo: String, val list: List<Kaomoji>) {
    val size = list.size

    companion object {
        fun create(name: String, logo: String, list: List<Kaomoji>): KaomojiPackage {
            return KaomojiPackage(name, logo, list)
        }
    }
}