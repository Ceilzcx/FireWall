package com.example.firewall.activity;

import android.Manifest;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firewall.R;
import com.example.firewall.adapter.PhoneCallLogAdapter;
import com.example.firewall.dao.PermissionsDao;
import com.example.firewall.dao.PhoneContactDao;

public class PhoneCallLogActivity extends AppCompatActivity {
    //权限数组（申请定位）
    private String[] permissions = new String[]{Manifest.permission.READ_CALL_LOG, Manifest.permission.WRITE_CALL_LOG};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_calllog);

        PermissionsDao.initPermissions(this, permissions);

        RecyclerView recyclerView = findViewById(R.id.recycler_mobile_calllog);
        PhoneCallLogAdapter adapter = new PhoneCallLogAdapter(PhoneContactDao.getContentCallLog(this));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

}
