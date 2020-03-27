package com.feimeng.kaomojiboard

import androidx.fragment.app.Fragment
import com.feimeng.kaomojiboard.loader.KaomojiLoader
import com.feimeng.kaomojiboard.ui.KaomojiFragment

/**
 * Author: Feimeng
 * Time:   2020/3/27
 * Description: 颜文字键盘
 */
class KaomojiBoard private constructor(val loader: KaomojiLoader) {

    companion object {
        var instance: KaomojiBoard? = null

        fun fragment(loader: KaomojiLoader): Fragment {
            instance = KaomojiBoard(loader)
            return KaomojiFragment.newInstance()
        }
    }
}