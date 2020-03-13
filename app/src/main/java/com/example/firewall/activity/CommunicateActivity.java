package com.example.firewall.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firewall.R;
import com.example.firewall.adapter.CommunicateAdapter;
import com.example.firewall.bean.InterceptPhoneInfo;

import java.util.List;

/**
 * 通讯安全：
 *      包括获得手机的联系人、电话信息和短信信息
 *      自定义选择想要拦截电话、短信的电话号码
 */
public class CommunicateActivity extends AppCompatActivity {
    private List<InterceptPhoneInfo> infos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_communicate);

        Toolbar toolbar = findViewById(R.id.tool_bar_communicate);
        toolbar.setNavigationOnClickListener(v -> finish());

        ////数据从SQLite获取

        TextView textView = findViewById(R.id.text_message);
        RecyclerView recyclerView = findViewById(R.id.recycler_communicate);
        if (infos.size() == 0) {
            textView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.INVISIBLE);
            return;
        }else {
            textView.setVisibility(View.INVISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
        }

        CommunicateAdapter adapter = new CommunicateAdapter(infos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

    }

}
