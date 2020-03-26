package com.example.firewall.activity;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;

import com.example.firewall.R;
import com.example.firewall.activity.base.BaseActivityUpEnable;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ViewTextActivity extends BaseActivityUpEnable {
    private String ssid;


    public ViewTextActivity() {
        super(R.string.packb);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        ssid = getIntent().getStringExtra("ssid");
        super.onCreate(savedInstanceState);



        ActionBar actionBar = getSupportActionBar();
        if(null != actionBar) {
            actionBar.setTitle("数据抓包详细");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        setTitle("ViewTextActivity");
        setContentView(R.layout.text);
        try {
            find_and_modify_text_view();
        } catch (IOException e) {
// TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    private void find_and_modify_text_view() throws IOException  {
        FileReader fr = new FileReader("/mnt/sdcard/aaa/"+ssid+".txt");
        BufferedReader br = new BufferedReader(fr);
        String str = null;
        TextView text_view = (TextView) findViewById(R.id.text_view);
        text_view.setMovementMethod(new ScrollingMovementMethod());
//CharSequence text_view_old = text_view.getText();
        while((str = br.readLine()) != null){
            str = br.readLine();
            text_view.append("\n"+str);
        }
    }
    protected  void initEvent(){};
    protected  void initData(){};
    protected  void initView(){};
}