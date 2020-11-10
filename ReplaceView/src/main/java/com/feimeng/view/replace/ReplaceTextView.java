package com.feimeng.view.replace;

import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.InputType;
import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.widget.ScrollView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Author: Feimeng
 * Time:   2020/10/27
 * Description: 支持查找替换功能的TextView
 */
class ReplaceTextView extends androidx.appcompat.widget.AppCompatEditText {
    private int mColorSearch, mColorReplace, mColorIndicate; // 查找高亮色、替换高亮色、指示器高亮色
    private String mSearchText; // 查找的内容、替换的内容
    private final List<TextBlock> mSearchBlocks = new LinkedList<>(); // 查找的区块列表
    private final Stack<TextBlock> mReplaceBlocks = new Stack<>(); // 替换的区块列表
    private int mIndicationPosition; // 指示器的位置
    private TextBlock mIndicateBlock; // 指示器的高亮块
    private OnReplaceListener mOnReplaceListener; // 替换事件监听器

    public ReplaceTextView(@NonNull Context context) {
        super(context);
        init();
    }

    private void init() {
        setEnabled(false);
        setBackground(null);
        setGravity(Gravity.LEFT);
        setTextSize(14);
        setTextColor(Color.BLACK);
        setInputType(getInputType() | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        mColorSearch = Color.parseColor("#FFF6BB");
        mColorReplace = Color.parseColor("#2DBCFF");
        mColorIndicate = Color.parseColor("#FFDD00");
    }

    public void setOnReplaceListener(OnReplaceListener onReplaceListener) {
        mOnReplaceListener = onReplaceListener;
        checkMove();
        checkAction();
    }

    public void setContent(String content) {
        setText(content);
    }

    public String getContent() {
        return getText().toString();
    }

    public void setContentSize(float size) {
        setTextSize(size);
    }

    public void setContentColor(@ColorInt int color) {
        setTextColor(color);
    }

    /**
     * 设置高亮颜色
     *
     * @param searchColor     查找颜色
     * @param replaceColor    替换颜色
     * @param indicationColor 指示器颜色
     */
    public void setHighlightColor(int searchColor, int replaceColor, int indicationColor) {
        mColorSearch = searchColor;
        mColorReplace = replaceColor;
        mColorIndicate = indicationColor;
    }

    /**
     * 查找
     *
     * @param searchText 查找文本
     * @return 文本块
     */
    public List<TextBlock> find(String searchText, String content) {
        List<TextBlock> list = new ArrayList<>();
        Matcher matcher = getMatcher(searchText, content);
        while (matcher.find()) {
            int start = matcher.start();
            int end = matcher.end();
            list.add(new SearchTextBlock(mColorSearch, start, end));
        }
        return list;
    }

    /**
     * 通过查找的文本块进行搜索
     *
     * @param searchText 搜索文本
     * @param textBlocks 文本块
     */
    public void search(String searchText, List<TextBlock> textBlocks) {
        mSearchText = searchText;
        // 清空记录
        mSearchBlocks.clear();
        mReplaceBlocks.clear();
        mSearchBlocks.addAll(textBlocks);
        highlight(getEditable());
        if (!mSearchBlocks.isEmpty()) {
            mIndicationPosition = 0; // 初始化指示器
            scrollToIndicate();
        }
        checkMove();
        checkAction();
    }

    /**
     * 查找文本
     *
     * @param searchText 搜索文本
     */
    public void search(String searchText) {
        mSearchText = searchText;
        // 清空记录
        mSearchBlocks.clear();
        mReplaceBlocks.clear();
        // 查找内容
        if (!mSearchText.isEmpty()) {
            Matcher matcher = getMatcher(mSearchText, getText());
            while (matcher.find()) {
                int start = matcher.start();
                int end = matcher.end();
                mSearchBlocks.add(new SearchTextBlock(mColorSearch, start, end));
            }
        }
        highlight(getEditable());
        if (!mSearchBlocks.isEmpty()) {
            mIndicationPosition = 0; // 初始化指示器
            scrollToIndicate();
        }
        checkMove();
        checkAction();
    }

    private Matcher getMatcher(String searchText, CharSequence content) {
        String[] arr = {"\\", "$", "(", ")", "*", "+", ".", "[", "]", "?", "^", "{", "}", "|"};
        for (String key : arr) {
            if (searchText.contains(key)) {
                searchText = searchText.replace(key, "\\" + key);
            }
        }
        return Pattern.compile(searchText).matcher(content);
    }

    /**
     * 获取搜索文本块
     *
     * @return 搜索文本块
     */
    public List<TextBlock> getSearchBlock() {
        return new ArrayList<>(mSearchBlocks);
    }

    /**
     * 往前移动指示器
     */
    public void prev() {
        if (mIndicationPosition > 0) {
            mIndicationPosition--;
            highlightIndicate(getEditable());
            checkMove();
            scrollToIndicate();
        }
    }

    /**
     * 往后移动指示器
     */
    public void next() {
        if (mIndicationPosition < mSearchBlocks.size() - 1) {
            mIndicationPosition++;
            highlightIndicate(getEditable());
            checkMove();
            scrollToIndicate();
        }
    }

    /**
     * 替换指示器处的内容
     *
     * @param replaceText 替换文本
     */
    public void replace(String replaceText) {
        if (mSearchBlocks.isEmpty()) return;
        int diffLength = replaceText.length() - mSearchText.length();

        // 待替换块
        TextBlock replaceBlock = mSearchBlocks.remove(mIndicationPosition);
        removeSpan(replaceBlock, getEditable());
        // 移动此替换块位置之后的文字块
        List<TextBlock> createList = new LinkedList<>();
        for (int i = mIndicationPosition; i < mSearchBlocks.size(); i++) {
            TextBlock textBlock = mSearchBlocks.get(i);
            removeSpan(textBlock, getEditable());
            textBlock.setStart(textBlock.getStart() + diffLength);
            textBlock.setEnd(textBlock.getEnd() + diffLength);
            createList.add(textBlock);
        }
        for (TextBlock textBlock : mReplaceBlocks) {
            if (textBlock.getStart() > replaceBlock.getStart()) {
                removeSpan(textBlock, getEditable());
                textBlock.setStart(textBlock.getStart() + diffLength);
                textBlock.setEnd(textBlock.getEnd() + diffLength);
                createList.add(textBlock);
            }
        }
        // 替换内容
        getEditableText().replace(replaceBlock.getStart(), replaceBlock.getEnd(), replaceText);
        // 添加替换块
        replaceBlock.setEnd(replaceBlock.getEnd() + diffLength);
        replaceBlock = new ReplaceTextBlock(mColorReplace, replaceBlock, replaceText);
        mReplaceBlocks.add(0, replaceBlock);
        createList.add(replaceBlock);
        // 创建文字块
        for (TextBlock textBlock : createList) {
            createSpan(textBlock, getEditable());
        }
        // 判断是否最后一个
        if (mIndicationPosition == mSearchBlocks.size())
            mIndicationPosition = mSearchBlocks.size() - 1;
        highlightIndicate(getEditable());
        checkMove();
        checkAction();
        if (!mSearchBlocks.isEmpty()) scrollToIndicate();
        if (mOnReplaceListener != null) mOnReplaceListener.onContentChange(getContent());
    }

    /**
     * 替换所有查找的内容
     *
     * @param replaceText 替换文本
     */
    public void replaceAll(String replaceText) {
        if (mSearchBlocks.isEmpty()) return;
        new Thread() {
            @Override
            public void run() {
                SpannableStringBuilder span = new SpannableStringBuilder(getText().toString());
                try {
                    int diffLength = replaceText.length() - mSearchText.length();
                    Iterator<TextBlock> iterator = mSearchBlocks.iterator();
                    while (iterator.hasNext()) {
                        TextBlock textBlock = iterator.next();
                        iterator.remove();
                        // 替换内容
                        span.replace(textBlock.getStart(), textBlock.getEnd(), replaceText);
                        // 移动后面的搜索块
                        for (TextBlock searchBlock : mSearchBlocks) {
                            if (searchBlock.getStart() > textBlock.getStart()) {
                                searchBlock.setStart(searchBlock.getStart() + diffLength);
                                searchBlock.setEnd(searchBlock.getEnd() + diffLength);
                            }
                        }
                        // 移动后台的替换块
                        for (TextBlock replaceBlock : mReplaceBlocks) {
                            if (replaceBlock.getStart() > textBlock.getStart()) {
                                replaceBlock.setStart(replaceBlock.getStart() + diffLength);
                                replaceBlock.setEnd(replaceBlock.getEnd() + diffLength);
                            }
                        }
                        textBlock.setEnd(textBlock.getEnd() + diffLength);
                        textBlock = new ReplaceTextBlock(mColorReplace, textBlock, replaceText);
                        mReplaceBlocks.add(textBlock);
                    }
                } catch (Exception ignored) {
                } finally {
                    mIndicationPosition = mSearchBlocks.isEmpty() ? -1 : 0;
                }
                highlight(span);
                post(() -> {
                    setText(span);
                    checkMove();
                    checkAction();
                    if (mOnReplaceListener != null)
                        mOnReplaceListener.onContentChange(getContent());
                });
            }
        }.start();
    }

    public void repeal() {
        if (mReplaceBlocks.isEmpty()) return;
        // 待搜索块
        ReplaceTextBlock searchBlock = (ReplaceTextBlock) mReplaceBlocks.remove(0);
        int diffLength = mSearchText.length() - searchBlock.getReplace().length();
        removeSpan(searchBlock, getEditable());
        // 移动此搜索块位置之后的文字块
        List<TextBlock> createList = new LinkedList<>();
        for (TextBlock textBlock : mReplaceBlocks) {
            if (textBlock.getStart() > searchBlock.getStart()) {
                removeSpan(textBlock, getEditable());
                textBlock.setStart(textBlock.getStart() + diffLength);
                textBlock.setEnd(textBlock.getEnd() + diffLength);
                createList.add(textBlock);
            }
        }
        mIndicationPosition = 0;
        for (int i = 0; i < mSearchBlocks.size(); i++) {
            TextBlock textBlock = mSearchBlocks.get(i);
            if (searchBlock.getStart() > textBlock.getStart()) {
                mIndicationPosition = i + 1;
            } else if (textBlock.getStart() > searchBlock.getStart()) {
                removeSpan(textBlock, getEditable());
                textBlock.setStart(textBlock.getStart() + diffLength);
                textBlock.setEnd(textBlock.getEnd() + diffLength);
                createList.add(textBlock);
            }
        }
        // 替换内容
        getEditableText().replace(searchBlock.getStart(), searchBlock.getEnd(), mSearchText);
        // 添加搜索块
        searchBlock.setEnd(searchBlock.getEnd() + diffLength);
        TextBlock searchReplace = new SearchTextBlock(searchBlock, mColorSearch);
        mSearchBlocks.add(mIndicationPosition, searchReplace);
        createList.add(searchReplace);
        // 创建文字块
        for (TextBlock textBlock : createList) {
            createSpan(textBlock, getEditable());
        }
        highlightIndicate(getEditable());
        checkMove();
        checkAction();
        scrollToIndicate();
        if (mOnReplaceListener != null) mOnReplaceListener.onContentChange(getContent());
    }

    private void scrollToIndicate() {
        ScrollView scrollView = (ScrollView) getParent();
        scrollView.smoothScrollTo(0, Math.max(0, getCurrentCursorLineVertical() - getResources().getDimensionPixelOffset(R.dimen.scroll_distance)));
    }

    private TextBlock getCurrent() {
        if (mIndicationPosition < 0 || mIndicationPosition >= mSearchBlocks.size()) return null;
        return mSearchBlocks.get(mIndicationPosition);
    }

    private void checkMove() {
        boolean mPrevious = false;
        boolean mNext = false;
        if (mSearchBlocks.size() < 2) {
            mPrevious = false;
            mNext = false;
        } else {
            if (mIndicationPosition == 0) {
                mPrevious = false;
                if (mSearchBlocks.size() >= 1) mNext = true;
            } else if (mIndicationPosition == mSearchBlocks.size() - 1) {
                mNext = false;
                if (mSearchBlocks.size() >= 1) mPrevious = true;
            } else {
                mPrevious = true;
                mNext = true;
            }
        }
        if (mOnReplaceListener != null) mOnReplaceListener.onMoveStatus(mPrevious, mNext);
    }

    private void checkAction() {
        boolean repeal = mReplaceBlocks.size() > 0;
        boolean replace = mSearchBlocks.size() > 0;
        if (mOnReplaceListener != null) mOnReplaceListener.onActionStatus(repeal, replace);
    }

    private void clearSpans(Spannable span) {
        for (BackgroundColorSpan s : span.getSpans(0, span.length(), BackgroundColorSpan.class)) {
            span.removeSpan(s);
        }
        for (ForegroundColorSpan s : span.getSpans(0, span.length(), ForegroundColorSpan.class)) {
            span.removeSpan(s);
        }
    }

    private void highlight(Spannable span) {
        // 清理高亮
        clearSpans(span);
        // 高亮搜索块
        for (TextBlock textBlock : mSearchBlocks) {
            createSpan(textBlock, span);
        }
        // 高亮替换块
        for (TextBlock textBlock : mReplaceBlocks) {
            createSpan(textBlock, span);
        }
        // 高亮指示块
        highlightIndicate(span);
    }

    private void highlightIndicate(Spannable span) {
        if (mIndicateBlock != null) {
            removeSpan(mIndicateBlock, span);
            mIndicateBlock = null;
        }
        TextBlock current = getCurrent();
        if (current != null) {
            mIndicateBlock = new SearchTextBlock(current, mColorIndicate);
            createSpan(mIndicateBlock, span);
            setSelection(mIndicateBlock.getStart());
        }
    }

    private void removeSpan(TextBlock textBlock, Spannable span) {
        span.removeSpan(textBlock);
    }

    private void createSpan(TextBlock textBlock, Spannable span) {
        span.setSpan(textBlock, textBlock.getStart(), textBlock.getEnd(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    public Editable getEditable() {
        return getEditableText();
    }

    private int getCurrentCursorLineVertical() {
        return getLayout().getLineTop(getCurrentCursorLine() - 1);
    }

    private int getCurrentCursorLine() {
        int selectionStart = Selection.getSelectionStart(getText());
        Layout layout = getLayout();
        if (selectionStart != -1) {
            return layout.getLineForOffset(selectionStart) + 1;
        }
        return -1;
    }

    private static class SearchTextBlock extends BackgroundColorSpan implements TextBlock {
        private int start, end;

        SearchTextBlock(int color, int start, int end) {
            super(color);
            this.start = start;
            this.end = end;
        }

        SearchTextBlock(TextBlock textBlock, int color) {
            super(color);
            this.start = textBlock.getStart();
            this.end = textBlock.getEnd();
        }

        @Override
        public int getStart() {
            return start;
        }

        @Override
        public int getEnd() {
            return end;
        }

        @Override
        public void setStart(int start) {
            this.start = start;
        }

        @Override
        public void setEnd(int end) {
            this.end = end;
        }
    }

    private static class ReplaceTextBlock extends ForegroundColorSpan implements TextBlock {
        private int start, end;
        private final String replace;

        ReplaceTextBlock(int color, TextBlock textBlock, String replace) {
            super(color);
            this.start = textBlock.getStart();
            this.end = textBlock.getEnd();
            this.replace = replace;
        }

        @Override
        public int getStart() {
            return start;
        }

        @Override
        public int getEnd() {
            return end;
        }

        @Override
        public void setStart(int start) {
            this.start = start;
        }

        @Override
        public void setEnd(int end) {
            this.end = end;
        }

        public String getReplace() {
            return replace;
        }
    }
}
