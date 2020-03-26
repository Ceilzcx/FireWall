package com.example.firewall.activity;

import android.content.Context;
import android.net.TrafficStats;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.firewall.R;
import com.example.firewall.activity.base.BaseActivityUpEnable;
import com.example.firewall.bean.TrafficAppInfo;
import com.example.firewall.util.TrafficStatsUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 流量统计完整版
 * 应用的流量获取不到
 * 暂时放弃
 */
public class TrafficStatsActivity extends BaseActivityUpEnable {
    // view
    private TextView tvTotalTrafficStats;
    private TextView tvTotalTrafficStatsSum;
    private TextView tvMobileTrafficStats;
    private TextView tvMobileTrafficStatsSum;

    private ListView lvApp;
    private ProgressBar pbLoading;
    private TrafficAppAdapter adapter = new TrafficAppAdapter();

    private List<TrafficAppInfo> systemApps = new ArrayList<>();
    private List<TrafficAppInfo> userApps = new ArrayList<>();

    /**
     * construct method. set the action bar title
     */
    public TrafficStatsActivity() {
        super(R.string.traffic_stats);
    }

    /**
     * change the default call
     */
    @Override
    protected void init() {
        initView();
    }

    /**
     * 1
     */
    @Override
    protected void initView() {
        setContentView(R.layout.activity_traffic_stats);
        // bind view
        tvTotalTrafficStats = findViewById(R.id.tv_total_traffic_stats);
        tvTotalTrafficStatsSum = findViewById(R.id.tv_total_traffic_stats_sum);
        tvMobileTrafficStats = findViewById(R.id.tv_mobile_traffic_stats);
        tvMobileTrafficStatsSum = findViewById(R.id.tv_mobile_traffic_stats_sum);
        lvApp =  findViewById(R.id.lv_app);
        pbLoading =  findViewById(R.id.pb_loading);

        // 设置adapter
        lvApp.setAdapter(adapter);
    }

    private void initAppsInfo(final List<TrafficAppInfo> apps) {

        runOnUiThread(() -> {
            //添加前清除列表
            systemApps.clear();
            userApps.clear();
            // 添加到列表
            for (TrafficAppInfo  app : apps) {
                if (app.isSystemApp()) {
                    systemApps.add(app);
                } else {
                    userApps.add(app);
                }
            }

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
    }

    /**
     * init data
     */
    @Override
    protected void initData() {
        long totalRxBytes = TrafficStats.getTotalRxBytes();
        long totalTxBytes = TrafficStats.getTotalTxBytes();
        long mobileRxBytes = TrafficStats.getMobileRxBytes();
        long mobileTxBytes = TrafficStats.getMobileTxBytes();

        long totalBytes = totalRxBytes + totalTxBytes;
        long mobileBytes = mobileRxBytes + mobileTxBytes;

        tvTotalTrafficStatsSum.setText(getString(R.string.total_traffic_stats_sum, Formatter.formatFileSize(this, totalBytes)));
        tvMobileTrafficStatsSum.setText(getString(R.string.mobile_traffic_stats_sum, Formatter.formatFileSize(this, mobileBytes)));
        tvTotalTrafficStats.setText(getString(R.string.traffic_stats_upload_download, Formatter.formatFileSize(this, totalTxBytes), Formatter.formatFileSize(this, totalRxBytes)));
        tvMobileTrafficStats.setText(getString(R.string.traffic_stats_upload_download, Formatter.formatFileSize(this, mobileTxBytes), Formatter.formatFileSize(this, mobileRxBytes)));

        new Thread(() -> {
            initAppsInfo(TrafficStatsUtils.getTrafficAppInfos(this));
        }).start();
    }

    /**
     *
     */
    @Override
    protected void initEvent() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    private class TrafficAppAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            // 两个标签
            return systemApps.size() + userApps.size() + 2;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            Context context = TrafficStatsActivity.this;
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

            TrafficAppItem item = null;
            View view = null;

            if (null == convertView || convertView instanceof TextView) {

                view = View.inflate(context, R.layout.item_traffic_manager_app_lv, null);

                item = new TrafficAppItem();
                view.setTag(item);
                //绑定视图
                item.tvTitle = view.findViewById(R.id.tv_title);
                item.tvRx = view.findViewById(R.id.tv_rx);
                item.tvTx = view.findViewById(R.id.tv_tx);
                item.ivIcon =  view.findViewById(R.id.iv_icon);
            } else {
                view = convertView;
                item = (TrafficAppItem) view.getTag();
            }

            TrafficAppInfo info = this.getItem(position);


            item.ivIcon.setImageDrawable(info.getIcon());
            item.tvTitle.setText(info.getName());
            item.tvRx.setText("下载流量: "+info.getRxTraffic());
            item.tvTx.setText("上传流量: "+info.getTxTraffic());

            return view;
        }


        @Override
        public TrafficAppInfo getItem(int position) {
            TrafficAppInfo  info = null;

            if (0 == position || userApps.size() + 1 == position)
                return info;

            if (position < userApps.size() + 1) {

                info = userApps.get(position - 1);
            } else {

                info = systemApps.get(position - userApps.size() - 2);
            }
            return info;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }
    }

}

class TrafficAppItem {
    ImageView ivIcon;
    TextView tvTitle;
    TextView tvRx;
    TextView tvTx;
}