package com.feimeng.keyboard.kaomojiboard.loader

import android.content.Context
import com.feimeng.keyboard.kaomojiboard.model.KaomojiPackage

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
     * @return 颜文字包
     */
    fun load(context: Context, categoryName: String): List<KaomojiPackage>
}