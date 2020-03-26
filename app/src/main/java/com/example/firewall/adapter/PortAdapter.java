package com.example.firewall.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.firewall.R;
import com.example.firewall.bean.Port;

import java.util.LinkedList;

public class PortAdapter extends BaseAdapter {

    private LinkedList<Port> mData;
    private Context mContext;

    public PortAdapter(LinkedList<Port> mData, Context mContext) {
        this.mData = mData;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(mContext).inflate(R.layout.item_list_port,parent,false);
        TextView txt_aName = (TextView) convertView.findViewById(R.id.txt_aName);
        TextView txt_aSpeak = (TextView) convertView.findViewById(R.id.txt_aSpeak);
        txt_aName.setText("协议为 "+mData.get(position).getXy()+"\n"+" 本地ip "+mData.get(position).getIp1()+" 本地端口号 "+mData.get(position).getP1());
        txt_aSpeak.setText(" 目标ip "+mData.get(position).getIp2()+" 目标端口 "+mData.get(position).getP2());
        return convertView;
    }
}