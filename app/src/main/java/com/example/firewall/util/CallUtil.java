package com.example.firewall.util;

import android.content.Context;
import android.net.Uri;
import android.os.IBinder;

import com.android.internal.telephony.ITelephony;

import java.lang.reflect.Method;

/**
 *
 */
public class CallUtil {
    private static final String URI_CALL = "content://call_log/calls";

    /**
     * 结束通话
     *调用ITelephony中的endcall
     * @return 无论是否挂断
     */
    public static boolean endCall() {
        try {
            Class serviceManager = Class.forName("android.os.ServiceManager");
            Method getService = serviceManager.getDeclaredMethod("getService", String.class);
            ITelephony telephony = ITelephony.Stub.asInterface((IBinder) getService.invoke(null, Context.TELEPHONY_SERVICE));
            return telephony.endCall();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 通过号码删除通话，删除最新的通话记录
     * @param number 需要删除的号码
     * @return 删除行数
     */
    public static int deleteLatestCall(Context context, String number) {
        return context.getContentResolver().delete(Uri.parse(URI_CALL),
                "date = (select max(date) from calls where number=?)", new String[]{number});
    }

}
