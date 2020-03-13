package com.example.firewall.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firewall.R;
import com.example.firewall.bean.PhoneContactInfo;

import java.util.List;

public class PhoneContactAdapter extends RecyclerView.Adapter<PhoneContactAdapter.ViewHolder> {

    private List<PhoneContactInfo> phoneContactInfo;

    public PhoneContactAdapter(List<PhoneContactInfo> phoneInfo){
        this.phoneContactInfo = phoneInfo;
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
}
