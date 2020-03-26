package com.example.firewall.adapter;

import android.content.Context;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firewall.R;
import com.example.firewall.bean.TrafficModeInfo;

import java.util.List;

public class TrafficAdapter extends RecyclerView.Adapter<TrafficAdapter.ViewHolder> {
    private List<TrafficModeInfo> modeInfos;
    private Context context;

    public TrafficAdapter(Context context, List<TrafficModeInfo> modeInfos){
        this.modeInfos = modeInfos;
        this.context = context;
    }

    @NonNull
    @Override
    public TrafficAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_traffic_simple, parent, false);
        return new TrafficAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrafficAdapter.ViewHolder holder, int position) {
        TrafficModeInfo info = modeInfos.get(position);
        String ifname = info.getIfname();
        if (ifname.contains("softap"))
            holder.tv_ifname.setText("SoftAp");
        else if(ifname.contains("rmnet_data"))
            holder.tv_ifname.setText("4G网络"+ifname.subSequence(ifname.length()-1, ifname.length()));
        else if(ifname.contains("wlan"))
            holder.tv_ifname.setText("无线网络"+ifname.subSequence(ifname.length()-1, ifname.length()));
        else if(ifname.equals("lo"))
            holder.tv_ifname.setText("本地网络");
        else if (ifname.contains("dummy"))
            holder.tv_ifname.setText("dummy网卡");
        holder.tv_rx_packets.setText("接收数据包: \n"+info.getRx_packets());
        holder.tv_rx_bytes.setText("接收数据大小: \n"+ Formatter.formatFileSize(context,info.getRx_bytes()));
        holder.tv_tx_bytes.setText("发送数据包: \n"+info.getTx_bytes());
        holder.tv_tx_packets.setText("发送数据大小: \n"+Formatter.formatFileSize(context,info.getTx_packets()));

    }

    @Override
    public int getItemCount() {
        return modeInfos.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView tv_ifname;
        TextView tv_rx_bytes;
        TextView tv_tx_bytes;
        TextView tv_tx_packets;
        TextView tv_rx_packets;

        ViewHolder(View view){
            super(view);
            tv_ifname = view.findViewById(R.id.tv_ifname);
            tv_rx_bytes = view.findViewById(R.id.tv_rx_bytes);
            tv_tx_bytes = view.findViewById(R.id.tv_tx_bytes);
            tv_rx_packets = view.findViewById(R.id.tv_rx_packets);
            tv_tx_packets = view.findViewById(R.id.tv_tx_packets);
        }
    }
}
