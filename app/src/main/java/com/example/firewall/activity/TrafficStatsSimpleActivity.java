package com.example.firewall.activity;

import android.net.TrafficStats;
import android.os.Bundle;
import android.text.format.Formatter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firewall.R;
import com.example.firewall.adapter.TrafficAdapter;
import com.example.firewall.bean.TrafficModeInfo;
import com.example.firewall.util.TrafficStatsUtils;

import java.util.List;

public class TrafficStatsSimpleActivity extends AppCompatActivity {
    private TextView tvTotalTrafficStats;
    private TextView tvTotalTrafficStatsSum;
    private TextView tvMobileTrafficStats;
    private TextView tvMobileTrafficStatsSum;
    private RecyclerView rvMode;
    private TrafficAdapter adapter;
    private List<TrafficModeInfo> infos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traffic_simple);

        initView();
        initData();

    }

    private void initView(){
        tvTotalTrafficStats = findViewById(R.id.tv_total_traffic_stats);
        tvTotalTrafficStatsSum = findViewById(R.id.tv_total_traffic_stats_sum);
        tvMobileTrafficStats = findViewById(R.id.tv_mobile_traffic_stats);
        tvMobileTrafficStatsSum = findViewById(R.id.tv_mobile_traffic_stats_sum);
        rvMode = findViewById(R.id.rv_mode);
    }

    private void initData() {
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

        infos = TrafficStatsUtils.getTrafficMode();
        adapter = new TrafficAdapter(this, infos);
        rvMode.setLayoutManager(new LinearLayoutManager(this));
        rvMode.setAdapter(adapter);
    }

}


