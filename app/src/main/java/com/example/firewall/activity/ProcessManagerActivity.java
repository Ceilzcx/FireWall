package com.example.firewall.activity;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.format.Formatter;
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
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.firewall.R;
import com.example.firewall.base.BaseActivityUpEnableWithMenu;
import com.example.firewall.bean.AppItem;
import com.example.firewall.bean.ProcessInfo;
import com.example.firewall.engine.ProcessManagerEngine;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.jaredrummler.android.processes.AndroidProcesses;
import com.jaredrummler.android.processes.models.AndroidAppProcess;

public class ProcessManagerActivity extends BaseActivityUpEnableWithMenu {
    // view
    private TextView tvTotalMemory;
    private TextView tvFreeMemory;
    private TextView tvTypeLabel;
    private ListView lvApp;
    private ProgressBar pbLoading;
    // data
    private List<ProcessInfo> systemApps = new CopyOnWriteArrayList<>();//用于并发
    private List<ProcessInfo> userApps = new CopyOnWriteArrayList<>();
    private List<Boolean> checkeds = new ArrayList<>();// 代表选择的item
    private AppAdapter adapter = new AppAdapter();

    private Thread initDataThread;

    /**
     * 标题
     */
    public ProcessManagerActivity() {
        super(R.string.process_manager, R.menu.menu_process_manager);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
        initEvent();
    }

    /**
     * 1
     */

    protected void initView() {
        setContentView(R.layout.activity_process_manager);
        // bind view
        tvTotalMemory = (TextView) findViewById(R.id.tv_total_memory);
        tvFreeMemory = (TextView) findViewById(R.id.tv_free_memory);
        tvTypeLabel = (TextView) findViewById(R.id.tv_type_label);
        lvApp = (ListView) findViewById(R.id.lv_app);
        pbLoading = (ProgressBar) findViewById(R.id.pb_loading);
        // set adapter
        lvApp.setAdapter(adapter);
    }

    /**
     * 2
     */

    protected void initData() {

        String totalMemory = ProcessManagerEngine.getTotalMemory2(ProcessManagerActivity.this);
        String freeMemory =ProcessManagerEngine.getAvailMemory(ProcessManagerActivity.this);
        //long freeMemory = ProcessManagerEngine.getFreeMemory(this);
        tvTotalMemory.setText(getString(R.string.total_memory)+totalMemory);
        tvFreeMemory.setText(getString(R.string.free_memory)+freeMemory);
        //tvTotalMemory.setText(getString(R.string.total_memory) + Formatter.formatFileSize(this, totalMemory));
        //tvFreeMemory.setText(getString(R.string.free_memory) + Formatter.formatFileSize(this, freeMemory));

        //线程存在，不需要新线程
        if (null != initDataThread && initDataThread.isAlive()) {
            return;
        }

        initDataThread = new Thread() {
            @Override
            public void run() {
                // 获取app信息
                List<ProcessInfo> infos = ProcessManagerEngine.getRunningProcessesInfo(ProcessManagerActivity.this);
                // 刷新ui和数据
                initAppsInfo(infos);
            }
        };

        initDataThread.start();
    }

    /**
     * 加载应用信息
     *
     * @param infos
     */
    private void initAppsInfo(final List<ProcessInfo> infos) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //清空
                userApps.clear();
                systemApps.clear();
                checkeds.clear();

                for (ProcessInfo info : infos) {
                    if (info.isSystemApp()) {
                        systemApps.add(info);
                    } else {
                        userApps.add(info);
                    }
                    checkeds.add(false);
                }

                checkeds.add(false);
                checkeds.add(false);

                onDataChanged();
            }
        });
    }

    /**
     * 通知Listview和进度条刷新
     */
    private void onDataChanged() {
        // 刷新listview
        adapter.notifyDataSetChanged();
        // 隐藏进度条
        pbLoading.setVisibility(View.GONE);
        // 显示标签类型
        tvTypeLabel.setVisibility(View.VISIBLE);
    }

    /**
     * 3
     */

    protected void initEvent() {
        // set scroll listener to implement type label change text
        lvApp.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                // prevent the data haven't loaded
                if (0 == userApps.size() || 0 == systemApps.size())
                    return;
                // the system app label is at first or above the screen
                if (firstVisibleItem >= userApps.size() + 1) {
                    // change the type label to system app
                    tvTypeLabel.setText(getString(R.string.system_app) + "(" + systemApps.size() + ")");
                } else {
                    // change the type label to user app
                    tvTypeLabel.setText(getString(R.string.user_app) + "(" + userApps.size() + ")");
                }
            }
        });
        // set on click listener
        lvApp.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ProcessInfo bean = (ProcessInfo) lvApp.getItemAtPosition(position);
//                System.out.println(bean.getName());
                Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS",
                        Uri.parse("package:" + bean.getPackageName()));
                startActivity(intent);
            }
        });
    }

    /**
     * cancel all ListView item
     */
    private void cancelAllItem() {
        for (int i = 1; i < checkeds.size(); i++) {
            checkeds.set(i, false);
        }
        // 刷新 ui
        adapter.notifyDataSetChanged();
    }

    /**
     * select all ListView item
     */
    private void selectAllItem() {
        // set checked
        for (int i = 1; i < checkeds.size(); i++) {
            // prevent empty app or this app be selected
            ProcessInfo bean = adapter.getItem(i);
            if (null == bean || getPackageName().equals(bean.getPackageName()))
                continue;
            checkeds.set(i, true);
        }
        // refresh ui
        adapter.notifyDataSetChanged();
    }

    /**
     * @return the count of checked item
     */
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
     * Will be call before shown
     * Use it to enable or disable item
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // find items
        MenuItem killProcess = menu.findItem(R.id.m_kill_process);
        MenuItem selectAllItem = menu.findItem(R.id.m_select_all);
        MenuItem cancelAllItem = menu.findItem(R.id.m_cancel_all);
        // get checked item count
        int checkedCount = getCheckedItemCount();
        if (0 == checkedCount) {
            // no checked item, disable it
            killProcess.setEnabled(false);
            cancelAllItem.setEnabled(false);
        } else {
            // enable it
            killProcess.setEnabled(true);
            cancelAllItem.setEnabled(true);
        }
        // if all item are checked, no need enable select all
        if (checkedCount == checkeds.size()) {
            selectAllItem.setEnabled(false);
        } else {
            selectAllItem.setEnabled(true);
        }

        return true;
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
            case R.id.m_kill_process:
                onKillSelectedProcesses();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * query user whether kill selected process
     */
    private void onKillSelectedProcesses() {
        // create dialog
        new AlertDialog.Builder(ProcessManagerActivity.this)
                .setTitle(R.string.tips)
                .setMessage(R.string.message_kill_process)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        killSelectedProcesses();
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    /**
     * kill all checked processes
     */
    private void killSelectedProcesses() {
        long totalReleaseMemory = 0;
        int releaseCount = 0;

        // get activity manager
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        // kill checked process
        // delete from last position
        for (int i = checkeds.size() - 1; i > 0; i--) {
            if (!checkeds.get(i))
                continue;
            // get bean
            ProcessInfo bean = adapter.getItem(i);
            if (null == bean)
                continue;
            System.out.println("position:" + i + " packageName:" + bean.getPackageName());
            // kill process
            am.killBackgroundProcesses(bean.getPackageName());
            // add count
            releaseCount++;
            totalReleaseMemory += bean.getMemory();
            // remove app from list. Because the list is CopyOnWriteArrayList. So it can remove directly.
            if (bean.isSystemApp()) {
                systemApps.remove(bean);
            } else {
                userApps.remove(bean);
            }
            checkeds.remove(i);
        }
        // notify refresh ListView
        adapter.notifyDataSetChanged();

        // show tips
        String tips = getString(R.string.tips_kill_process, releaseCount, Formatter.formatFileSize(this, totalReleaseMemory));
        Toast.makeText(this, tips, Toast.LENGTH_SHORT).show();

    }

    /**
     * app adapter
     */
    private class AppAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            // +2: because of the two tips label
            return systemApps.size() + userApps.size() + 2;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            Context context = ProcessManagerActivity.this;
            // the first position is user label
            if (0 == position) {
                TextView tv = (TextView) View.inflate(context, R.layout.item_software_manager_type_label_lv, null);
                tv.setText(getString(R.string.user_app) + "(" + userApps.size() + ")");
                return tv;
            }
            // the userApps.size() + 1 position is system label
            if (userApps.size() + 1 == position) {
                TextView tv = (TextView) View.inflate(context, R.layout.item_software_manager_type_label_lv, null);
                tv.setText(getString(R.string.system_app) + "(" + systemApps.size() + ")");
                return tv;
            }
            // other position is app view
            AppItem item = null;
            View view = null;
            // check cache
            if (null == convertView || convertView instanceof TextView) {
                // no cache, create a new view
                view = View.inflate(context, R.layout.item_software_manager_app_lv, null);
                // create item and cache it
                item = new AppItem();
                view.setTag(item);
                // bind child view
                item.tvTitle = (TextView) view.findViewById(R.id.tv_title);
                item.tvSize = (TextView) view.findViewById(R.id.tv_size);
                item.ivIcon = (ImageView) view.findViewById(R.id.iv_icon);
                item.cb = (CheckBox) view.findViewById(R.id.cb);

            } else {
                // have cache and the view is not TextView
                view = convertView;
                item = (AppItem) view.getTag();
            }
            // get AppBean
            ProcessInfo bean = this.getItem(position);

            // set value
            item.ivIcon.setImageDrawable(bean.getIcon());
            item.tvTitle.setText(bean.getAppName());
            item.tvSize.setText(Formatter.formatFileSize(context, bean.getMemory()));

            item.cb.setChecked(checkeds.get(position));// update checked
            // set checkbox listener, can't use checked change event, it would effect setChecked
            item.cb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CheckBox cb = (CheckBox) v;
                    checkeds.set(position, cb.isChecked());
                    System.out.println("position:" + position + " isChecked:" + cb.isChecked());
                }
            });

            return view;
        }

        /**
         * the the item bean
         *
         * @param position
         * @return return item bean if success, null otherwise
         */
        @Override
        public ProcessInfo getItem(int position) {
            ProcessInfo bean = null;
            // the type label, return null
            if (0 == position || userApps.size() + 1 == position)
                return bean;

            if (position < userApps.size() + 1) {
                // user apps
                bean = userApps.get(position - 1);// for example, position is 4, then the bean position should be 3
            } else {
                // system apps
                /*
                0 user app
                1 up1
                2 up2
                3 system app
                4 sp1
                5 sp2
                 */
                bean = systemApps.get(position - userApps.size() - 2);// for example, user apps count is 2, position is 5, then bean position is 1
            }
            return bean;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }
    }
}