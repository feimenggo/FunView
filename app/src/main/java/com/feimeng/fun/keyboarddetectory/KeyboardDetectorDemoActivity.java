package com.feimeng.fun.keyboarddetectory;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.feimeng.fun.R;
import com.feimeng.keyboard.detector.KeyboardDetector;
import com.feimeng.keyboard.detector.KeyboardLayoutHelper;

/**
 * Author: Feimeng
 * Time:   2020/3/31
 * Description:
 */
public class KeyboardDetectorDemoActivity extends AppCompatActivity implements KeyboardLayoutHelper.OnKeyboardCallback, View.OnClickListener {
    private ViewGroup mInputWrap;
    private EditText mTextareaView;
    private TextView mFace;
    private View mFloatingView;
    private KeyboardLayoutHelper mKeyboardLayoutHelper;

    private KeyboardDetector mDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keyboard_detector_demo);

        mTextareaView = findViewById(R.id.textarea);
        mInputWrap = findViewById(R.id.inputWrap);
        mFace = findViewById(R.id.face);
        mFace.setOnClickListener(this);
        mFloatingView = findViewById(R.id.floating);
//        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
//        fragmentTransaction.replace(R.id.group, KaomojiBoard.Companion.fragment(new MemoryKaomojiLoader()));
//        fragmentTransaction.commitAllowingStateLoss();
        mDetector = new KeyboardDetector(this);
        mDetector.addKeyboardChangeListener(mKeyboardLayoutHelper = new KeyboardLayoutHelper().addSmoothView(mFloatingView)
                .addInstantView(mTextareaView, 0, getResources().getDimensionPixelOffset(R.dimen.toolkitHeight))
                .enablePanel(this, mInputWrap, getResources().getDimensionPixelOffset(R.dimen.toolkitHeight), this));
        mDetector.start(mInputWrap);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDetector.destroy();
    }

    @Override
    public void onKeyboardVisibility(boolean show, int keyboardHeight) {
        Log.d("nodawang", "onKeyboardToggle:" + show);
//        View childAt = mInputWrap.getChildAt(1);
//        if (childAt.getLayoutParams().height != keyboardHeight) {
//            childAt.getLayoutParams().height = keyboardHeight;
//            childAt.requestLayout();
//        }
    }

    @Override
    public void onPanelVisibility(boolean show) {
        Log.d("nodawang", "onPanelToggle:" + show);
        mFace.setText(show ? "开" : "关");
    }

    @Override
    public void onClick(View v) {
        if (v == mFace) {
            mKeyboardLayoutHelper.togglePanel();
        }
    }
}
