package com.feimeng.sensitiveword;

/**
 * Author: Feimeng
 * Time:   2020/11/3
 * Description:
 */
public class WordLocation {
    public String word;
    public int start;
    public int end;

    WordLocation(String word, int start, int end) {
        this.word = word;
        this.start = start;
        this.end = end;
    }
}
