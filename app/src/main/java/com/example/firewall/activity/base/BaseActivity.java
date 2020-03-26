package com.example.firewall.activity.base;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


/**
 *基本模板活动
 *如果扩展此活动，则会在onCreate时按顺序调用initView initData initEvent。
 *不需要重写onCreate。 只需在initView中调用setContentView即可。
 */
public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();

    }

    /**
     * 初始化
     * 按顺序调用initView initData initEvent
     * 如果更改order，覆盖class
     */
    protected void init() {
        initView();
        initData();
        initEvent();
    }

    /**
     初始化所有视图
     */
    protected abstract void initView();

    /**
     * 初始化数据
     */
    protected abstract void initData();

    /**
     * 初始化事件
     */
    protected abstract void initEvent();

}
