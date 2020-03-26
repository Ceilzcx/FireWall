package com.example.firewall.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firewall.R;
import com.example.firewall.bean.InterceptPhoneInfo;
import com.example.firewall.bean.PhoneCallLogInfo;

import java.util.ArrayList;
import java.util.List;

public class PhoneCallLogAdapter extends RecyclerView.Adapter<PhoneCallLogAdapter.ViewHolder> {

    private List<PhoneCallLogInfo> infos;
    private List<InterceptPhoneInfo> checkInfos;

    public PhoneCallLogAdapter(List<PhoneCallLogInfo> infos){
        this.infos = infos;
        checkInfos = new ArrayList<>();
    }

    @NonNull
    @Override
    public PhoneCallLogAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mobile_calllog, parent, false);
        return new PhoneCallLogAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhoneCallLogAdapter.ViewHolder holder, int position) {
        PhoneCallLogInfo info = infos.get(position);
        if (info.getNumber() != null)
            holder.textView_number.setText(info.getNumber());
        if (info.getDate() != null)
            holder.textView_date.setText(info.getDate());
        if (info.getType() == PhoneCallLogInfo.CALLIN)
            holder.textView_type.setText("呼入");
        else if (info.getType() == PhoneCallLogInfo.CALLOUT)
            holder.textView_type.setText("呼出");
        else if (info.getType() == PhoneCallLogInfo.UNCALLED)
            holder.textView_type.setText("未接");
        else
            holder.textView_type.setText("未知");
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            String number = holder.textView_number.getText().toString();
            if (isChecked){
                InterceptPhoneInfo checkInfo = new InterceptPhoneInfo();
                checkInfo.setNumber(number);
                checkInfos.add(checkInfo);
            }else {
                //在列表中,删除
                int index = -1;
                for (int i = 0; i < checkInfos.size(); i++) {
                    if (checkInfos.get(i).getNumber().equals(number)){
                        index = i;
                    }
                }
                if (index != -1)
                    checkInfos.remove(index);
            }
        });
    }

    @Override
    public int getItemCount() {
        return infos.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        CheckBox checkBox;
        TextView textView_number;
        TextView textView_date;
        TextView textView_type;

        ViewHolder(View view){
            super(view);
            checkBox = view.findViewById(R.id.checkbox);
            textView_number = view.findViewById(R.id.text_phone_number);
            textView_date = view.findViewById(R.id.text_phone_date);
            textView_type = view.findViewById(R.id.text_phone_type);
        }
    }

    public List<InterceptPhoneInfo> getCheckInfos() {
        return checkInfos;
    }
}

