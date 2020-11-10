package com.feimeng.sensitiveword;

import com.feimeng.sensitiveword.util.PinyinUtils;

import org.junit.Test;

import java.util.Arrays;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        String oldValue = "处女";
        System.out.println(Arrays.toString(oldValue.split("")));
        System.out.println(String.join(" ", oldValue.split("")));
        System.out.println(PinyinUtils.getPingYin(oldValue));
        System.out.println(PinyinUtils.getFirstSpell(oldValue));
        System.out.println(PinyinUtils.converterToSpell(oldValue));
        System.out.println(PinyinUtils.converterToFirstSpell(oldValue));
//        assertEquals(4, 2 + 2);
    }

    @Test
    public void main() {
        SensitiveWordFilter dfa = new SensitiveWordFilter();
        dfa.put("中国人");
        dfa.put("中国男人");
        dfa.put("中国人民");
        dfa.put("人民");
        dfa.put("中间");
        dfa.put("女人");

        dfa.put("一举");
        dfa.put("一举成名");
        dfa.put("一举成名走四方");
        dfa.put("成名");
        dfa.put("走四方");

        String content = "我们中国人都是好人，在他们中间有男人和女人。中国男人很惨，中国人民长期被压迫。";
        System.out.println(dfa.contains(content));
        System.out.println(dfa.getWords(content));
        System.out.println(SensitiveWordFilter.filterByReplace(content, dfa.getWordLocations(content), "*"));
        System.out.println(SensitiveWordFilter.filterByInterval(content, dfa.getWordLocations(content), "-"));
        System.out.println(SensitiveWordFilter.filterByPingYin(content, dfa.getWordLocations(content)));
        System.out.println(SensitiveWordFilter.filterByPingYinFirst(content, dfa.getWordLocations(content)));
        System.out.println(SensitiveWordFilter.highlight(content, dfa.getWordLocations(content)));
        System.out.println(SensitiveWordFilter.highlight(content, dfa.getWordLocations(content)));

        content = "一举成名走四方的是什么";
        System.out.println(dfa.getWords(content));
        System.out.println(SensitiveWordFilter.filterByReplace(content, dfa.getWordLocations(content), "*"));
        System.out.println(SensitiveWordFilter.highlight(content, dfa.getWordLocations(content)));
        System.out.println(SensitiveWordFilter.highlight(content, dfa.getWordLocations(content)));
    }
}