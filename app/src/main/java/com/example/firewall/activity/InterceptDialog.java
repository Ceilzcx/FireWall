package com.example.firewall.activity;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.example.firewall.R;
import com.example.firewall.adapter.CommunicateAdapter;
import com.example.firewall.bean.InterceptPhoneInfo;
import com.example.firewall.dao.InterceptDao;

import java.util.List;

public class InterceptDialog extends Dialog implements View.OnClickListener{
    private TextView titleTxt;
    private TextView submitTxt;
    private TextView cancelTxt;
    private EditText editText;
    private CheckBox checkBox_tel;
    private CheckBox checkBox_note;

    private Context mContext;
    private String title;

    private List<InterceptPhoneInfo> infos;
    private CommunicateAdapter adapter;

    public InterceptDialog(Context context) {
        super(context);
        this.mContext = context;
    }


    public InterceptDialog(Context context, int themeResId, List<InterceptPhoneInfo> infos, CommunicateAdapter adapter) {
        super(context, themeResId);
        this.mContext = context;
        this.infos = infos;
        this.adapter = adapter;
    }

    public void setTitle(String title){
        this.title = title;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_add_intercept_number);
        setCanceledOnTouchOutside(false);
        initView();
    }


    private void initView(){
        titleTxt = findViewById(R.id.title);
        submitTxt = findViewById(R.id.submit);
        submitTxt.setOnClickListener(this);
        cancelTxt = findViewById(R.id.cancel);
        cancelTxt.setOnClickListener(this);

        editText = findViewById(R.id.edit_intercept_number);
        checkBox_tel = findViewById(R.id.checkbox_tel);
        checkBox_note = findViewById(R.id.checkbox_note);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cancel:
                this.dismiss();
                break;
            case R.id.submit:
                InterceptDao dao = new InterceptDao(mContext);
                InterceptPhoneInfo info = new InterceptPhoneInfo();
                info.setNumber(editText.getText().toString());
                if (checkBox_note.isChecked() && checkBox_tel.isChecked())
                    info.setType(InterceptPhoneInfo.ALL_INTERCEPT);
                else if (!checkBox_note.isChecked() && checkBox_tel.isChecked())
                    info.setType(InterceptPhoneInfo.TELE_INTERCEPT);
                else if (checkBox_note.isChecked() && !checkBox_tel.isChecked())
                    info.setType(InterceptPhoneInfo.NOTE_INTERCEPT);
                dao.add(info);
                infos.add(info);
                adapter.notifyDataSetChanged();
                Log.e("number:", info.getNumber());
                this.dismiss();
                break;
        }
    }

}
