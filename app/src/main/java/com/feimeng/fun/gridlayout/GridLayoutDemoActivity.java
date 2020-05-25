package com.feimeng.fun.gridlayout;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.feimeng.fun.R;
import com.feimeng.layout.GridLayout;

/**
 * Author: Feimeng
 * Time:   2020/3/31
 * Description:
 */
public class GridLayoutDemoActivity extends AppCompatActivity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_layout_demo);
        GridLayout gridLayout = findViewById(R.id.grid_layout);
    }

    @Override
    public void onClick(View v) {

    }
}
