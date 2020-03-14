package com.example.firewall.activity;

import android.Manifest;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firewall.R;
import com.example.firewall.adapter.PhoneContactAdapter;
import com.example.firewall.util.PermissionsUtil;
import com.example.firewall.dao.PhoneContactDao;

/**
 * 手机联系人界面
 *      从手机读取联系人姓名+号码
 *      将指定的联系人添加到拦截名单(存入SQLite数据库)
 */
public class PhoneContactActivity extends AppCompatActivity {
    //权限数组（申请定位）
    private String[] permissions = new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_contact);

        PermissionsUtil.initPermissions(this, permissions);

        RecyclerView recyclerView = findViewById(R.id.recycler_mobile_contact);
        PhoneContactAdapter adapter = new PhoneContactAdapter(PhoneContactDao.queryContactPhoneNumber(this));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

}
