package com.example.firewall.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.firewall.R;
import com.example.firewall.bean.InterceptPhoneInfo;
import com.example.firewall.dao.InterceptDao;

import java.util.List;

/**
 * 人工添加的拦截信息
 */
public class CommunicateAdapter extends RecyclerView.Adapter<CommunicateAdapter.ViewHolder> {

    private List<InterceptPhoneInfo> infos;
    private InterceptDao dao;

    public CommunicateAdapter(List<InterceptPhoneInfo> infos, Context context){
        this.infos = infos;
        dao = new InterceptDao(context);
    }

    @NonNull
    @Override
    public CommunicateAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_commuicate, parent, false);
        return new CommunicateAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommunicateAdapter.ViewHolder holder, int position) {
        InterceptPhoneInfo info = infos.get(position);
        if (info.getNumber() != null)
            holder.textView_number.setText(info.getNumber());
        if (info.getType() == InterceptPhoneInfo.ALL_INTERCEPT)
            holder.textView_type.setText("电话拦截\t\t\t短信拦截");
        else if (info.getType() == InterceptPhoneInfo.NOTE_INTERCEPT)
            holder.textView_type.setText("短信拦截");
        else if (info.getType() == InterceptPhoneInfo.TELE_INTERCEPT)
            holder.textView_type.setText("电话拦截");
        holder.imageView.setOnClickListener(v -> {
            dao.delete(holder.textView_number.getText().toString());
        });
    }

    @Override
    public int getItemCount() {
        return infos.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView textView_number;
        TextView textView_type;
        ImageView imageView;

        ViewHolder(View view){
            super(view);
            textView_number = view.findViewById(R.id.text_phone_number);
            textView_type = view.findViewById(R.id.text_intercept_type);
            imageView = view.findViewById(R.id.communicate_delete);
        }
    }

}
