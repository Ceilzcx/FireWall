package com.example.firewall.dao;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

/**
 * 申请权限,传入要申请的权限和Context
 */
public class PermissionsDao {
    private static final int OPEN_SET_REQUEST_CODE = 100;

    //调用此方法判断是否拥有权限
    public static void initPermissions(Context context, String[] permissions) {
        if (lacksPermission(context, permissions)) {//判断是否拥有权限
            //请求权限，第二参数权限String数据，第三个参数是请求码便于在onRequestPermissionsResult 方法中根据code进行判断
            ActivityCompat.requestPermissions((Activity) context, permissions, OPEN_SET_REQUEST_CODE);
        }
    }

    //如果返回true表示缺少权限
    private static boolean lacksPermission(Context context, String[] permissions) {
        for (String permission : permissions) {
            //判断是否缺少权限，true=缺少权限
            if(ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED){
                return true;
            }
        }
        return false;
    }

}
