package com.feimeng.imagepicker.ui

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.feimeng.imagepicker.ImagePicker.Companion.PICKED_MEDIA
import com.feimeng.imagepicker.ImagePicker.Companion.SELECT_ORIGINAL
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
import java.io.File
import java.util.*


/**
 * Author: Feimeng
 * Time:   2020/3/24
 * Description: 图片选择
 */
class ImagePickerActivity : BaseImagePickerActivity(), AlbumCollection.AlbumCallbacks, View.OnClickListener, AlbumMediaCollection.AlbumMediaCallbacks, ImagePickerAction {
    private lateinit var mAlbumCollection: AlbumCollection
    private lateinit var mAlbumMediaCollection: AlbumMediaCollection
    private lateinit var mSelectedCollection: SelectedItemCollection

    private lateinit var mAlbumTitleView: TextView
    private lateinit var mAlbumRV: RecyclerView
    private lateinit var mAlbumMediaRV: RecyclerView
    private lateinit var mShadow: View
    private var mAlbumAdapter: AlbumAdapter = AlbumAdapter(this)
    private var mAlbumMediaAdapter: AlbumMediaAdapter = AlbumMediaAdapter(this)

    private lateinit var mPreviewView: TextView // 预览按钮
    private lateinit var mOriginalView: TextView // 原图开关
    private lateinit var mConfirmView: TextView // 确定按钮

    /**
     * 拍照结果
     */
    private var mImageCaptureUri: Uri? = null
    private var mImageCaptureFile: File? = null

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
        if (SelectionSpec.hasInstance()) {
            overridePendingTransition(R.anim.in_from_bottom, R.anim.none)
            super.onCreate(savedInstanceState)
            window.decorView.setBackgroundColor(Color.WHITE)
            setContentView(R.layout.ip_activity_image_picker)
            initView()
            initData(savedInstanceState)
        } else {
            super.onCreate(savedInstanceState)
            finish()
        }
    }

    protected fun initView() {
        findViewById<View>(R.id.back).setOnClickListener(this)
        mPreviewView = findViewById(R.id.preview)
        mPreviewView.setOnClickListener(this)
        mOriginalView = findViewById(R.id.original)
        mOriginalView.setOnClickListener(this)
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
        if (SelectionSpec.instance.original != -1) {
            mOriginalView.visibility = View.VISIBLE
            updateOriginal()
        }
    }

    protected fun initData(savedInstanceState: Bundle?) {
        mSelectedCollection = SelectedItemCollection(this)
        mSelectedCollection.onCreate(savedInstanceState)
        mAlbumCollection = AlbumCollection()
        mAlbumCollection.onCreate(this, this)
        mAlbumMediaCollection = AlbumMediaCollection()
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
        if (::mAlbumCollection.isInitialized) mAlbumCollection.onDestroy()
        if (::mAlbumMediaCollection.isInitialized) mAlbumMediaCollection.onDestroy()
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
        createImageUri()
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri)
        startActivityForResult(intent, REQ_CAPTURE_IMAGE)
    }

    protected fun createImageUri() {
        val imageName = "IMG_" + System.currentTimeMillis() + ".jpg"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) { // 29以上使用Media保存图片
            val cv = ContentValues()
            cv.put(MediaStore.Images.Media.TITLE, imageName)
            cv.put(MediaStore.Images.Media.DISPLAY_NAME, imageName)
            cv.put(MediaStore.Images.Media.DESCRIPTION, imageName)
            cv.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            if (SelectionSpec.instance.captureDirectory == null) {
                cv.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            } else {
                cv.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/" + SelectionSpec.instance.captureDirectory)
            }
            mImageCaptureUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv)
        } else { // 29以下手动保存图片到相册
            var directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            SelectionSpec.instance.captureDirectory?.let { directory = File(directory, it) }
            if (!directory.exists()) directory.mkdirs()
            mImageCaptureFile = File(directory, imageName)
            mImageCaptureUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                FileProvider.getUriForFile(this, "$packageName.imagepicker", mImageCaptureFile!!)
            } else {
                Uri.fromFile(mImageCaptureFile)
            }
        }
    }

    protected fun onAlbumSelected(album: Album) {
        mAlbumMediaCollection.load(album, SelectionSpec.instance.capture)
        mAlbumTitleView.text = album.getDisplayName(this)
        if (album.isAll && album.isEmpty) {
            Toast.makeText(this, "没有图片", Toast.LENGTH_SHORT).show()
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
                    val albumMedia: ArrayList<AlbumMedia> = data!!.getParcelableArrayListExtra(PICKED_MEDIA)!!
                    pickerResult(mSelectedCollection.asListOfUri(albumMedia))
                }
                REQ_CAPTURE_IMAGE -> {
                    if (mImageCaptureUri != null) { // 拍照成功
                        if (SelectionSpec.instance.capturePick) { // 直接使用照片
                            if (mImageCaptureUri != null) pickerResult(ArrayList<Uri>(1).apply { add(mImageCaptureUri!!) })
                        } else {
                            mAlbumTitleView.postDelayed({
                                mImageCaptureFile?.let { saveImageIntoGallery(mImageCaptureFile!!) }
                                mAlbumMediaCollection.reload() // 刷新相册
                            }, 200)
                        }
                    }
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
            R.id.original -> toggleOriginal()
            R.id.confirm -> pickerResult(mSelectedCollection.asListOfUri())
        }
    }

    protected fun startPreview() {
        ImagePickerPreviewActivity.start(this, REQ_PREVIEW_IMAGE, mSelectedCollection.asList())
    }

    protected fun showAlbumList() {
        mAlbumTitleView.isSelected = true
        mAlbumRV.visibility = View.VISIBLE
        mShadow.visibility = View.VISIBLE
    }

    protected fun hideAlbumList() {
        mAlbumTitleView.isSelected = false
        mAlbumRV.visibility = View.GONE
        mShadow.visibility = View.GONE
    }

    protected fun pickerResult(images: ArrayList<Uri>) {
        setResult(Activity.RESULT_OK, intent.putParcelableArrayListExtra(PICKED_MEDIA, images).putExtra(SELECT_ORIGINAL, SelectionSpec.instance.original))
        onBackPressed()
    }

    private fun toggleOriginal() {
        SelectionSpec.instance.original = if (SelectionSpec.instance.original == 1) 0 else 1
        updateOriginal()
    }

    private fun updateOriginal() {
        if (SelectionSpec.instance.original == -1) return
        mOriginalView.setCompoundDrawablesWithIntrinsicBounds(if (SelectionSpec.instance.original == 1) R.drawable.ip_icon_original_pic_chosen else R.drawable.ip_icon_original_pic_unchoose, 0, 0, 0)
    }

    /**
     * 保存图片到相册
     */
    private fun saveImageIntoGallery(imageFile: File) {
        val name = imageFile.name
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, name)
        values.put(MediaStore.Images.Media.DISPLAY_NAME, name)
        values.put(MediaStore.Images.Media.DESCRIPTION, name)
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")

        val option = BitmapFactory.Options()
        option.inJustDecodeBounds = true
        BitmapFactory.decodeFile(imageFile.absolutePath, option)
        values.put(MediaStore.Images.ImageColumns.WIDTH, option.outWidth)
        values.put(MediaStore.Images.ImageColumns.HEIGHT, option.outHeight)
        values.put(MediaStore.Images.ImageColumns.DATA, imageFile.absolutePath)
        contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
    }
}
