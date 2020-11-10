package com.feimeng.sensitiveword;

import com.feimeng.sensitiveword.util.PinyinUtils;
import com.feimeng.sensitiveword.util.StringUtils;
import com.feimeng.sensitiveword.util.Strings;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * DFA 脱敏算法实现支持类
 *
 * @author hoojo
 * @version 1.0
 */
public class SensitiveWordFilter {
    protected final HashMap<Character, DfaNode> cacheNodes = new HashMap<>();
    protected boolean partMatch; // 是否支持匹配词语的一部分

    public void setPartMatch(boolean partMatch) {
        this.partMatch = partMatch;
    }

    public void put(Collection<String> words) {
        for (String word : words) {
            put(word);
        }
    }

    public void put(String word) {
        if (StringUtils.isBlank(word)) {
            throw new IllegalArgumentException("关键词不能为空");
        }

        word = StringUtils.trim(word);
        if (word.length() < 2) {
            throw new IllegalArgumentException("关键词至少2个字");
        }

        Character fisrtChar = word.charAt(0);
        DfaNode node = cacheNodes.get(fisrtChar);
        if (node == null) {
            node = new DfaNode(fisrtChar);
            cacheNodes.put(fisrtChar, node);
        }

        for (int i = 1; i < word.length(); i++) {
            char nextChar = word.charAt(i);

            DfaNode nextNode = null;
            if (!node.isLeaf()) {
                nextNode = node.getChilds().get(nextChar);
            }
            if (nextNode == null) {
                nextNode = new DfaNode(nextChar);
            }

            node.addChild(nextNode);
            node = nextNode;

            if (i == word.length() - 1) {
                node.setWord(true);
            }
        }
    }

    /**
     * 判断一段文字包含敏感词语，支持敏感词结果回调
     *
     * @param content  被匹配内容
     * @param callback 回调接口
     * @return 是否匹配到的词语
     */
    protected boolean processor(String content, Callback callback) {
        if (StringUtils.isBlank(content)) {
            return false;
        }

        if (content.length() < 2) {
            return false;
        }

        for (int index = 0; index < content.length(); index++) {
            char fisrtChar = content.charAt(index);

            DfaNode node = cacheNodes.get(fisrtChar);
            if (node == null || node.isLeaf()) {
                continue;
            }

            int charCount = 1;
            for (int i = index + 1; i < content.length(); i++) {
                char wordChar = content.charAt(i);

                node = node.getChilds().get(wordChar);
                if (node != null) {
                    charCount++;
                } else {
                    break;
                }

                if (partMatch && node.isWord()) {
                    String word = StringUtils.substring(content, index, index + charCount);
                    if (callback.call(word, index, index + charCount)) {
                        return true;
                    }
                    break;
                } else if (node.isWord()) {
                    String word = StringUtils.substring(content, index, index + charCount);
                    if (callback.call(word, index, index + charCount)) {
                        return true;
                    }
                }

                if (node.isLeaf()) {
                    break;
                }
            }

            if (partMatch) {
                index += charCount;
            }
        }

        return false;
    }


    private static final String HTML_HIGHLIGHT = "<font color='red'>%s</font>";

    /**
     * 匹配到敏感词的回调接口
     */
    protected interface Callback {

        /**
         * 匹配掉敏感词回调
         *
         * @param word 敏感词
         * @return true 立即停止后续任务并返回，false 继续执行
         */
        boolean call(String word, int start, int end);
    }

    /**
     * 是否包含敏感字符
     *
     * @param content 被匹配内容
     * @return 是否包含敏感字符
     */
    public boolean contains(String content) {
        return processor(content, (word, start, end) -> {
            return true; // 有敏感词立即返回
        });
    }

    /**
     * 返回匹配到的敏感词语
     *
     * @param content 被匹配的语句
     * @return 返回匹配的敏感词语集合
     */
    public Set<String> getWords(String content) {
        final Set<String> words = new HashSet<>();
        processor(content, (word, start, end) -> {
            words.add(word);
            return false; // 继续匹配后面的敏感词
        });
        return words;
    }

    /**
     * 返回匹配到的敏感词语位置
     *
     * @param content 被匹配的语句
     * @return 返回匹配的敏感词语集合
     */
    public List<WordLocation> getWordLocations(String content) {
        final List<WordLocation> words = new ArrayList<>();
        getWordLocations(content, words);
        return words;
    }

    /**
     * 返回匹配到的敏感词语位置
     *
     * @param content 被匹配的语句
     * @param words   返回匹配的敏感词语集合
     */
    public void getWordLocations(String content, List<WordLocation> words) {
        processor(content, (word, start, end) -> {
            words.add(new WordLocation(word, start, end));
            return false; // 继续匹配后面的敏感词
        });
    }

    /**
     * html高亮敏感词
     *
     * @param content 被匹配的语句
     * @return 返回html高亮敏感词
     */
    public static String highlight(String content, List<WordLocation> wordLocations) {
        return highlight(content, transformWordLocation(wordLocations));
    }

    public static String highlight(String content, Set<String> words) {
        for (String word : words) {
            content = content.replaceAll(word, String.format(HTML_HIGHLIGHT, word));
        }
        return content;
    }

    /**
     * 过滤敏感词，并把敏感词替换为指定字符
     *
     * @param content     被匹配的语句
     * @param replaceText 替换字符
     * @return 过滤后的字符串
     */
    public static String filterByReplace(String content, List<WordLocation> wordLocations, String replaceText) {
        return filterByReplace(content, transformWordLocation(wordLocations), replaceText);
    }

    public static String filterByReplace(String content, Set<String> words, String replaceText) {
        for (String word : words) {
            content = content.replaceAll(word, Strings.repeat(replaceText, word.length()));
        }
        return content;
    }

    /**
     * 过滤敏感词，并把敏感词用指定字符间隔
     *
     * @param content    被匹配的语句
     * @param divideText 间隔字符
     * @return 过滤后的字符串
     */
    public static String filterByInterval(String content, List<WordLocation> wordLocations, String divideText) {
        return filterByInterval(content, transformWordLocation(wordLocations), divideText);
    }

    public static String filterByInterval(String content, Set<String> words, String divideText) {
        for (String word : words) {
            String wordDivide = word.replace("", divideText);
            String wordInterval = wordDivide.substring(1, wordDivide.length() - 1);
            content = content.replaceAll(word, wordInterval);
        }
        return content;
    }

    public static String filterByPingYin(String content, List<WordLocation> wordLocations) {
        return filterByPingYin(content, transformWordLocation(wordLocations));
    }

    public static String filterByPingYin(String content, Set<String> words) {
        for (String word : words) {
            String wordDivide = word.replace("", " ");
            String wordInterval = wordDivide.substring(1, wordDivide.length() - 1);
            String pingYin = PinyinUtils.getPingYin(wordInterval);
            content = content.replaceAll(word, pingYin);
        }
        return content;
    }

    public static String filterByPingYinFirst(String content, List<WordLocation> wordLocations) {
        return filterByPingYinFirst(content, transformWordLocation(wordLocations));
    }

    public static String filterByPingYinFirst(String content, Set<String> words) {
        for (String word : words) {
            String pingYin = PinyinUtils.getPingYin(word.substring(0, 1));
            String wordSuffix = word.substring(1);
            content = content.replaceAll(word, pingYin + wordSuffix);
        }
        return content;
    }


    private static Set<String> transformWordLocation(List<WordLocation> wordLocations) {
        Set<String> words = new HashSet<>();
        for (WordLocation wordLocation : wordLocations) {
            words.add(wordLocation.word);
        }
        return words;
    }
}
