package com.feimeng.view.replace;

/**
 * Author: Feimeng
 * Time:   2020/10/27
 * Description:
 */
public interface TextBlock {
    /**
     * 区块起始坐标
     */
    int getStart();

    /**
     * 区块结束坐标
     */
    int getEnd();

    /**
     * 设置起始坐标
     *
     * @param start 起始坐标
     */
    void setStart(int start);

    /**
     * 设置结束坐标
     *
     * @param end 结束坐标
     */
    void setEnd(int end);
}
