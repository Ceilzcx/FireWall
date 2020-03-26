package com.example.firewall.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.firewall.R;
import com.example.firewall.activity.base.BaseActivityUpEnable;
import com.example.firewall.engine.ProcessManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**

 */
public class MActivity extends BaseActivityUpEnable implements AdapterView.OnItemClickListener {
    private ListView lv;
    private File[] files;
    ArrayList name;

    public MActivity() {
        super(R.string.packa);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.my);
        lv=findViewById(R.id.pack_lv);
        name = new ArrayList();
        start_button();
        stop_button();
        lv.setOnItemClickListener(this);
        File path = new File("/sdcard/aaa");
        File[] files = path.listFiles();
        getFileName(files);
    }


    private void getFileName(File[] files) {
        if (files != null) {    // 先判断目录是否为空，否则会报空指针
            for (File file : files) {
                if (file.isDirectory()) {
                    Log.i("zeng", "若是文件目录。继续读1" + file.getName().toString()
                            + file.getPath().toString());

                    getFileName(file.listFiles());
                    Log.i("zeng", "若是文件目录。继续读2" + file.getName().toString()
                            + file.getPath().toString());
                } else {
                    String fileName = file.getName();
                    if (fileName.endsWith(".txt")) {
                        HashMap map = new HashMap();
                        String s = fileName.substring(0,
                                fileName.lastIndexOf(".")).toString();
                        Log.i("zeng", "文件名txt：：   " + s);
                        map.put("Name", fileName.substring(0,
                                fileName.lastIndexOf(".")));

                        name.add(map);

                    }
                }
            }
        }

        SimpleAdapter adapter = new SimpleAdapter(this, name, R.layout.sd_list,
                new String[] { "Name" }, new int[] { R.id.txt_tv });
        lv.setAdapter(adapter);
    }



    private void start_button(){
        Button button = findViewById(R.id.start_button);
        button.setOnClickListener(start_button_listener);

    }
    private void stop_button(){
        Button button = findViewById(R.id.stop_button);
        button.setOnClickListener(stop_button_listener);

    }

    private Button.OnClickListener start_button_listener = v -> {
        Toast.makeText(MActivity.this, "开始!", Toast.LENGTH_LONG).show();
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
        String hehe = dateFormat.format( now );
        int o;
        try {
            //String[] cmd = {"su","-c","/data/local/tcpdump -X -vv -l > /sdcard/aaa/"+hehe+".txt"};

            Runtime.getRuntime().exec("chmod 777 tcpdump");
            Process process = Runtime.getRuntime().exec("tcpdump");
            InputStreamReader isr = new InputStreamReader(process.getInputStream());
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null)
                Log.e("tcpdump", line);
        } catch (IOException e) {
// TODO Auto-generated catch block
            e.printStackTrace();
        }

    };
    private Button.OnClickListener stop_button_listener = v -> {
        Toast.makeText(MActivity.this, "结束!", Toast.LENGTH_LONG).show();
        try {
            File path = new File("/sdcard/aaa");
            File[] files = path.listFiles();
            getFileName(files);

            Runtime.getRuntime().exec("killall tcpdump");
        } catch (IOException e) {
// TODO Auto-generated catch block
            e.printStackTrace();
        }
    };



    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        TextView tv = (TextView) arg1.findViewById(R.id.txt_tv);
        if (!TextUtils.isEmpty(tv.getText().toString())) {
            Intent in = new Intent(MActivity.this, ViewTextActivity.class);
            in.putExtra("ssid", tv.getText().toString());
            startActivity(in);
        }
    }
    protected  void initEvent(){};
    protected  void initData(){};
    protected  void initView(){};
}
