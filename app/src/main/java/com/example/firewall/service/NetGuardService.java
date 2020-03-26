package com.example.firewall.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.VpnService;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.firewall.R;

import java.io.IOException;

public class NetGuardService extends VpnService {

    private static final String TAG = "NetGuard.Service";

    private static final String EXTRA_COMMAND = "Command";

    private ParcelFileDescriptor vpn = null;

    public static final int START = 1;

    public static final int RELOAD = 2;

    public static final int STOP = 3;

    @Override
    public void onCreate() {
        // Listen for connectivity updates
        IntentFilter ifConnectivity = new IntentFilter();
        ifConnectivity.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(connectivityChangedReceiver, ifConnectivity);
        super.onCreate();
    }

    private BroadcastReceiver connectivityChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "Received " + intent);
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Get command
        int cmd = intent.getIntExtra(EXTRA_COMMAND, RELOAD);
        Log.e(TAG, "执行：" + cmd);
        // Process command
        switch (cmd) {
            case START:

                break;
            case RELOAD:
                ParcelFileDescriptor prev = vpn;
                vpnStart();
                if (prev != null)
                    vpnStop(prev);
                break;
            case STOP:
                if (vpn != null) {
                    vpnStop(vpn);
                    vpn = null;
                }
                stopSelf();
                break;
        }
        return START_STICKY;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void vpnStart() {
        Log.e(TAG, "Starting");
        final Builder builder = new Builder();
        builder.setSession(getString(R.string.app_name));
        builder.addAddress("10.1.10.1", 32);
        builder.addAddress("fd00:1:fd00:1:fd00:1:fd00:1", 128);
        builder.addRoute("0.0.0.0", 0);
        builder.addRoute("0:0:0:0:0:0:0:0", 0);
        try {
            builder.addDisallowedApplication("FireWall");
            builder.addDisallowedApplication("com.google.android.gms");
            builder.addDisallowedApplication(getPackageName());
            vpn = builder.establish();
            Log.e(TAG, "启动完成");
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    private void vpnStop(ParcelFileDescriptor prev) {
        if (prev != null) {
            try {
                prev.close();
            } catch (IOException ex) {
                Log.e(TAG, ex.toString() + "\n" + Log.getStackTraceString(ex));
            }
        }
    }

    public static void start(Context context) {

    }

    public static void stop(Context context) {

    }

    public static void reload(Context context) {

    }


    @Override
    public void onDestroy() {
        Log.e(TAG, "VPNService Destroy");
        unregisterReceiver(connectivityChangedReceiver);
        super.onDestroy();
    }

}
