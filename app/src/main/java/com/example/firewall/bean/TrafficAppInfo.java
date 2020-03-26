package com.example.firewall.bean;

import android.graphics.drawable.Drawable;

public class TrafficAppInfo {
    private Drawable icon;
    private String name;
    private String packageName;
    private long rxTraffic;
    private long txTraffic;
    private boolean systemApp;

    public TrafficAppInfo(){}

    public String getName() {
        return name;
    }

    public Drawable getIcon() {
        return icon;
    }

    public long getTxTraffic() {
        return txTraffic;
    }

    public long getRxTraffic() {
        return rxTraffic;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public void setTxTraffic(long txTraffic) {
        this.txTraffic = txTraffic;
    }

    public void setRxTraffic(long rxTraffic) {
        this.rxTraffic = rxTraffic;
    }

    public boolean isSystemApp() {
        return systemApp;
    }

    public void setSystemApp(boolean systemApp) {
        this.systemApp = systemApp;
    }
}
