package com.example.firewall.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firewall.R;
import com.example.firewall.bean.InterceptPhoneInfo;
import com.example.firewall.bean.PhoneContactInfo;

import java.util.ArrayList;
import java.util.List;

public class PhoneContactAdapter extends RecyclerView.Adapter<PhoneContactAdapter.ViewHolder> {

    private List<PhoneContactInfo> phoneContactInfo;
    private List<InterceptPhoneInfo> checkInfos;

    public PhoneContactAdapter(List<PhoneContactInfo> phoneInfo){
        this.phoneContactInfo = phoneInfo;
        checkInfos = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mobile_contact, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PhoneContactInfo info = phoneContactInfo.get(position);
        if (info.getUsername() != null)
            holder.textView_username.setText(info.getUsername());
        if (info.getNumber() != null)
            holder.textView_number.setText(info.getNumber());
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
        return phoneContactInfo.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        CheckBox checkBox;
        TextView textView_username;
        TextView textView_number;

        ViewHolder(View view){
            super(view);
            checkBox = view.findViewById(R.id.checkbox);
            textView_username = view.findViewById(R.id.text_phone_username);
            textView_number = view.findViewById(R.id.text_phone_number);
        }
    }

    public List<InterceptPhoneInfo> getCheckInfos() {
        return checkInfos;
    }
}
