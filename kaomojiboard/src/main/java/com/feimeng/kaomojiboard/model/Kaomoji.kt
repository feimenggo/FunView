package com.feimeng.kaomojiboard.model

/**
 * Author: Feimeng
 * Time:   2020/3/27
 * Description:
 */
class Kaomoji(val body: String) {
    companion object {
        fun valueOf(value: String): Kaomoji {
           return Kaomoji(value)
        }
    }
}