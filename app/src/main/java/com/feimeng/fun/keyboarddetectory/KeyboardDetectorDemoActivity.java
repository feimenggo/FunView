package com.feimeng.fun.keyboarddetectory;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.feimeng.fun.R;
import com.feimeng.keyboard.detector.AnimHelper;
import com.feimeng.keyboard.detector.KeyboardDetector;
import com.feimeng.keyboard.detector.OnKeyboardChangeListener;

/**
 * Author: Feimeng
 * Time:   2020/3/31
 * Description:
 */
public class KeyboardDetectorDemoActivity extends AppCompatActivity implements OnKeyboardChangeListener {
    private EditText mInputView;

    private KeyboardDetector mDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keyboard_detector_demo);

        mInputView = findViewById(R.id.input);
//        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
//        fragmentTransaction.replace(R.id.group, KaomojiBoard.Companion.fragment(new MemoryKaomojiLoader()));
//        fragmentTransaction.commitAllowingStateLoss();
        mDetector = new KeyboardDetector(this);
        mDetector.setKeyboardChangeListener(this);
        mDetector.start(mInputView);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDetector.close();
    }

    @Override
    public void onKeyboardHeightChanged(boolean visible, int height, int orientation) {
        Log.d("nodawang", "visible:" + visible + " height:" + height + " orientation:" + orientation);
        AnimHelper.Companion.animWithToolkit(visible, height, mInputView);
    }
}
