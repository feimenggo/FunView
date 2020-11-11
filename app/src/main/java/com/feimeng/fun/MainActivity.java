package com.feimeng.fun;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.feimeng.fun.diffuse.DiffuseImageDemoActivity;
import com.feimeng.fun.gridlayout.GridLayoutDemoActivity;
import com.feimeng.fun.imagecrop.ImageCropDemoActivity;
import com.feimeng.fun.imagepicker.ImagePickerDemoActivity;
import com.feimeng.fun.imageviewer.ImageViewerDemoActivity;
import com.feimeng.fun.kaomoji.KaomojiDemoActivity;
import com.feimeng.fun.keyboarddetectory.KeyboardDetectorDemoActivity;
import com.feimeng.fun.ratio.RatioLayoutDemoActivity;
import com.feimeng.fun.replace.ReplaceDemoActivity;
import com.feimeng.network.monitor.NetWorkMonitorManager;
import com.feimeng.network.monitor.NetWorkState;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.keyboard).setOnClickListener(this);
        findViewById(R.id.kaomoji).setOnClickListener(this);
        findViewById(R.id.imagePicker).setOnClickListener(this);
        findViewById(R.id.imageCrop).setOnClickListener(this);
        findViewById(R.id.imageViewer).setOnClickListener(this);
        findViewById(R.id.ratio).setOnClickListener(this);
        findViewById(R.id.grid).setOnClickListener(this);
        findViewById(R.id.replace).setOnClickListener(this);
        findViewById(R.id.diffuse).setOnClickListener(this);
        findViewById(R.id.btn).setOnClickListener(this);
        NetWorkMonitorManager.getInstance().init(getApplication());
        NetWorkMonitorManager.getInstance().register(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.keyboard:
                startActivity(new Intent(this, KeyboardDetectorDemoActivity.class));
                break;
            case R.id.kaomoji:
                startActivity(new Intent(this, KaomojiDemoActivity.class));
                break;
            case R.id.imagePicker:
                startActivity(new Intent(this, ImagePickerDemoActivity.class));
                break;
            case R.id.imageCrop:
                startActivity(new Intent(this, ImageCropDemoActivity.class));
                break;
            case R.id.imageViewer:
                startActivity(new Intent(this, ImageViewerDemoActivity.class));
                break;
            case R.id.ratio:
                startActivity(new Intent(this, RatioLayoutDemoActivity.class));
                break;
            case R.id.grid:
                startActivity(new Intent(this, GridLayoutDemoActivity.class));
                break;
            case R.id.replace:
                startActivity(new Intent(this, ReplaceDemoActivity.class));
                break;
            case R.id.diffuse:
                startActivity(new Intent(this, DiffuseImageDemoActivity.class));
                break;
        }
    }

    public void onN(NetWorkState netWorkState) {
        Log.d("nodawang", "state:" + netWorkState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NetWorkMonitorManager.getInstance().unregister(this);
        NetWorkMonitorManager.getInstance().onDestroy();
    }

    public void addView(View... a) {
    }
}
