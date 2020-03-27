package com.feimeng.imagepicker.ui

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.feimeng.imagepicker.ImagePicker.Companion.PICKED_MEDIA
import com.feimeng.imagepicker.R
import com.feimeng.imagepicker.adapter.AlbumAdapter
import com.feimeng.imagepicker.adapter.AlbumMediaAdapter
import com.feimeng.imagepicker.base.BaseImagePickerActivity
import com.feimeng.imagepicker.collection.AlbumCollection
import com.feimeng.imagepicker.collection.AlbumMediaCollection
import com.feimeng.imagepicker.collection.SelectedItemCollection
import com.feimeng.imagepicker.entity.Album
import com.feimeng.imagepicker.entity.AlbumMedia
import com.feimeng.imagepicker.entity.SelectionSpec
import com.feimeng.imagepicker.ui.widget.MediaGridInset
import com.feimeng.imagepicker.util.UIUtils
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * Author: Feimeng
 * Time:   2020/3/24
 * Description: 图片选择
 */
class ImagePickerActivity : BaseImagePickerActivity(), AlbumCollection.AlbumCallbacks, View.OnClickListener, AlbumMediaCollection.AlbumMediaCallbacks, ImagePickerAction {
    private val mAlbumCollection = AlbumCollection()
    private val mAlbumMediaCollection = AlbumMediaCollection()
    private lateinit var mSelectedCollection: SelectedItemCollection

    private lateinit var mAlbumTitleView: TextView
    private lateinit var mAlbumRV: RecyclerView
    private lateinit var mAlbumMediaRV: RecyclerView
    private lateinit var mShadow: View
    private var mAlbumAdapter: AlbumAdapter = AlbumAdapter(this)
    private var mAlbumMediaAdapter: AlbumMediaAdapter = AlbumMediaAdapter(this)

    private lateinit var mPreviewView: TextView
    private lateinit var mConfirmView: TextView

    private var mCameraPictureUrl: Uri? = null

    companion object {
        private const val REQ_PREVIEW_IMAGE = 100
        private const val REQ_CAPTURE_IMAGE = 200

        fun start(context: Activity, requestCode: Int) {
            context.startActivityForResult(Intent(context, ImagePickerActivity::class.java), requestCode)
        }

        fun start(context: Fragment, requestCode: Int) {
            context.startActivityForResult(Intent(context.context, ImagePickerActivity::class.java), requestCode)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        overridePendingTransition(R.anim.in_from_bottom, R.anim.none)
        super.onCreate(savedInstanceState)
        window.decorView.setBackgroundColor(Color.WHITE)
        setContentView(R.layout.ip_activity_image_picker)
        initView()
        initData(savedInstanceState)
    }

    private fun initView() {
        findViewById<View>(R.id.back).setOnClickListener(this)
        mPreviewView = findViewById(R.id.preview)
        mPreviewView.setOnClickListener(this)
        mConfirmView = findViewById(R.id.confirm)
        mConfirmView.setOnClickListener(this)
        // 相册列表
        mAlbumTitleView = findViewById(R.id.albumTitle)
        mAlbumTitleView.setOnClickListener(this)
        mShadow = findViewById(R.id.shadow)
        mShadow.setOnClickListener(this)
        mAlbumRV = findViewById(R.id.albumRV)
        mAlbumRV.setHasFixedSize(true)
        mAlbumRV.layoutManager = LinearLayoutManager(this)
        mAlbumRV.adapter = mAlbumAdapter
        // 图片列表
        mAlbumMediaRV = findViewById(R.id.imageRV)
        mAlbumMediaRV.setHasFixedSize(true)
        val spanCount: Int
        val selectionSpec = SelectionSpec.instance
        spanCount = if (selectionSpec.gridExpectedSize > 0) {
            UIUtils.spanCount(this, selectionSpec.gridExpectedSize)
        } else {
            selectionSpec.spanCount
        }
        mAlbumMediaRV.layoutManager = GridLayoutManager(this, spanCount)
        val spacing = resources.getDimensionPixelSize(R.dimen.media_grid_spacing)
        mAlbumMediaRV.addItemDecoration(MediaGridInset(spanCount, spacing, false))
        mAlbumMediaRV.adapter = mAlbumMediaAdapter
    }

    private fun initData(savedInstanceState: Bundle?) {
        mSelectedCollection = SelectedItemCollection(this)
        mSelectedCollection.onCreate(savedInstanceState)
        mAlbumCollection.onCreate(this, this)
        mAlbumMediaCollection.onCreate(this, this)
        mAlbumCollection.onRestoreInstanceState(savedInstanceState)
        mAlbumCollection.loadAlbums()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0, R.anim.out_to_bottom)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mAlbumCollection.onSaveInstanceState(outState)
        mSelectedCollection.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        super.onDestroy()
        mAlbumCollection.onDestroy()
        mAlbumMediaCollection.onDestroy()
    }

    override fun onAlbumLoad(albums: List<Album>) {
        mAlbumAdapter.setData(albums)
        mAlbumAdapter.setSelection(mAlbumCollection.currentSelection)
        val album = albums[mAlbumCollection.currentSelection]
        if (album.isAll) album.addCaptureCount()
        onAlbumSelected(album)
    }

    override fun onAlbumReset() {
        mAlbumAdapter.resetData()
    }

    override fun selectionCollection(): SelectedItemCollection {
        return mSelectedCollection
    }

    override fun onSelectAlbum(album: Album, position: Int) {
        hideAlbumList()
        mAlbumCollection.setStateCurrentSelection(position)
        mAlbumAdapter.setSelection(position)
        if (album.isAll) album.addCaptureCount()
        onAlbumSelected(album)
    }

    override fun onSelectionChange() {
        val selectedCount: Int = mSelectedCollection.count()
        mPreviewView.isEnabled = selectedCount > 0
        mConfirmView.isEnabled = selectedCount > 0
    }

    override fun onCaptureImage() {
        mCameraPictureUrl = createImageUri()
        val pictureChooseIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        pictureChooseIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCameraPictureUrl)
        startActivityForResult(pictureChooseIntent, REQ_CAPTURE_IMAGE)
    }

    private fun createImageUri(): Uri? {
        val contentResolver = contentResolver
        val cv = ContentValues()
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        cv.put(MediaStore.Images.Media.TITLE, timeStamp)
        return contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv)
    }

    private fun onAlbumSelected(album: Album) {
        mAlbumMediaCollection.load(album, SelectionSpec.instance.capture)
        mAlbumTitleView.text = album.getDisplayName(this)
        if (album.isAll && album.isEmpty) {
            Toast.makeText(this, "没有图片", Toast.LENGTH_SHORT).show()
        } else {
        }
    }

    override fun onAlbumMediaLoad(albumMedias: List<AlbumMedia>) {
        mAlbumMediaAdapter.setData(albumMedias)
    }

    override fun onAlbumMediaReset() {
        mAlbumMediaAdapter.resetData()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQ_PREVIEW_IMAGE -> {
                    val albumMedia: ArrayList<AlbumMedia> = data!!.getParcelableArrayListExtra(PICKED_MEDIA)
                    pickerResult(mSelectedCollection.asListOfUri(albumMedia))
                }
                REQ_CAPTURE_IMAGE -> {
                    if (mCameraPictureUrl != null) pickerResult(ArrayList<Uri>(1).apply { add(mCameraPictureUrl!!) })
                }
            }
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.back -> onBackPressed()
            R.id.albumTitle, R.id.shadow -> {
                if (mAlbumTitleView.isSelected) { // 隐藏相册列表
                    hideAlbumList()
                } else { // 显示相册列表
                    showAlbumList()
                }
            }
            R.id.preview -> startPreview()
            R.id.confirm -> pickerResult(mSelectedCollection.asListOfUri())
        }
    }

    private fun startPreview() {
        ImagePickerPreviewActivity.start(this, REQ_PREVIEW_IMAGE, mSelectedCollection.asList())
    }

    private fun showAlbumList() {
        mAlbumTitleView.isSelected = true
        mAlbumRV.visibility = View.VISIBLE
        mShadow.visibility = View.VISIBLE
    }

    private fun hideAlbumList() {
        mAlbumTitleView.isSelected = false
        mAlbumRV.visibility = View.GONE
        mShadow.visibility = View.GONE
    }

    private fun pickerResult(images: ArrayList<Uri>) {
        setResult(Activity.RESULT_OK, intent.putParcelableArrayListExtra(PICKED_MEDIA, images))
        onBackPressed()
    }
}
