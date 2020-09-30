package com.feimeng.keyboard.kaomojiboard

import com.feimeng.keyboard.kaomojiboard.model.Kaomoji

/**
 * Author: Feimeng
 * Time:   2020/3/31
 * Description:
 */
interface OnKaomojiClickEvent {
    fun onKaomojiClick(kaomoji: Kaomoji)
}