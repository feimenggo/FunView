package com.feimeng.imagepicker.ui

import android.animation.Animator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import com.feimeng.imagepicker.ImagePicker
import com.feimeng.imagepicker.R
import com.feimeng.imagepicker.adapter.PreviewPagerAdapter
import com.feimeng.imagepicker.base.BaseImagePickerActivity
import com.feimeng.imagepicker.entity.AlbumMedia
import com.feimeng.imagepicker.entity.SelectionSpec

/**
 * Author: Feimeng
 * Time:   2020/3/24
 * Description: 图片选择预览
 */
class ImagePickerPreviewActivity : BaseImagePickerActivity(), ViewPager.OnPageChangeListener, View.OnClickListener {
    private lateinit var mVP: ViewPager
    private lateinit var mAdapter: PreviewPagerAdapter

    private lateinit var mSelects: List<AlbumMedia>
    private lateinit var mCurrentSelects: ArrayList<AlbumMedia>

    private lateinit var mCheckView: ImageView
    private lateinit var mSelectView: TextView // 选择数量大于1有效
    private lateinit var mConfirmView: TextView

    private lateinit var mShowGroupScroll: HorizontalScrollView
    private lateinit var mShowGroup: LinearLayout
    private var mLastShowView: View? = null
    private var mAnimation: ValueAnimator? = null

    private var mIsMulti: Boolean = false

    companion object {
        fun start(context: Activity, requestCode: Int, albumMedias: ArrayList<AlbumMedia>) {
            context.startActivityForResult(Intent(context, ImagePickerPreviewActivity::class.java).putParcelableArrayListExtra(ImagePicker.PICKED_MEDIA, albumMedias), requestCode)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.setBackgroundColor(Color.parseColor("#2A2A2A"))
        mIsMulti = SelectionSpec.instance.maxSelectable > 1
        setContentView(if (mIsMulti) R.layout.ip_activity_image_picker_preview else R.layout.ip_activity_image_picker_preview_single)
        initView()
    }

    private fun initView() {
        mSelects = intent.getParcelableArrayListExtra(ImagePicker.PICKED_MEDIA)!!
        mCurrentSelects = ArrayList(mSelects)

        findViewById<View>(R.id.back).setOnClickListener(this)
        mCheckView = findViewById(R.id.check)
        mCheckView.setOnClickListener(this)
        mConfirmView = findViewById(R.id.confirm)
        mConfirmView.setOnClickListener(this)

        mAdapter = PreviewPagerAdapter(mSelects)
        mVP = findViewById(R.id.vp)
        mVP.adapter = mAdapter
        mVP.addOnPageChangeListener(this)

        if (mIsMulti) {
            mSelectView = findViewById(R.id.select)

            val size = resources.getDimensionPixelSize(R.dimen.media_preview_size)
            val spacing = resources.getDimensionPixelSize(R.dimen.media_preview_spacing)
            mShowGroupScroll = findViewById(R.id.scroll)
            mShowGroup = mShowGroupScroll.getChildAt(0) as LinearLayout
            val bg = ContextCompat.getColor(this@ImagePickerPreviewActivity, R.color.checked)
            for ((index, media) in mSelects.withIndex()) {
                val iv = ImageView(this)
                SelectionSpec.instance.imageEngine.loadThumbnail(this, 0, ColorDrawable(Color.GRAY), iv, media.contentUri)
                iv.scaleType = ImageView.ScaleType.CENTER_CROP
                iv.cropToPadding = true
                iv.setBackgroundColor(bg)
                iv.setOnClickListener(this)
                iv.tag = index
                val layout = ViewGroup.MarginLayoutParams(size, size)
                layout.rightMargin = spacing
                mShowGroup.addView(iv, layout)
            }
            showMedia(0)
        } else {
            mCheckView.isSelected = mCurrentSelects.contains(mSelects[mVP.currentItem])
            updateCheck()
        }
        updateConfirm()
    }

    override fun onPageScrollStateChanged(state: Int) {
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
    }

    override fun onPageSelected(position: Int) {
        if (mIsMulti) showMedia(position)
    }

    private fun showMedia(position: Int) {
        val showView = mShowGroup.getChildAt(position)
        val padding = resources.getDimensionPixelSize(R.dimen.media_preview_padding)
        // 选中状态
        mCheckView.isSelected = mCurrentSelects.contains(mSelects[mVP.currentItem])
        updateCheck()
        // 动画
        val scrollLeft = mShowGroupScroll.scrollX
        val scrollRight = mShowGroupScroll.scrollX + mShowGroupScroll.width
        val viewLeft = showView.left
        val viewRight = showView.right
        if (viewLeft < scrollLeft) { // 目标相对容器左边不可见
            mShowGroupScroll.smoothScrollBy(-showView.width - (showView.layoutParams as ViewGroup.MarginLayoutParams).rightMargin, 0)
        } else if (viewRight > scrollRight) { // 目标相对容器右边不可见
            mShowGroupScroll.smoothScrollBy(showView.width + (showView.layoutParams as ViewGroup.MarginLayoutParams).rightMargin, 0)
        }

        mAnimation?.end()
        mAnimation = ValueAnimator.ofInt(0, padding)
        mAnimation!!.addUpdateListener { anim ->
            val value = anim.animatedValue as Int
            mLastShowView?.setPadding(padding - value, padding - value, padding - value, padding - value)
            showView.setPadding(value, value, value, value)
        }
        mAnimation!!.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator) {
                mLastShowView = showView
            }

            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        })
        mAnimation!!.start()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.back -> onBackPressed()
            R.id.check -> toggleCheck()
            R.id.confirm -> previewResult()
            else -> mVP.setCurrentItem(v.tag as Int, false)
        }
    }

    private fun toggleCheck() {
        if (mCheckView.isSelected) { // 取消选中
            mCheckView.isSelected = false
            mCurrentSelects.remove(mSelects[mVP.currentItem])
        } else { // 选中
            mCheckView.isSelected = true
            mCurrentSelects.add(mSelects[mVP.currentItem])
        }
        updateCheck()
        updateConfirm()
    }

    private fun updateCheck() {
        mCheckView.setImageDrawable(ColorDrawable(if (mCheckView.isSelected) Color.GREEN else Color.RED))
    }

    @SuppressLint("SetTextI18n")
    private fun updateConfirm() {
        if (mCurrentSelects.isEmpty()) {
            if (mIsMulti) mSelectView.text = "未选择图片"
            mConfirmView.isEnabled = false
        } else {
            if (mIsMulti) mSelectView.text = "已选${mCurrentSelects.size}张"
            mConfirmView.isEnabled = true
        }
    }

    private fun previewResult() {
        setResult(Activity.RESULT_OK, intent.putParcelableArrayListExtra(ImagePicker.PICKED_MEDIA, mCurrentSelects))
        onBackPressed()
    }
}
