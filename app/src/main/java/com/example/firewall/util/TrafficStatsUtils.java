package com.example.firewall.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.TrafficStats;
import android.util.Log;

import com.example.firewall.bean.TrafficAppInfo;
import com.example.firewall.bean.TrafficModeInfo;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

public class TrafficStatsUtils {

    /**
     * @Description: 获取uid上传的流量(wifi+3g/4g)
     * @return 上传的流量（tcp+udp）  返回-1 表示不支持得机型
     */
    public static long getTxTraffic(int uid) {
        return  TrafficStats.getUidTxBytes(uid);
    }

    /**
     * @Description: 获取uid上传的流量(wifi+3g/4g)  通过读取/proc/uid_stat/uid/tcp_snd文件获取
     * @return 上传的流量（tcp）  返回-1 表示出现异常
     */
    public static long getTxTcpTraffic(int uid){
        RandomAccessFile rafSnd = null;
        String sndPath = "/proc/uid_stat/" + uid + "/tcp_snd";
        long sndTcpTraffic;
        try {
            rafSnd = new RandomAccessFile(sndPath, "r");
            sndTcpTraffic = Long.parseLong(rafSnd.readLine());
        } catch (FileNotFoundException e) {
            sndTcpTraffic = -1;
        } catch (IOException e) {
            e.printStackTrace();
            sndTcpTraffic = -1;
        } finally {
            try {
                if (rafSnd != null){
                    rafSnd.close();
                }
            } catch (IOException e) {
                sndTcpTraffic = -1;
            }
        }
        return sndTcpTraffic;
    }

    /**
     * @Description: 获取uid下載的流量(wifi+3g/4g)
     * @return 下載的流量(tcp+udp) 返回-1表示不支持的机型
     */
    public static long getRxTraffic(int uid){
        return  TrafficStats.getUidRxBytes(uid);
    }

    /**
     * @Description: 获取uid下载的流量(wifi+3g/4g) 通过读取/proc/uid_stat/uid/tcp_rcv文件获取
     * @return 下载的流量（tcp）  返回-1 表示出现异常
     */
    public static long getRxTcpTraffic(int uid) {
        RandomAccessFile rafRcv = null; // 用于访问数据记录文件
        String rcvPath = "/proc/uid_stat/" + uid + "/tcp_rcv";
        long rcvTcpTraffic;
        try {
            rafRcv = new RandomAccessFile(rcvPath, "r");
            rcvTcpTraffic = Long.parseLong(rafRcv.readLine()); // 读取流量统计
        } catch (FileNotFoundException e) {
            rcvTcpTraffic = -1;
        } catch (IOException e) {
            rcvTcpTraffic = -1;
        } finally {
            try {
                if (rafRcv != null){
                    rafRcv.close();
                }
            } catch (IOException e) {
                rcvTcpTraffic = -1;
            }
        }
        return rcvTcpTraffic;
    }

    /**
     * @Description: 得到uid的总流量（上传+下载）
     * @return uid的总流量   当设备不支持方法且没有权限访问/proc/uid_stat/uid时 返回-1
     */
    public static long getTotalTraffic(int uid){
        long txTraffic = (getTxTraffic(uid)==-1)?getTxTcpTraffic(uid):getTxTraffic(uid);
        if(txTraffic==-1){
            return -1;
        }
        long rxTraffic = (getRxTraffic(uid)==-1)?getRxTcpTraffic(uid):getRxTraffic(uid);
        if(rxTraffic==-1){
            return -1;
        }
        return txTraffic+rxTraffic;
    }

    /**
     * @Description: 取得程序的uid
     * @return 当前程序的uid  返回-1表示出现异常
     */
    public static int getUid(Context context) {
        try {
            PackageManager packageManager =context.getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            return applicationInfo.uid;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static List<TrafficAppInfo> getTrafficAppInfos(Context context){
        List<TrafficAppInfo> res = new ArrayList<>();
        PackageManager pm = context.getPackageManager();
        //获取所有安装应用信息
        final List<ApplicationInfo> infos = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        for (ApplicationInfo info : infos) {
            if (getTotalTraffic(info.uid) != 0){
                TrafficAppInfo appInfo = new TrafficAppInfo();
                appInfo.setIcon(info.loadIcon(pm));
                appInfo.setName(info.loadLabel(pm).toString());
                appInfo.setPackageName(info.packageName);
                appInfo.setRxTraffic(getRxTcpTraffic(info.uid) + getRxTraffic(info.uid));
                appInfo.setTxTraffic(getTxTraffic(info.uid) + getTxTcpTraffic(info.uid));
                //检查系统和sd卡中的应用
                if ((info.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                    //判断是否为系统应用，系统应用返回true
                    appInfo.setSystemApp(true);
                }

                if ((info.flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) != 0) {
                    //存在EXTERNAL_STORAGE标志，代表为sd卡中的应用
                    appInfo.setSystemApp(true);
                }

                res.add(appInfo);
            }
        }

        return res;
    }

    public static List<TrafficModeInfo> getTrafficMode() {
        List<TrafficModeInfo> res = new ArrayList<>();
        String rcvPath = "/proc/net/xt_qtaguid/iface_stat_fmt";
        try {
            FileReader fr = new FileReader(rcvPath);
            BufferedReader br = new BufferedReader(fr);
            String line = br.readLine();
            while ((line = br.readLine()) != null){
                String[] datas = line.split(" ");
                TrafficModeInfo info = new TrafficModeInfo();
                info.setIfname(datas[0]);
                info.setRx_bytes(Long.parseLong(datas[5]) + Long.parseLong(datas[7]));
                info.setRx_packets(Integer.parseInt(datas[6]) + Integer.parseInt(datas[8]));
                info.setTx_bytes(Long.parseLong(datas[11]) + Long.parseLong(datas[13]));
                info.setTx_packets(Integer.parseInt(datas[12]) + Integer.parseInt(datas[14]));
                res.add(info);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return res;
    }

}