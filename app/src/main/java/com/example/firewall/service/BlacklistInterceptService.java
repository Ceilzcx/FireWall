package com.example.firewall.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;

import androidx.annotation.Nullable;

import com.example.firewall.bean.InterceptPhoneInfo;
import com.example.firewall.dao.InterceptDao;

import java.util.List;

import static com.example.firewall.util.CallUtil.endCall;


/**
 *黑名单拦截服务
 *拦截电话，如果号码在黑名单中
 */
public class BlacklistInterceptService extends Service {
    private SmsBroadcastReceiver smsBroadcastReceiver;
    private TelephonyManager tm;
    private PhoneStateListener listener;
    private List<InterceptPhoneInfo> infos;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // 短信广播
    private class SmsBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Object[] datas = (Object[]) intent.getSerializableExtra("pdus");
            String format = intent.getStringExtra("format");
            assert datas != null;
            for (Object sms : datas) {
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) sms, format);
                //短信来自的号码
                String phone = smsMessage.getOriginatingAddress();
                for (InterceptPhoneInfo info : infos) {
                    if(phone.equals(info.getNumber())){
                        //终止广播
                        abortBroadcast();
                    }
                }

            }
        }
    }

    @Override
    public void onCreate() {
        InterceptDao dao = new InterceptDao(getApplicationContext());
        infos = dao.getAll();
        // 创建短信的广播监听对象
        smsBroadcastReceiver = new SmsBroadcastReceiver();
        // 短信广播意图
        IntentFilter filter = new IntentFilter(
                "android.provider.Telephony.SMS_RECEIVED");
        // 设置最高级别
        filter.setPriority(Integer.MAX_VALUE);
        registerReceiver(smsBroadcastReceiver, filter);

        // 开启电话的监听

        tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        listener = new PhoneStateListener() {

            @Override
            public void onCallStateChanged(int state,
                                           final String incomingNumber) {
                // state 电话的状态 incomingNumber 打进来的号码
                switch (state) {
                    case TelephonyManager.CALL_STATE_IDLE:// 挂断的状态，空闲的状态
                        System.out.println("CALL_STATE_IDLE");
                        break;
                    case TelephonyManager.CALL_STATE_RINGING:// 响铃状态
                        System.out.println("CALL_STATE_RINGING");
                        for (InterceptPhoneInfo info : infos) {
                            if (incomingNumber.equals(info.getNumber())) {
                                // 电话拦截
                                System.out.println("挂断电话");
                                // 挂断电话之前先注册内容观察者,监听电话日志的变化
                                getContentResolver().registerContentObserver(
                                        Uri.parse("content://call_log/calls"), true,
                                        new ContentObserver(new Handler()) {
                                            // 电话日志变化 触发此方法调用
                                            @Override
                                            public void onChange(boolean selfChange) {
                                                // 马上删除电话日志
                                                deleteCalllog(incomingNumber);
                                                // 再取消内容观察者注册
                                                getContentResolver()
                                                        .unregisterContentObserver(this);
                                                super.onChange(selfChange);
                                            }

                                        });

                                endCall();
                            }
                        }
                        break;
                    case TelephonyManager.CALL_STATE_OFFHOOK:// 通话的状态
                        System.out.println("CALL_STATE_OFFHOOK");
                        break;

                    default:
                        break;
                }
                super.onCallStateChanged(state, incomingNumber);
            }

        };
        // 注册电话的监听
        tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        // 关闭短信的广播监听
        unregisterReceiver(smsBroadcastReceiver);
        // 关闭电话的监听
        tm.listen(listener, PhoneStateListener.LISTEN_NONE);
        super.onDestroy();
    }

    protected void deleteCalllog(String number) {
        Uri uri = Uri.parse("content://call_log/calls");
        // 删除日志
        getContentResolver().delete(uri, "number=?", new String[] { number });
    }

}

