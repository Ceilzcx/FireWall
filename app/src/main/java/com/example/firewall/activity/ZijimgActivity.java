package com.example.firewall.activity;

import android.app.Activity;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import com.example.firewall.R;
import com.example.firewall.base.BaseActivityUpEnable;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/4/20.
 */

public class ZijimgActivity extends BaseActivityUpEnable {
    WifiManager wifi;
    List list;
    TextView show;
    String csum;


    public ZijimgActivity() {
        super(R.string.wific);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_zijims);
        show = (TextView) findViewById(R.id.textView);
        show.setMovementMethod(new ScrollingMovementMethod());
        wifi = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        if (!wifi.isWifiEnabled()) {
            if (wifi.getWifiState() != WifiManager.WIFI_STATE_ENABLING)
                wifi.setWifiEnabled(true);
        }
/**
 * 获取当前连接上的wifi相关信息
 */
        WifiInfo info = wifi.getConnectionInfo();
        int strength = info.getRssi();
        int speed = info.getLinkSpeed();
        String bssid = info.getBSSID();
        String ssid = info.getSSID();
        String units = WifiInfo.LINK_SPEED_UNITS;
        String wifiinformation = "检测到的wifi状况为: \n";
        /**
         * 获取扫描到的所有wifi相关信息
         */
        List<ScanResult> results = wifi.getScanResults();

        for(ScanResult result:results){
            wifiinformation += "mac地址为"+result.BSSID+",路由器名称"+result.SSID+",强度为"+result.level+"\n";
    }
        String text;
        wifiinformation += "\n";
        wifiinformation+="你的wifi连接状况为:";
        wifiinformation += "\n";
        if(bssid==null)
            bssid="00:00:00:00:00:00";
        if(bssid.equals("00:00:00:00:00:00")){
             text ="你没有连接到wifi";
        }
        else{ text = "你正连接的是" + ssid + "，速度为" + String.valueOf(speed) + String.valueOf(units)+",强度: " + strength;}
        wifiinformation += "\n\n";
        wifiinformation += text;

        show.setText(wifiinformation);}
    protected  void initEvent(){};
    protected  void initData(){};
    protected  void initView(){};

}
