package com.example.firewall.activity;

import android.Manifest;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firewall.R;
import com.example.firewall.adapter.PhoneContactAdapter;
import com.example.firewall.bean.InterceptPhoneInfo;
import com.example.firewall.util.PermissionsUtils;
import com.example.firewall.dao.PhoneContactDao;

import java.util.List;

/**
 * 手机联系人界面
 *      从手机读取联系人姓名+号码
 *      将指定的联系人添加到拦截名单(存入SQLite数据库)
 */
public class PhoneContactActivity extends AppCompatActivity {
    //权限数组（申请定位）
    private String[] permissions = new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS};
    private PhoneContactAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_contact);

        PermissionsUtils.initPermissions(this, permissions);

        Toolbar toolbar = findViewById(R.id.tool_bar_mobile_contact);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        RecyclerView recyclerView = findViewById(R.id.recycler_mobile_contact);
        adapter = new PhoneContactAdapter(PhoneContactDao.queryContactPhoneNumber(this));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    public void addInterceptNumber(MenuItem item){
        List<InterceptPhoneInfo> infos = adapter.getCheckInfos();
        InterceptContactDialog dialog = new InterceptContactDialog(this, R.style.dialog, infos);
        dialog.show();
    }

    public void back(MenuItem item){
        finish();
    }

}
