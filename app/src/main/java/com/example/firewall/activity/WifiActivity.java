package com.example.firewall.activity;

import java.util.ArrayList;
import java.util.List;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.firewall.R;
import com.example.firewall.base.BaseActivityUpEnable;
import com.example.firewall.util.WifiUtils;


/**
 * Search WIFI and show in ListView
 *
 */
public class WifiActivity extends BaseActivityUpEnable implements OnClickListener,
        OnItemClickListener {
    private static final int REQUEST_CODE_ACCESS_COARSE_LOCATION = 100;
    private Button search_btn;
    private Button ziji_btn;
    private Button port_btn;
    private ListView wifi_lv;
    private TextView wifi_ssid_tv;
    private WifiUtils mUtils;
    private List<String> result;
    private ProgressDialog progressdlg = null;
    private LocationManager locManager;
    private  int WIFI_SCAN_PERMISSION_CODE=1;
    public WifiActivity() {
        super(R.string.wifia);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestLocationPermission();
        setContentView(R.layout.activity_wifi);
        mUtils = new WifiUtils(this);
        findViews();
        setLiteners();

    }
    public void requestLocationPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//如果 API level 是大于等于 23(Android 6.0) 时
            //判断是否具有权限
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //判断是否需要向用户解释为什么需要申请该权限
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    Toast.makeText(this, "请打开位置权限", Toast.LENGTH_SHORT);

                }
                //请求权限
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        REQUEST_CODE_ACCESS_COARSE_LOCATION);
            }
        }
    }

    private void findViews() {
        this.search_btn = (Button) findViewById(R.id.search_btn);
        this.wifi_lv = (ListView) findViewById(R.id.wifi_lv);
        this.wifi_ssid_tv=(TextView)findViewById(R.id.mstext);
        this.ziji_btn = (Button) findViewById(R.id.ziji_btn);
    }

    private void setLiteners() {
        search_btn.setOnClickListener(this);
        ziji_btn.setOnClickListener(this);
        wifi_lv.setOnItemClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.search_btn) {
            showDialog();
            wifi_lv.setAdapter(new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1));
            new MyAsyncTask().execute();
        }
        if (v.getId() == R.id.ziji_btn) {
            Intent i = new Intent(WifiActivity.this, ZijimgActivity.class);
            startActivity(i);
        }
    }

    /**
     * init dialog and show
     */
    private void showDialog() {
        progressdlg = new ProgressDialog(this);
        progressdlg.setCanceledOnTouchOutside(false);
        progressdlg.setCancelable(false);
        progressdlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressdlg.setMessage(getString(R.string.wait_moment));
        progressdlg.show();
    }
    /**
     * dismiss dialog
     */
    private void progressDismiss() {
        if (progressdlg != null) {
            progressdlg.dismiss();
        }
    }

    class MyAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... arg0) {
            //扫描附近WIFI信息
            result = mUtils.getScanWifiResult();
            Log.d("result",String.valueOf(result.size()));
            publishProgress();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            progressDismiss();
            initListViewData();
        }
    }

    private void initListViewData() {
        if (null != result && result.size() > 0) {
            wifi_lv.setAdapter(new ArrayAdapter<String>(
                    getApplicationContext(), R.layout.wifi_list_item,
                    R.id.ssid, result));
            wifi_ssid_tv.setText("共找到wifi "+ result.size()+"个");
        } else {
            wifi_ssid_tv.setText("没找到wifi");
        }
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        TextView tv = (TextView) arg1.findViewById(R.id.ssid);
        if (!TextUtils.isEmpty(tv.getText().toString())) {
            Intent in = new Intent(WifiActivity.this, WifiConnectActivity.class);
            in.putExtra("ssid", tv.getText().toString());
            startActivity(in);
        }
    }
    protected  void initEvent(){};
    protected  void initData(){};
    protected  void initView(){};
}
