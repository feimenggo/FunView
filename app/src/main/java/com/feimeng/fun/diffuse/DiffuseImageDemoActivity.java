
package com.feimeng.fun.diffuse;

import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.feimeng.diffuseimageview.DiffuseImageView;
import com.feimeng.fun.R;

/**
 * Author: Feimeng
 * Time:   2020/10/27
 * Description:
 */
public class DiffuseImageDemoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diffuse_image_demo);
        DiffuseImageView iv1 = findViewById(R.id.image1);
        iv1.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.img));
        DiffuseImageView iv2 = findViewById(R.id.image2);
        iv2.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.img2));
    }
}
