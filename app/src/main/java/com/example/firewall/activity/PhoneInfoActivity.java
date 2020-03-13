package com.example.firewall.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firewall.R;
import com.example.firewall.adapter.PhoneInfoAdapter;
import com.example.firewall.util.IpUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 手机信息的界面：
 *      包括查看MAC地址、主板信息等等
 */
public class PhoneInfoActivity extends AppCompatActivity {
    private List<String> infos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_info);

        initData();

        Toolbar toolbar = findViewById(R.id.tool_bar_phone_info);
        toolbar.setNavigationOnClickListener(v -> finish());

        RecyclerView recyclerView = findViewById(R.id.recycler_phone_info);
        PhoneInfoAdapter adapter = new PhoneInfoAdapter(infos);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    //初始化获得数据
    @SuppressLint("HardwareIds")
    private void initData() {
        infos = new ArrayList<>();
        TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        WifiManager wifiManager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }

        assert telephonyManager != null;
        assert wifiManager != null;

        infos.add("用户："+ Build.USER);
        infos.add("ID："+Build.ID);
        infos.add("");

        infos.add("设备编号："+telephonyManager.getDeviceId());
        infos.add("手机型号："+Build.MODEL);
        infos.add("手机品牌："+Build.BRAND);
        infos.add("手机类型："+Build.TYPE);
        infos.add("SIM卡序号："+telephonyManager.getSimSerialNumber());
        infos.add("主板："+Build.BOARD);
        infos.add("固件："+Build.DEVICE);

        infos.add("");
        String number = telephonyManager.getLine1Number();
        if (number == null)
            number = "N/A";
        infos.add("手机号码："+number);   //不一定成功
        infos.add("MAC地址："+wifiManager.getConnectionInfo().getMacAddress());
        infos.add("IP地址："+ IpUtils.getIPAddress(this));
    }

}

