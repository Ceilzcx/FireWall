package com.example.firewall.dao;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.os.Environment;
import android.os.RemoteException;
import android.text.TextUtils;

import com.jcy31401129.androidfirewall.domain.AppInfoBean;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 提供应用信息
 */
public class AppManagerDao {

    /**
     * 获得SD卡的可用空间
     *
     * @return 可用空间大小
     */
    public static long getSdCardFreeSpace() {
        File directory = Environment.getExternalStorageDirectory();
        return directory.getFreeSpace();
    }

    /**
     * 获得内存可用空间
     *
     * @return 可用内存大小
     */
    public static long getRomFreeSpace() {
        File directory = Environment.getDataDirectory();
        return directory.getFreeSpace();
    }

    /**
     * 获取所有安装的应用信息
     * @return 返回AppInfoBean列表，不为空
     */
    public static List<AppInfoBean> getInstalledAppInfo(Context context, final AppInfoListener listener) {

        PackageManager pm = context.getPackageManager();
        //获取所有安装应用信息
        final List<ApplicationInfo> infos = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        // 用于计算获取大小的进度
        class CompletedCountBean {
            int completedCount;
        }
        final CompletedCountBean count = new CompletedCountBean();

        // 创建bean列表。 这里输入列表的某些初始值
        final List<AppInfoBean> appInfos = new ArrayList<>(infos.size());

        //启动定时器设置最大等待时间
        final Timer timer = new Timer(true);
        // 如果存在监听，启动
        if (null != listener) {
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    synchronized (count) {
                        count.completedCount = -1;
                        listener.onGetInfoCompleted(appInfos);
                    }
                }
            };
            timer.schedule(timerTask, 3000);
        }


        for (ApplicationInfo info : infos) {
            // 新建一个AppInfoBean并添加到列表中
            final AppInfoBean bean = new AppInfoBean();
            appInfos.add(bean);

            // 为bean设置值
            bean.setIcon(info.loadIcon(pm));
            bean.setName(info.loadLabel(pm).toString());
            bean.setPackageName(info.packageName);

            //检查系统和sd卡中的应用
            if ((info.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                //判断是否为系统应用，系统应用返回true
                bean.setSystemApp(true);
            }

            if ((info.flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) != 0) {
                //存在EXTERNAL_STORAGE标志，代表为sd卡中的应用
                bean.setSystemApp(true);
            }

            //获取apk路径
            bean.setApkPath(info.sourceDir);
            //获取app大小
            getAppSize(context, info.packageName, new AppSizeInfoListener() {
                @Override
                public void onGetSizeInfoCompleted(AppSizeInfo sizeInfo) {
                    //总大小=缓存+数据+应用
                    long totalSize = sizeInfo.cacheSize + sizeInfo.codeSize + sizeInfo.dataSize;
                    bean.setSize(totalSize);
                    bean.setCacheSize(sizeInfo.cacheSize);
                    // 监听器为空，不调用
                    if(null == listener)
                        return;
                    synchronized (count) {
                        count.completedCount++;
                        //获取所以有app大小时，唤醒监听器
                        if(count.completedCount == infos.size()) {
                            // 停止计时器
                            timer.cancel();
                            listener.onGetInfoCompleted(appInfos);
                        }
                    }
                }
            });
        }
        return appInfos;
    }

    /**
     * 通过包名获取app大小
     * @param context
     * @param packageName 包名
     * @param listener 获取大小成功时调用
     */
    public static void getAppSize(Context context, String packageName, final AppSizeInfoListener listener) {
        // 检查参数
        if(null == listener) {
            throw new NullPointerException("listener can't be null");
        }
        if(TextUtils.isEmpty(packageName)) {
            throw  new IllegalArgumentException("packageName can't be empty");
        }

        // get pm
        PackageManager pm = context.getPackageManager();
        Method getPackageSizeInfo = null;
        try {
            // 获取包大小信息的方法
            getPackageSizeInfo = pm.getClass().getMethod(
                    "getPackageSizeInfo",
                    String.class, IPackageStatsObserver.class);
            // 调用方法
            getPackageSizeInfo.invoke(pm, packageName,
                    new IPackageStatsObserver.Stub() {
                        @Override
                        public void onGetStatsCompleted(PackageStats pStats, boolean succeeded)
                                throws RemoteException {
                            //调用监听器
                            listener.onGetSizeInfoCompleted(
                                    new AppSizeInfo(pStats.cacheSize, pStats.dataSize, pStats.codeSize));
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 清除所有应用缓存
     * @param context
     *                 onClearCompleted 子线程运行
     *                 onClearFailed UI运行
     */
    public static void clearAllCache(Context context, final ClearCacheListener listener) {

        PackageManager pm = context.getPackageManager();
        try {
            Method freeStorageAndNotify = pm.getClass().getDeclaredMethod("freeStorageAndNotify", long.class, IPackageDataObserver.class);
            freeStorageAndNotify.invoke(pm, Long.MAX_VALUE, new IPackageDataObserver.Stub(){
                @Override
                public void onRemoveCompleted(String packageName, boolean succeeded) throws RemoteException {
                    if(null != listener)
                        listener.onClearCompleted();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            if(null != listener)
                listener.onClearFailed();
        }
    }

    /**
     * 清除完成后唤醒
     */
    public static interface ClearCacheListener {
        /**
         *成功时唤醒
         */
        void onClearCompleted();

        /**
         * 失败时唤醒
         */
        void onClearFailed();
    }

    /**
     * 应用信息获取成功时唤醒
     */
    public static interface AppInfoListener {
        void onGetInfoCompleted(List<AppInfoBean> apps);
    }

    /**
     * 大小信息获取时唤醒
     */
    public static interface AppSizeInfoListener {
        void onGetSizeInfoCompleted(AppSizeInfo sizeInfo);
    }

    /**
     * app大小bean
     */
    public static class AppSizeInfo {
        private long cacheSize;
        private long dataSize;
        private long codeSize;

        public AppSizeInfo() {
        }

        public AppSizeInfo(long cacheSize, long dataSize, long codeSize) {
            this.cacheSize = cacheSize;
            this.dataSize = dataSize;
            this.codeSize = codeSize;
        }

        public long getCacheSize() {
            return cacheSize;
        }

        public void setCacheSize(long cacheSize) {
            this.cacheSize = cacheSize;
        }

        public long getDataSize() {
            return dataSize;
        }

        public void setDataSize(long dataSize) {
            this.dataSize = dataSize;
        }

        public long getCodeSize() {
            return codeSize;
        }

        public void setCodeSize(long codeSize) {
            this.codeSize = codeSize;
        }
    }

}
