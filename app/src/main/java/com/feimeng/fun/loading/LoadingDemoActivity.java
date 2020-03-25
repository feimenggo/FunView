package com.feimeng.fun.loading;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.feimeng.fun.R;
import com.feimeng.loading.Loading;

public class LoadingDemoActivity extends AppCompatActivity implements View.OnClickListener {
    private Loading.Status mLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_demo);
        TextView content = findViewById(R.id.content);
        FrameLayout root = findViewById(R.id.root);
        mLoading = Loading.from(new LoadingAdapter()).into(root, content.getLayoutParams());
        findViewById(R.id.loading).setOnClickListener(this);
        findViewById(R.id.success).setOnClickListener(this);
        findViewById(R.id.failure).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.loading:
                mLoading.showLoading();
                break;
            case R.id.success:
                mLoading.showLoadSuccess(200);
                break;
            case R.id.failure:
                mLoading.showLoadFailed();
                break;
        }
    }
}
