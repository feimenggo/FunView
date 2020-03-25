package com.feimeng.fun.behavior;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.feimeng.fun.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Feimeng
 * Time:   2020/3/23
 * Description:
 */
public class StudyBehaviorActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_behavior);

        RecyclerView recyclerView = findViewById(R.id.my_list);
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            list.add(i + 1 + "");
        }
        TypeAdapter adapter = new TypeAdapter(this, list, false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }
}
