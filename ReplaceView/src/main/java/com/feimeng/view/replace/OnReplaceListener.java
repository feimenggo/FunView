package com.feimeng.view.replace;

/**
 * Author: Feimeng
 * Time:   2020/10/27
 * Description: 替换事件监听器
 */
public interface OnReplaceListener {
    void onMoveStatus(boolean prev, boolean next);

    void onActionStatus(boolean repeal, boolean replace);

    void onContentChange(String content);
}
