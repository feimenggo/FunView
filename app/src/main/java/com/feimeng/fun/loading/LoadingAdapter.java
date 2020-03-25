package com.feimeng.fun.loading;

import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.feimeng.loading.Loading;

import static com.feimeng.loading.Loading.STATUS_EMPTY_DATA;
import static com.feimeng.loading.Loading.STATUS_LOADING;
import static com.feimeng.loading.Loading.STATUS_LOAD_FAILED;


/**
 * Author: Feimeng
 * Time:   2019/3/27 16:12
 * Description:
 */
public class LoadingAdapter implements Loading.Adapter {
    @Override
    public View getView(Loading.Status loading, View convertView, int status) {
        TextView tv = new TextView(loading.getContext());
        tv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        tv.setGravity(Gravity.CENTER);
        tv.setTextSize(20);
        switch (status) {
            case STATUS_LOADING:
                tv.setText("正在加载");
                tv.setBackgroundColor(Color.BLUE);
                break;
            case STATUS_LOAD_FAILED:
                tv.setText("加载失败");
                tv.setBackgroundColor(Color.RED);
                break;
            case STATUS_EMPTY_DATA:
                tv.setText("没有数据");
                tv.setBackgroundColor(Color.YELLOW);
                break;
        }
        return tv;
    }
}
