package com.feimeng.keyboard.kaomojiboard.ui

import com.feimeng.keyboard.kaomojiboard.model.Kaomoji

/**
 * Author: Feimeng
 * Time:   2020/3/27
 * Description:
 */
interface KaomojiAction {
    fun clickKaomoji(kaomoji: Kaomoji, position: Int)
}