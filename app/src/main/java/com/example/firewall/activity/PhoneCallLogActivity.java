package com.example.firewall.activity;

import android.Manifest;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firewall.R;
import com.example.firewall.adapter.PhoneCallLogAdapter;
import com.example.firewall.bean.InterceptPhoneInfo;
import com.example.firewall.util.PermissionsUtils;
import com.example.firewall.dao.PhoneContactDao;

import java.util.List;

public class PhoneCallLogActivity extends AppCompatActivity {
    //权限数组（申请定位）
    private String[] permissions = new String[]{Manifest.permission.READ_CALL_LOG, Manifest.permission.WRITE_CALL_LOG};
    PhoneCallLogAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_calllog);

        PermissionsUtils.initPermissions(this, permissions);

        Toolbar toolbar = findViewById(R.id.tool_bar_mobile_callLog);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        RecyclerView recyclerView = findViewById(R.id.recycler_mobile_calllog);
        adapter = new PhoneCallLogAdapter(PhoneContactDao.getContentCallLog(this));
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
