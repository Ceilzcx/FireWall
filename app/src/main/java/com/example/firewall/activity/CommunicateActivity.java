package com.example.firewall.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firewall.R;
import com.example.firewall.adapter.CommunicateAdapter;
import com.example.firewall.bean.InterceptPhoneInfo;
import com.example.firewall.dao.InterceptDao;

import java.util.List;

/**
 * 通讯安全：
 *      包括获得手机的联系人、电话信息和短信信息
 *      自定义选择想要拦截电话、短信的电话号码
 */
public class CommunicateActivity extends AppCompatActivity {
    private List<InterceptPhoneInfo> infos;
    private InterceptDao dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_communicate);

        Toolbar toolbar = findViewById(R.id.tool_bar_communicate);
        toolbar.setNavigationOnClickListener(v -> finish());
        setSupportActionBar(toolbar);

        ////数据从SQLite获取
        dao = new InterceptDao(this);
        infos = dao.getAll();

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

        CommunicateAdapter adapter = new CommunicateAdapter(infos, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_communicate, menu);
        return true;
    }

    //添加右上角菜单点击事件
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.add_number_hand:
                InterceptDialog dialog = new InterceptDialog(this, R.style.dialog);
                dialog.setTitle("添加名单");
                dialog.show();
                break;
            case R.id.add_number_contact:
                Intent intent1 = new Intent(CommunicateActivity.this, PhoneContactActivity.class);
                startActivity(intent1);
                break;
            case R.id.add_number_log:
                Intent intent2 = new Intent(CommunicateActivity.this, PhoneCallLogActivity.class);
                startActivity(intent2);
                break;
        }
        return true;
    }



}
