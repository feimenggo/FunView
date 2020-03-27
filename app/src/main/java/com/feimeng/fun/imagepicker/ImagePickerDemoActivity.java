package com.feimeng.fun.imagepicker;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.feimeng.fun.R;
import com.feimeng.imagepicker.ImagePicker;
import com.feimeng.imagepicker.MimeType;
import com.feimeng.imagepicker.engine.GlideEngine;
import com.feimeng.imagepicker.entity.SelectionSpec;

import java.util.ArrayList;

public class ImagePickerDemoActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView mTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_picker_demo);
        findViewById(R.id.single).setOnClickListener(this);
        findViewById(R.id.multi).setOnClickListener(this);
        mTv = findViewById(R.id.tv);
        requestPermissionAndDisplayGallery();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.single:
                pickerSingle();
                break;
            case R.id.multi:
                pickerMulti();
                break;
        }
    }

    private void requestPermissionAndDisplayGallery() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 99);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "没有存储卡读写权限", Toast.LENGTH_SHORT).show();
            finish();
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

    private void pickerMulti() {
        SelectionSpec selectionSpec = SelectionSpec.Companion.getNewCleanInstance(new GlideEngine());
        selectionSpec.setCapture(true);
        selectionSpec.setMimeTypeSet(MimeType.INSTANCE.ofAll());
        selectionSpec.setMediaTypeExclusive(false);
        selectionSpec.setMaxSelectable(9);
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
            mTv.setText(sb.toString());
        }
    }
}
