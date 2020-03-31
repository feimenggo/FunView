package com.feimeng.fun.kaomoji;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.feimeng.fun.R;
import com.feimeng.fun.kaomoji.loader.MemoryKaomojiLoader;
import com.feimeng.keyboard.kaomojiboard.KaomojiBoard;

public class KaomojiDemoActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caomoji_demo);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.group, KaomojiBoard.Companion.fragment(new MemoryKaomojiLoader()));
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
