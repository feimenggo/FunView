package com.feimeng.keyboard.kaomojiboard.ui.widget

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class GridInset(private val mSpanCount: Int,
                private val mHorizontalSpacing: Int,
                private val mVerticalSpacing: Int,
                private val mIncludeEdge: Boolean)
    : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView,
                                state: RecyclerView.State) {
        val position = parent.getChildAdapterPosition(view) // item position
        val column = position % mSpanCount // item column

        if (mIncludeEdge) {
            // spacing - column * ((1f / spanCount) * spacing)
            outRect.left = mHorizontalSpacing - column * mHorizontalSpacing / mSpanCount
            // (column + 1) * ((1f / spanCount) * spacing)
            outRect.right = (column + 1) * mHorizontalSpacing / mSpanCount

            if (position < mSpanCount) { // top edge
                outRect.top = mVerticalSpacing
            }
            outRect.bottom = mVerticalSpacing // item bottom
        } else {
            // column * ((1f / spanCount) * spacing)
            outRect.left = column * mHorizontalSpacing / mSpanCount
            // spacing - (column + 1) * ((1f / spanCount) * spacing)
            outRect.right = mHorizontalSpacing - (column + 1) * mHorizontalSpacing / mSpanCount
            if (position >= mSpanCount) {
                outRect.top = mVerticalSpacing // item top
            }
        }
    }
}
