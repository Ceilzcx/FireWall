package com.example.firewall.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.firewall.R;
import com.example.firewall.bean.InterceptPhone;

import java.util.List;

/**
 * 通讯安全：
 *      包括获得手机的联系人、电话信息和短信信息
 *      自定义选择想要拦截电话、短信的电话号码
 */
public class CommunicateActivity extends AppCompatActivity {
    //数据从SQLite获取
    private List<InterceptPhone> phones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commuicate);

        Toolbar toolbar = findViewById(R.id.tool_bar_communicate);
        toolbar.setNavigationOnClickListener(v -> finish());

    }

}
