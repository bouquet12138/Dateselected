package com.example.datechioce;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.datechioce.custom_view.BottomPopDateSelect;
import com.example.datechioce.custom_view.DatePickerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Button mAppearBt;
    private BottomPopDateSelect mBottomPopDateSelect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initListener();
    }

    /**
     * 初始化view
     */
    private void initView() {
        mAppearBt = findViewById(R.id.appearBt);
        mBottomPopDateSelect = new BottomPopDateSelect(MainActivity.this);
    }

    /**
     * 初始化监听
     */
    private void initListener() {
        mAppearBt.setOnClickListener((view) -> {
            mBottomPopDateSelect.show();//展示一下
        });
        mBottomPopDateSelect.setOnConfirmListener((currentDate) -> {
            Toast.makeText(this, currentDate, Toast.LENGTH_SHORT).show();
            mBottomPopDateSelect.dismiss();//弹窗消失
        });
    }


}
