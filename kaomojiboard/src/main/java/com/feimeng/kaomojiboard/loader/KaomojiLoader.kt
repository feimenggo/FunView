package com.feimeng.kaomojiboard.loader

import android.content.Context
import com.feimeng.kaomojiboard.model.Kaomoji

/**
 * Author: Feimeng
 * Time:   2020/3/27
 * Description: 颜文字加载器
 */
interface KaomojiLoader {

    /**
     * 加载颜文字
     * @param context 上下文
     * @param categoryName 分类名称
     * @return 颜文字列表
     */
    fun load(context: Context, categoryName: String): List<Kaomoji>
}