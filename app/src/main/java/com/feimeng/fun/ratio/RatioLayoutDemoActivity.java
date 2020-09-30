package com.feimeng.fun.ratio;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.feimeng.fun.R;
import com.feimeng.view.RangeSeekBar;

/**
 * Author: Feimeng
 * Time:   2020/3/31
 * Description:
 */
public class RatioLayoutDemoActivity extends AppCompatActivity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ratio_layout_demo);
        RangeSeekBar seekBar = findViewById(R.id.seek);
        seekBar.setSeekBarTextCreater(new RangeSeekBar.SeekBarTextCreater() {
            @Override
            public String createText(int progress) {
                return String.valueOf(progress + 11);
            }
        });
    }

    @Override
    public void onClick(View v) {

    }
}
