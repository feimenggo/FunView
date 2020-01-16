package com.feimeng.network.monitor;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

/**
 * Author: Feimeng
 * Time:   2020/1/15
 * Description: 网络状态工具
 */
public class NetStateUtils {
    /**
     * 获取当前的网络状态
     *
     * @param context 上下文
     * @return 网络状态 0:没有网络,1:WIFI网络,2:2G网络,3:3G网络,4:4G网络
     */
    public static int getAPNType(Context context) {
        //结果返回值
        int netType = 0;
        //获取手机所有连接管理对象
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager == null) return netType;
        // 获取NetworkInfo对象
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        // NetworkInfo对象为空 则代表没有网络
        if (networkInfo == null) {
            return netType;
        }
        // 否则 NetworkInfo对象不为空 则获取该networkInfo的类型
        int nType = networkInfo.getType();
        if (nType == ConnectivityManager.TYPE_WIFI) {
            //WIFI
            netType = 1;
        } else if (nType == ConnectivityManager.TYPE_MOBILE) {
            int nSubType = networkInfo.getSubtype();
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (telephonyManager != null) {
                if (nSubType == TelephonyManager.NETWORK_TYPE_LTE
                        && !telephonyManager.isNetworkRoaming()) {
                    netType = 4;
                } else if (nSubType == TelephonyManager.NETWORK_TYPE_UMTS // 3G 联通的3G为UMTS或HSDPA 电信的3G为EVDO
                        || nSubType == TelephonyManager.NETWORK_TYPE_HSDPA
                        || nSubType == TelephonyManager.NETWORK_TYPE_EVDO_0
                        && !telephonyManager.isNetworkRoaming()) {
                    netType = 3;
                } else if (nSubType == TelephonyManager.NETWORK_TYPE_GPRS // 2G 移动和联通的2G为GPRS或EGDE，电信的2G为CDMA
                        || nSubType == TelephonyManager.NETWORK_TYPE_EDGE
                        || nSubType == TelephonyManager.NETWORK_TYPE_CDMA
                        && !telephonyManager.isNetworkRoaming()) {
                    netType = 2;
                } else {
                    netType = 2;
                }
            }
        }
        return netType;
    }
}
