package com.example.firewall.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.firewall.R;
import com.example.firewall.adapter.PortAdapter;
import com.example.firewall.base.BaseActivityUpEnable;
import com.example.firewall.bean.Port;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class PortActivity extends BaseActivityUpEnable implements View.OnClickListener,AdapterView.OnItemClickListener {
    private List<Port> mData = null;
    private Context mContext;
    private PortAdapter mAdapter = null;
    private ListView list_port;
    private  ProgressDialog progressdlg;


    public PortActivity() {
        super(R.string.porta);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_port);
        mContext = PortActivity.this;
        list_port = findViewById(R.id.port_lv);
        mData = new LinkedList<>();
        showDialog();
        Runtime mRuntime = Runtime.getRuntime();
        try {
//Process中封装了返回的结果和执行错误的结果
            Process mProcess = mRuntime.exec("netstat -a ");
            BufferedReader mReader = new BufferedReader(new InputStreamReader(mProcess.getInputStream()));
            StringBuffer mRespBuff = new StringBuffer();
            char[] buff = new char[1024];
            String q=null;
            while((q = mReader.readLine()) != null){
                mRespBuff.append(q + "?");
            }
            if(mRespBuff!=null)
                Log.e("Port",String.valueOf(mRespBuff));
            List<String> re = new ArrayList();
            String a1 = null;
            String a2;
            String a3;
            String a4;
            String a5;
            String sum="";
            int i=0;
            /*for(i=70;i<mRespBuff.length()-8;i++) {
                if (mRespBuff.indexOf("?") != -1) {
                    int j = i;
                    int n;
                    int n1;
                    int n11;
                    int n12;
                    int n22;
                    int n21;
                    int n2;
                    a1 = mRespBuff.substring(i+1, i+5);
                    Log.e("mResult",a1);
                    n = mRespBuff.indexOf("?", i + 1);
                    if (a1.equals(" tcp")) {
                        //本地ip及端口
                        n1 = mRespBuff.indexOf(":", i);
                        n11 = mRespBuff.lastIndexOf(" ", n1);
                        n12 = mRespBuff.indexOf(" ", n1);
                        //目标ip及端口
                        n2 = mRespBuff.lastIndexOf(":", n);
                        n21 = mRespBuff.lastIndexOf(" ", n2);
                        n22 = mRespBuff.indexOf(" ", n2);
                        a2 = mRespBuff.substring(n11 + 1, n1);
                        a3 = mRespBuff.substring(n1 + 1, n12);
                        a4 = mRespBuff.substring(n21 + 1, n2);
                        a5 = mRespBuff.substring(n2 + 1, n22);
                        if (a2!=null) {
                            sum+= "协议为" + a1 +"\n"+ "本机ip为" + a2 + "本地端口号" + a3 + "目标ip为" + a4 + "目标端口号为" + a5+" \n";
                            mData.add(new Port(a1,a2,a3,a4,a5));
                            //  tv.setText(tv.getText().toString()+s);
                        }
                    }

                    if (a1.equals("tcp6")) {
                        //本地ip及端口
                        n1 = mRespBuff.indexOf("::ffff:", i);
                        n11 = mRespBuff.indexOf(":", n1+7);
                        n12 = mRespBuff.indexOf("::ffff:", n11);
                        n2 = mRespBuff.indexOf(":", n12+7);
                        n21 = mRespBuff.indexOf(" ", n2);
                        a2=null;
                        a3=null;
                        a4=null;
                        a5=null;
                        if(n1!=-1&&n11!=-1&&n12!=-1&&n2!=-1&&n21!=-1&&n1<n&&n21<n&&n2<n&&n11<n&&n12<n){
                            a2 = mRespBuff.substring(n1+7, n11);
                            a3 = mRespBuff.substring(n11+1, n12-1);
                            a4 = mRespBuff.substring(n12+ 7, n2);
                            a5 = mRespBuff.substring(n2 + 1, n21);
                        }
                        if (a2!=null) {
                            sum+= "协议为" + a1 +"\n"+  "本机ip为" + a2 + "本地端口号" + a3 + "目标ip为" + a4 + "目标端口号为" + a5+" \n";
                            mData.add(new Port(a1,a2,a3,a4,a5));
                            //  tv.setText(tv.getText().toString()+s);
                        }
                    }
                    if (a1.equals("udp6")) {
                        //本地ip及端口
                        n1 = mRespBuff.indexOf(":::", i);
                        n11 = mRespBuff.indexOf(" ", n1+3);
                        a2 = mRespBuff.substring(n1 + 3, n11);

                        if (a2!=null) {
                            sum+= "协议为" + a1 +"\n"+  "本机端口号" + a2+" \n";
                            mData.add(new Port(a1,a2));
                            //  tv.setText(tv.getText().toString()+s);
                        }
                    }

                }
            }*/
            try {
                while(i<mRespBuff.length()&&i!=-1){
                    int j;
                    if ((j=mRespBuff.indexOf("?",i))!=-1){
                        i=j+1;
                        int n;
                        int n1;
                        int n11;
                        int n12;
                        int n22;
                        int n21;
                        int n2;
                        if(i+4<mRespBuff.length()) {
                            a1 = mRespBuff.substring(i, i + 4);
                        }
                        n = i;
                        if (a1.equals("tcp ")) {
                            //本地ip及端口
                            n1 = mRespBuff.indexOf(":", i);
                            n11 = mRespBuff.lastIndexOf(" ", n1);
                            n12 = mRespBuff.indexOf(" ", n1);
                            //目标ip及端口
                            n2 = mRespBuff.indexOf(":", n1+1);
                            n21 = mRespBuff.lastIndexOf(" ", n2);
                            n22 = mRespBuff.indexOf(" ", n2);

                            a2 = mRespBuff.substring(n11 + 1, n1);
                            a3 = mRespBuff.substring(n1 + 1, n12);

                            a4 = mRespBuff.substring(n21 + 1, n2);
                            a5 = mRespBuff.substring(n2 + 1, n22);
                            //Log.e("协议",a2+" "+a3+" "+a4+" "+a5);
                            if (a2!=null) {
                                sum+= "协议为" + a1 +"\n"+ "本机ip为" + a2 + "本地端口号" + a3 + "目标ip为" + a4 + "目标端口号为" + a5+" \n";
                                mData.add(new Port(a1,a2,a3,a4,a5));
                                //  tv.setText(tv.getText().toString()+s);
                            }
                        }
                        if (a1.equals("tcp6")) {
                            //本地ip及端口
                            int x=mRespBuff.indexOf("?", i);
                            n1 = mRespBuff.indexOf(":", i);
                            n11 = mRespBuff.lastIndexOf(" ", n1);
                            n12 = mRespBuff.indexOf(" ", n1);
                            //目标ip及端口
                            n2 = mRespBuff.lastIndexOf(":", x);
                            n21 = mRespBuff.lastIndexOf(" ", n2);
                            n22 = mRespBuff.indexOf(" ", n2);

                            a2 = mRespBuff.substring(n11 + 1, n1);
                            a3 = mRespBuff.substring(n1 + 1, n12);

                            a4 = mRespBuff.substring(n21 + 1, n2);
                            a5 = mRespBuff.substring(n2 + 1, n22);
                            //Log.e("协议",a2+" "+a3+" "+a4+" "+a5);
                            if (a2!=null) {
                                sum+= "协议为" + a1 +"\n"+ "本机ip为" + a2 + "本地端口号" + a3 + "目标ip为" + a4 + "目标端口号为" + a5+" \n";
                                mData.add(new Port(a1,a2,a3,a4,a5));
                                //  tv.setText(tv.getText().toString()+s);
                            }
                        }
                        if (a1.equals("udp ")) {
                            //本地ip及端口
                            n1 = mRespBuff.indexOf(":", i);
                            n11 = mRespBuff.lastIndexOf(" ", n1);
                            n12 = mRespBuff.indexOf(" ", n1);
                            //目标ip及端口
                            n2 = mRespBuff.indexOf(":", n1+1);
                            n21 = mRespBuff.lastIndexOf(" ", n2);
                            n22 = mRespBuff.indexOf(" ", n2);

                            a2 = mRespBuff.substring(n11 + 1, n1);
                            a3 = mRespBuff.substring(n1 + 1, n12);

                            a4 = mRespBuff.substring(n21 + 1, n2);
                            a5 = mRespBuff.substring(n2 + 1, n22);
                            //Log.e("协议",a2+" "+a3+" "+a4+" "+a5);
                            if (a2!=null) {
                                sum+= "协议为" + a1 +"\n"+ "本机ip为" + a2 + "本地端口号" + a3 + "目标ip为" + a4 + "目标端口号为" + a5+" \n";
                                mData.add(new Port(a1,a2,a3,a4,a5));
                                //  tv.setText(tv.getText().toString()+s);
                            }
                        }
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
                Log.d("errroor",String.valueOf(i));
            }


            mReader.close();
            // tv.setText(mRespBuff.toString());
        } catch (IOException e) {
// TODO Auto-generated catch block
            e.printStackTrace();
        }

        mAdapter = new PortAdapter((LinkedList<Port>) mData, mContext);
        list_port.setAdapter(mAdapter);
        progressdlg.cancel();
    }


    @Override
    public void onClick(View v) {

    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        TextView tv = (TextView) arg1.findViewById(R.id.ssid);
        if (!TextUtils.isEmpty(tv.getText().toString())) {
            Intent in = new Intent(PortActivity.this, WifiConnectActivity.class);
            in.putExtra("ssid", tv.getText().toString());
            startActivity(in);
        }
    }
    protected  void initEvent(){};
    protected  void initData(){};
    protected  void initView(){};

    private void showDialog() {
        progressdlg = new ProgressDialog(this);
        progressdlg.setCanceledOnTouchOutside(false);
        progressdlg.setCancelable(false);
        progressdlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressdlg.setMessage(getString(R.string.wait_moment));
        progressdlg.show();
    }






}



