package com.feimeng.fun.imagecrop;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.feimeng.fun.R;
import com.feimeng.imagecrop.view.ImageCropView;
import com.feimeng.imagepicker.ImagePicker;
import com.feimeng.imagepicker.MimeType;
import com.feimeng.imagepicker.engine.GlideEngine;
import com.feimeng.imagepicker.entity.SelectionSpec;

import java.util.ArrayList;

public class ImageCropDemoActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageCropView mImageCropView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_crop_demo);
        mImageCropView = findViewById(R.id.imageCrop);
        mImageCropView.setImageFilePath("/storage/emulated/0/Pictures/bili/1594729401171.png");
        mImageCropView.setDoubleTapEnabled(true);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.single:

                break;
        }
    }

    private void pickerSingle() {
        SelectionSpec selectionSpec = SelectionSpec.Companion.getNewCleanInstance(new GlideEngine());
        selectionSpec.setCapture(true);
        selectionSpec.setMimeTypeSet(MimeType.INSTANCE.ofImage());
        selectionSpec.setMediaTypeExclusive(false);
        selectionSpec.setMaxSelectable(1);
        ImagePicker.Companion.start(this, 10, selectionSpec);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            ArrayList<Uri> list = data.getParcelableArrayListExtra(ImagePicker.PICKED_MEDIA);
            StringBuilder sb = new StringBuilder();
            for (Uri uri : list) {
                sb.append(uri.toString()).append("\n");
            }
//            mTv.setText(sb.toString());
        }
    }

    private void crop() {
        if (mImageCropView.isChangingScale()) {
            Bitmap croppedImage = mImageCropView.getCroppedImage();
        }
    }
}
