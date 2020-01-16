package com.feimeng.fun;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.feimeng.network.monitor.NetWorkMonitorManager;
import com.feimeng.network.monitor.NetWorkState;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn).setOnClickListener(this);
        NetWorkMonitorManager.getInstance().init(getApplication());
        NetWorkMonitorManager.getInstance().register(this);
    }

    @Override
    public void onClick(View view) {
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
}
