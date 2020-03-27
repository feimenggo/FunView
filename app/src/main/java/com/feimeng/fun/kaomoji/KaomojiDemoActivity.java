package com.feimeng.fun.kaomoji;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.feimeng.fun.R;
import com.feimeng.kaomojiboard.KaomojiBoard;
import com.feimeng.kaomojiboard.loader.impl.DefaultKaomojiLoader;

public class KaomojiDemoActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caomoji_demo);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.group, KaomojiBoard.Companion.fragment(new DefaultKaomojiLoader()));
        fragmentTransaction.commitAllowingStateLoss();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.single:
                break;
            case R.id.multi:
                break;
        }
    }
}
