package com.example.firewall.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.firewall.R;
import com.example.firewall.base.BaseActivityUpEnable;
import com.example.firewall.bean.AppInfoBean;
import com.example.firewall.engine.AppManagerEngine;


import java.util.ArrayList;
import java.util.List;

/**
 *清除缓存类
 */
public class CleanCacheActivity extends BaseActivityUpEnable {
    private static final int REQUEST_CODE_ACCESS_COARSE_LOCATION =1000 ;
    // 视图
    private TextView tvResult;
    private ProgressBar pbProgress;
    private ListView lvApp;
    private Button btnOk;

    // 数据
    private List<AppInfoBean> apps = new ArrayList<>();
    private AppAdapter adapter;

    /**
     * construct method. set the action bar title
     */
    public CleanCacheActivity() {
        super(R.string.clean_cache);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestReadNetworkStats();
        initView();
        initData();
        initEvent();
    }

    /**
     * 1
     */
    private void requestReadNetworkStats() {
        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
        startActivity(intent);
    }

    public void requestUserPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//如果 API level 是大于等于 23(Android 6.0) 时
            //判断是否具有权限
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.PACKAGE_USAGE_STATS) != PackageManager.PERMISSION_GRANTED) {
                //判断是否需要向用户解释为什么需要申请该权限
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.PACKAGE_USAGE_STATS)) {
                    Toast.makeText(this, "请打开位置权限", Toast.LENGTH_SHORT);

                }
                //请求权限
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.PACKAGE_USAGE_STATS},
                        REQUEST_CODE_ACCESS_COARSE_LOCATION);
            }
        }
    }
    protected void initView() {
        setContentView(R.layout.activity_clean_cache);
        // 绑定视图
        tvResult = (TextView) findViewById(R.id.tv_result);
        pbProgress = (ProgressBar) findViewById(R.id.pb_progress);
        lvApp = (ListView) findViewById(R.id.lv_app);
        btnOk = (Button) findViewById(R.id.btn_ok);

        //隐藏视图
        tvResult.setVisibility(View.GONE);
        btnOk.setVisibility(View.GONE);

    }

    /**
     * 2
     */

    protected void initData() {
        // 操作很耗时，需要在子线程上运行
        new Thread() {
            @Override
            public void run() {
                // 获取所有app信息
                AppManagerEngine.getInstalledAppInfo(CleanCacheActivity.this, new AppManagerEngine.AppInfoListener() {
                    @Override
                    public void onGetInfoCompleted(List<AppInfoBean> apps) {
                        onDataGetCompleted(apps);
                    }
                });
            }
        }.start();


    }

    /**
     * 过滤无缓存应用程序并显示列表
     *
     * @param apps
     */
    private void onDataGetCompleted(final List<AppInfoBean> apps) {
        // 更改列表数据
        runOnUiThread(() -> {
            //过滤无缓存
            for (AppInfoBean app : apps) {
                if (app.getCacheSize() > 0) {
                    CleanCacheActivity.this.apps.add(app);

                }
            }
            //隐藏进程

            pbProgress.setVisibility(View.GONE);

            // 检查缓存应用的数量
            // 如果没有应用程序缓存
            if (0 == CleanCacheActivity.this.apps.size()) {
                // 不显示缓存数据并设置事件
                tvResult.setVisibility(View.VISIBLE);
                lvApp.setVisibility(View.GONE);
                btnOk.setVisibility(View.VISIBLE);
                // 只需点击按钮即可完成
                btnOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
                return;
            }

            // 存在app

            // 设置 adapter
            adapter = new AppAdapter();
            lvApp.setAdapter(adapter);

            // 隐藏其他视图
            tvResult.setVisibility(View.GONE);
            lvApp.setVisibility(View.VISIBLE);
            btnOk.setVisibility(View.VISIBLE);
            // 改变按钮上文字
            btnOk.setText(R.string.clear_right_now);
            // 点击按钮时清除缓存
            btnOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clearAllCache();
                }
            });
        });
    }

    /**
     * 清除应用中的所有缓存
     */
    private void clearAllCache() {
        AppManagerEngine.clearAllCache(this, new AppManagerEngine.ClearCacheListener() {
            @Override
            public void onClearCompleted() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CleanCacheActivity.this.onClearCompleted();
                    }
                });
            }

            @Override
            public void onClearFailed() {
                CleanCacheActivity.this.onClearFailed();
            }
        });
    }

    /**
     * 3
     */

    protected void initEvent() {
        //设置项目点击监听器来启动应用信息活动
        lvApp.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AppInfoBean bean = (AppInfoBean) lvApp.getItemAtPosition(position);
//                System.out.println(bean.getName());
                Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS",
                        Uri.parse("package:" + bean.getPackageName()));
                startActivity(intent);
            }
        });

    }

    /**
     * will be call when clear cache completed
     */
    private void onClearCompleted() {
        // 更改按钮文本和事件
        btnOk.setText(R.string.ok);
        // 按钮点击时结束
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 显示提示
        long totalSize = 0;
        for (AppInfoBean app : apps) {
            totalSize += app.getCacheSize();
        }
        Toast.makeText(this, getString(R.string.tips_clear_cache_completed,
                Formatter.formatFileSize(this, totalSize)),
                Toast.LENGTH_SHORT).show();

        // clear list
        apps.clear();
        adapter.notifyDataSetChanged();
    }

    /**
     * 将在清除缓存失败时调用
     */
    private void onClearFailed() {
        Toast.makeText(this, R.string.tips_failed_to_clear, Toast.LENGTH_SHORT).show();
    }

    /**
     * app adapter
     */
    private class AppAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return apps.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            AppItem item = null;
            //如果没有缓存，则创建一个视图
            if (null == convertView) {
                convertView = View.inflate(CleanCacheActivity.this, R.layout.item_clean_cache_lv, null);
                //绑定视图
                item = new AppItem();
                item.ivIcon = (ImageView) convertView.findViewById(R.id.iv_icon);
                item.tvTitle = (TextView) convertView.findViewById(R.id.tv_title);
                item.tvSize = (TextView) convertView.findViewById(R.id.tv_size);
                // 设置item
                convertView.setTag(item);
            } else {
                item = (AppItem) convertView.getTag();
            }
            // 取值
            AppInfoBean app = getItem(position);
            // 赋值
            item.ivIcon.setImageDrawable(app.getIcon());
            item.tvTitle.setText(app.getName());
            item.tvSize.setText(Formatter.formatFileSize(CleanCacheActivity.this, app.getCacheSize()));

            return convertView;
        }

        @Override
        public AppInfoBean getItem(int position) {
            return apps.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

    }

    /**
     * 只用于AppAdapter
     */
    private static class AppItem {
        private ImageView ivIcon;
        private TextView tvTitle;
        private TextView tvSize;
    }

}
