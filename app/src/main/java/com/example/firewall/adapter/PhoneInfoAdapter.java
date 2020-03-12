package com.example.firewall.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firewall.R;

import java.util.List;

public class PhoneInfoAdapter extends RecyclerView.Adapter<PhoneInfoAdapter.ViewHolder> {
    private List<String> phoneInfo;

    public PhoneInfoAdapter(List<String> phoneInfo){
        this.phoneInfo = phoneInfo;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_phone_info, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.textView_info.setText(phoneInfo.get(position));
    }

    @Override
    public int getItemCount() {
        return phoneInfo.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView textView_info;

        ViewHolder(View view){
            super(view);
            textView_info = view.findViewById(R.id.text_phone_info);
        }
    }

}
