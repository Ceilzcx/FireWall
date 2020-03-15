package com.example.firewall.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.firewall.R;
import com.example.firewall.base.BaseActivityUpEnable;
import com.example.firewall.bean.AppInfoBean;
import com.example.firewall.bean.VirusBean;
import com.example.firewall.constant.Constant;
import com.example.firewall.dao.AntivirusDao;
import com.example.firewall.engine.AppManagerEngine;
import com.example.firewall.util.EncryptionUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 使用金山毒霸数据库
 */
public class AntivirusActivity extends BaseActivityUpEnable {
    // 视图
    private ImageView ivScan;
    private TextView tvUseTime;
    private LinearLayout llLog;
    private ProgressBar pbProgress;

    // 数据
    private Thread initDataThread;
    private int useTime = 0;//用时
    private Timer timer;
    private int progress = 0;
    private boolean running;
    private ArrayList<VirusBean> viruses = new ArrayList<>();
    /**
     *构建方法 设置标题栏
     */
    public AntivirusActivity() {
        super(R.string.anti_virus);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
        initEvent();

    }

    /**
     * 1
     */

    protected void initView() {
        setContentView(R.layout.activity_antivirus);
        // 绑定视图
        ivScan = (ImageView) findViewById(R.id.iv_scan);
        tvUseTime = (TextView) findViewById(R.id.tv_use_time);
        llLog = (LinearLayout) findViewById(R.id.ll_log);
        pbProgress = (ProgressBar) findViewById(R.id.pb_progress);


        // 开始一个旋转动画
        Animation rotateAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate_forever);
        rotateAnimation.setDuration(2000);
        ivScan.startAnimation(rotateAnimation);
    }

    /**
     * 2
     */

    protected void initData() {
        running = true;

        // 启动计时器来计时
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        useTime++;
                        tvUseTime.setText(formatTime(useTime));
                    }
                });
            }
        };
        timer = new Timer(true);
        timer.schedule(timerTask, 1000, 1000);


        // 启动一个线程来扫描应用程序
        initDataThread = new Thread() {
            @Override
            public void run() {
                List<AppInfoBean> apps = AppManagerEngine.getInstalledAppInfo(AntivirusActivity.this, null);
                //设置进度最大值
                pbProgress.setMax(apps.size());

                AntivirusDao dao = new AntivirusDao(AntivirusActivity.this);
                for (final AppInfoBean app: apps) {
                    if(!running) {//安全退出
                        return;
                    }
                    try {
                        String md5 = EncryptionUtils.md5File(new File(app.getApkPath()));
                        final VirusBean virusBean = dao.selectByMd5(md5);
                        // 设置进程
                        progress++;
                        pbProgress.setProgress(progress);

                        // 添加一个TextView到llLog
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // 创建TextView并添加到llLog
                                TextView textView = new TextView(AntivirusActivity.this);
                                textView.setText(app.getName());
                                llLog.addView(textView, 0);
                                // 检查是否病毒
                                if(null != virusBean) {
                                    // 应用为病毒
                                    // 字体设置红色
                                    textView.setTextColor(Color.RED);
                                }
                            }
                        });

                        //添加病毒列表
                        if(null != virusBean) {
                            virusBean.setPackageName(app.getPackageName());
                            viruses.add(virusBean);
                        }

                        //增强用户的感受
                        SystemClock.sleep(new Random().nextInt(1024));

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                // 完成扫描应用程序时取消定时器
                timer.cancel();

                //开始结果活动
                startResultActivity();

            }
        };
        initDataThread.start();

    }

    /**
     * 启动结果类
     */
    private void startResultActivity() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(AntivirusActivity.this, AntivirusResultActivity.class);
                intent.putExtra(Constant.EXTRA_VIRUSES, viruses);
                startActivity(intent);
                finish();
            }
        });

    }

    /**
     * 3
     */

    protected void initEvent() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //防止内存泄漏
        //如果活动退出，停止子线程
        running = false;
        // 取消定时器
        timer.cancel();
    }

    /**
     *时间格式转换
     */
    private String formatTime(int second) {
        int minute = second / 60;
        second = second % 60;
        return String.format("%02d:%02d", minute, second);
    }
}
