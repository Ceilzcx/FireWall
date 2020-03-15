package com.example.firewall.activity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.firewall.R;
import com.example.firewall.base.BaseActivityUpEnable;
import com.example.firewall.bean.VirusBean;
import com.example.firewall.constant.Constant;


import java.util.ArrayList;

/**
 * 展示扫描结果
 */
public class AntivirusResultActivity extends BaseActivityUpEnable {
    // 视图
    private TextView tvResult;
    private Button btnResult;
    // data
    private ArrayList<VirusBean> viruses;

    /**
     * 构建方法 设置操作栏标题
     */
    public AntivirusResultActivity() {
        super(R.string.anti_virus);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
        initEvent();
    }

    /**
     * 1
     */

    protected void initView() {
        setContentView(R.layout.activity_antivirus_result);
        // 绑定视图
        tvResult = (TextView) findViewById(R.id.tv_result);
        btnResult = (Button) findViewById(R.id.btn_result);

    }

    /**
     * 2
     */

    protected void initData() {
        // 获取List

        viruses = (ArrayList<VirusBean>) getIntent().getSerializableExtra(Constant.EXTRA_VIRUSES);
        // 如果没有病毒，则不显示病毒并设置退出事件
        if (null == viruses || 0 == viruses.size()) {
            btnResult.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
            return;
        }

        // 如果有病毒，则显示结果并设置卸载事件
        tvResult.setText(getString(R.string.found_virus, viruses.size()));
        tvResult.setTextColor(Color.RED);

        btnResult.setText(R.string.clear_right_now);

        // 设置卸载监听器
        btnResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 删除病毒
                clearViruses();
                // 重置按钮文本和事件
                clearFinish();
            }
        });

    }

    /**
     *设置按钮文本为OK 监听器设置
     */
    private void clearFinish() {
        btnResult.setText(R.string.ok);
        btnResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * 删除病毒app
     */
    private void clearViruses() {
        //卸载用户应用程序
        for (VirusBean virus : viruses) {
            Intent intent = new Intent(Intent.ACTION_DELETE, Uri.parse("package:" + virus.getPackageName()));
            startActivity(intent);
        }
    }

    /**
     * 3
     */

    protected void initEvent() {

    }
}
