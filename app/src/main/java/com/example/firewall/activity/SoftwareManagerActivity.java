package com.example.firewall.activity;

import android.app.AppOpsManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.format.Formatter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;

import com.example.firewall.R;
import com.example.firewall.base.BaseActivityUpEnableWithMenu;
import com.example.firewall.bean.AppInfoBean;

import com.example.firewall.dao.AppManagerDao;
import com.example.firewall.util.PermissionsUtils;
import com.stericson.RootTools.RootTools;

import java.util.ArrayList;
import java.util.List;


/**
 * 管理所有应用 包括系统应用
 */
public class SoftwareManagerActivity extends BaseActivityUpEnableWithMenu {
    // view
    private TextView tvRomFreeSpace;
    private TextView tvSdCardFreeSpace;
    private TextView tvTypeLabel;
    private ListView lvApp;
    private ProgressBar pbLoading;


    private List<AppInfoBean> systemApps = new ArrayList<>();
    private List<AppInfoBean> userApps = new ArrayList<>();
    private List<Boolean> checkeds = new ArrayList<>();
    private AppAdapter adapter = new AppAdapter();
    private AppRemovedReceiver uninstallReceiver = new AppRemovedReceiver();


    private boolean root;


    private Thread initDateThread;

    /**
     * 方法构建
     */
    public SoftwareManagerActivity() {
        super(R.string.software_manager, R.menu.menu_software_manager);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String permission = "android.permission.PACKAGE_USAGE_STATS";
        boolean granted= this.checkCallingOrSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
        if (!granted)
            requestReadNetworkStats();

        initView();
        initData();
        initEvent();
    }

    private void requestReadNetworkStats() {
        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
        startActivity(intent);
    }

    /**
     * 1
     */

    protected void initView() {
        setContentView(R.layout.activity_software_manager);
        // 绑定视图
        tvRomFreeSpace =  findViewById(R.id.tv_rom_free_sapce);
        tvSdCardFreeSpace =  findViewById(R.id.tv_sd_card_free_sapce);
        tvTypeLabel =  findViewById(R.id.tv_type_label);
        lvApp =  findViewById(R.id.lv_app);
        pbLoading =  findViewById(R.id.pb_loading);

        // 设置adapter
        lvApp.setAdapter(adapter);
    }

    /**
     * 2
     */

    protected void initData() {
        // 检查是否root
        root = RootTools.isRootAvailable();

        long romFreeSpace = AppManagerDao.getRomFreeSpace();
        long sdCardFreeSpace = AppManagerDao.getSdCardFreeSpace();

        tvRomFreeSpace.setText(getString(R.string.rom_free_space) + Formatter.formatFileSize(this, romFreeSpace));
        tvSdCardFreeSpace.setText(getString(R.string.sd_card_free_space) + Formatter.formatFileSize(this, sdCardFreeSpace));


        if (null != initDateThread && initDateThread.isAlive()) {
            return;
        }

        initDateThread = new Thread() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void run() {
                // 获取应用信息
                AppManagerDao.getInstalledAppInfo(
                        SoftwareManagerActivity.this,
                        apps -> initAppsInfo(apps));
            }
        };

        initDateThread.start();
    }

    /**
     * 初始化app信息
     *
     * @param apps
     */
    private void initAppsInfo(final List<AppInfoBean > apps) {

        runOnUiThread(() -> {
            //添加前清除列表
            systemApps.clear();
            userApps.clear();
            checkeds.clear();
            // 添加到列表
            for (AppInfoBean  app : apps) {
                if (app.isSystemApp()) {
                    systemApps.add(app);
                } else {
                    userApps.add(app);
                }
                checkeds.add(false);
            }

            checkeds.add(false);
            checkeds.add(false);
            // 刷新ui
            onDataChanged();
        });
    }

    /**
     * 刷新进度条和Listview
     */
    private void onDataChanged() {

        adapter.notifyDataSetChanged();

        pbLoading.setVisibility(View.GONE);

        tvTypeLabel.setVisibility(View.VISIBLE);
    }

    /**
     * 3
     */

    protected void initEvent() {
        //设置滚动侦听器来实现类型标签更改文本
        lvApp.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                //防止数据未加载
                if (0 == userApps.size() || 0 == systemApps.size())
                    return;
                // 标签显示在屏幕上方
                if (firstVisibleItem >= userApps.size() + 1) {
                    // 系统标签
                    tvTypeLabel.setText(getString(R.string.system_app) + "(" + systemApps.size() + ")");
                } else {
                    //用户标签
                    tvTypeLabel.setText(getString(R.string.user_app) + "(" + userApps.size() + ")");
                }
            }
        });
        //设置item监听
        lvApp.setOnItemClickListener((parent, view, position, id) -> {
            AppInfoBean bean = (AppInfoBean) lvApp.getItemAtPosition(position);
//                System.out.println(bean.getName());
            Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS",
                    Uri.parse("package:" + bean.getPackageName()));
            startActivity(intent);
        });

        // 删除包
        IntentFilter filter = new IntentFilter(Intent.ACTION_PACKAGE_REMOVED);
        filter.addDataScheme("package");
        registerReceiver(uninstallReceiver, filter);
    }

    /**
     * 注销app reciever
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(uninstallReceiver);
    }

    /**
     * 程序卸载时，通知刷新LV
     */
    private class AppRemovedReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //initData();
            System.out.println("AppRemovedReceiver");
        }
    }

    /**
     * app adapter
     */
    private class AppAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            // 两个标签
            return systemApps.size() + userApps.size() + 2;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            Context context = SoftwareManagerActivity.this;
            // 第一个为用户标签
            if (0 == position) {
                TextView tv = (TextView) View.inflate(context, R.layout.item_software_manager_type_label_lv, null);
                tv.setText(getString(R.string.user_app) + "(" + userApps.size() + ")");
                return tv;
            }
            //用户+1为系统标签位置
            if (userApps.size() + 1 == position) {
                TextView tv = (TextView) View.inflate(context, R.layout.item_software_manager_type_label_lv, null);
                tv.setText(getString(R.string.system_app) + "(" + systemApps.size() + ")");
                return tv;
            }

            AppItem item = null;
            View view = null;

            if (null == convertView || convertView instanceof TextView) {

                view = View.inflate(context, R.layout.item_software_manager_app_lv, null);

                item = new AppItem();
                view.setTag(item);
                //绑定视图
                item.tvTitle =  view.findViewById(R.id.tv_title);
                item.tvSize =  view.findViewById(R.id.tv_size);
                item.ivIcon =  view.findViewById(R.id.iv_icon);
                item.cb =  view.findViewById(R.id.cb);

            } else {

                view = convertView;
                item = (AppItem) view.getTag();
            }

            AppInfoBean  bean = this.getItem(position);


            item.ivIcon.setImageDrawable(bean.getIcon());
            item.tvTitle.setText(bean.getName());
            item.tvSize.setText(Formatter.formatFileSize(context, bean.getSize()));

            // 如果手机root过则重设
            if (!bean.isSystemApp() || root) {
                item.cb.setEnabled(true);
                item.cb.setChecked(checkeds.get(position));

                item.cb.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CheckBox cb = (CheckBox) v;
                        checkeds.set(position, cb.isChecked());
                        System.out.println("position:" + position + " isChecked:" + cb.isChecked());
                    }
                });
            } else {
                item.cb.setEnabled(false);
                item.cb.setChecked(false);//如果没有root则无法卸载
            }

            return view;
        }


        @Override
        public AppInfoBean getItem(int position) {
            AppInfoBean  bean = null;

            if (0 == position || userApps.size() + 1 == position)
                return bean;

            if (position < userApps.size() + 1) {

                bean = userApps.get(position - 1);
            } else {

                bean = systemApps.get(position - userApps.size() - 2);
            }
            return bean;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }
    }

    /**
     * menu click event
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.m_select_all:
                selectAllItem();
                break;
            case R.id.m_cancel_all:
                cancelAllItem();
                break;
            case R.id.m_uninstall:
                onUninstallSelectedApp();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 卸载选中所有应用
     */
    private void onUninstallSelectedApp() {
        // create dialog
        new AlertDialog.Builder(this)
                .setTitle(R.string.tips)
                .setMessage(R.string.message_uninstall_app)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //uninstallSelectedApp();
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    /**
     * 进入系统详情卸载
     */
//    private void uninstallSelectedApp() {
//        for (int i = 1; i < checkeds.size(); i++) {
//            if (!checkeds.get(i)) {
//                continue;
//            }
//            //获取应用信息
//            AppInfo app = (AppInfo) lvApp.getItemAtPosition(i);
//            if (null == app)
//                continue;
//            System.out.println(app.getName());
//            System.out.println(app.getApkPath());
//            // root 系统应用
//            if (app.isSystemApp() && root) {
//
//                try {
//                    if (!RootTools.isAccessGiven())
//                        continue;
//                    //改变系统目录访问权限
//                    RootTools.sendShell("mount -o remount rw /system", 10000);
//                    RootTools.sendShell("rm -r " + app.getApkPath(), 10000);
//                    //改变回权限
//                    RootTools.sendShell("mount -o remount r /system", 10000);
//                } catch (TimeoutException e) {
//                    e.printStackTrace();
//                } catch (RootToolsException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                continue;
//            }
//
//            Intent intent = new Intent(Intent.ACTION_DELETE, Uri.parse("package:" + app.getPackageName()));
//            startActivity(intent);
//        }
//
//    }

    /**
     * 取消全选
     */
    private void cancelAllItem() {
        for (int i = 1; i < checkeds.size(); i++) {
            checkeds.set(i, false);
        }
        // refresh ui
        adapter.notifyDataSetChanged();
    }

    /**
     * select all ListView item
     */
    private void selectAllItem() {

        if (root) {

            for (int i = 1; i < checkeds.size(); i++) {
                checkeds.set(i, true);
            }
        } else {

            for (int i = 1; i <= userApps.size(); i++) {
                checkeds.set(i, true);
            }
        }

        adapter.notifyDataSetChanged();
    }


    private int getCheckedItemCount() {
        int count = 0;
        for (int i = 1; i < checkeds.size(); i++) {
            if (i == userApps.size() + 1)
                continue;
            if (checkeds.get(i))
                count++;
        }
        return count;
    }

    /**
     *menu的item
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem uninstallItem = menu.findItem(R.id.m_uninstall);
        MenuItem selectAllItem = menu.findItem(R.id.m_select_all);
        MenuItem cancelAllItem = menu.findItem(R.id.m_cancel_all);
        int checkedCount = getCheckedItemCount();
        if (0 == checkedCount) {
            uninstallItem.setEnabled(false);
            cancelAllItem.setEnabled(false);
        } else {
            uninstallItem.setEnabled(true);
            cancelAllItem.setEnabled(true);
        }
        selectAllItem.setEnabled(true);
        if (checkedCount == checkeds.size()) {
            selectAllItem.setEnabled(false);
        } else {
            selectAllItem.setEnabled(true);
        }
        return true;
    }
}


class AppItem {
    ImageView ivIcon;
    TextView tvTitle;
    TextView tvSize;
    CheckBox cb;
}