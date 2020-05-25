package com.feimeng.network.monitor;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Author: Feimeng
 * Time:   2020/1/15
 * Description:
 */
public class NetWorkMonitorManager {
    private static final String ANDROID_NET_CHANGE_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";
    private static NetWorkMonitorManager sInstance;

    private Application mApplication;
    private BroadcastReceiver mReceiver;
    private ConnectivityManager.NetworkCallback mNetworkCallback;

    /**
     * 存储接受网络状态变化消息的方法的map
     */
    private Map<Object, NetWorkStateReceiverMethod> mNetWorkStateChangedMethodMap = new HashMap<>();

    public static NetWorkMonitorManager getInstance() {
        if (sInstance == null) {
            synchronized (NetWorkMonitorManager.class) {
                if (sInstance == null) sInstance = new NetWorkMonitorManager();
            }
        }
        return sInstance;
    }

    private NetWorkMonitorManager() {
    }

    /**
     * 初始化 传入application
     *
     * @param application 应用对象
     */
    public void init(Application application) {
        if (application == null) throw new NullPointerException("application can not be null");
        mApplication = application;
        initMonitor();
    }

    /**
     * 初始化网络监听 根据不同版本做不同的处理
     */
    private void initMonitor() {
        ConnectivityManager connectivityManager = (ConnectivityManager) mApplication.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { // API 大于26时
            initNetworkCallback();
            connectivityManager.registerDefaultNetworkCallback(mNetworkCallback);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { // API 大于21时
            initNetworkCallback();
            connectivityManager.registerNetworkCallback(new NetworkRequest.Builder().build(), mNetworkCallback);
        } else { // 低版本
            mReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (ANDROID_NET_CHANGE_ACTION.equalsIgnoreCase(intent.getAction())) {
                        //网络发生变化 没有网络-0：WIFI网络1：4G网络-4：3G网络-3：2G网络-2
                        int netType = NetStateUtils.getAPNType(context);
                        NetWorkState netWorkState;
                        switch (netType) {
                            case 0: // None
                                netWorkState = NetWorkState.NONE;
                                break;
                            case 1: // Wifi
                                netWorkState = NetWorkState.WIFI;
                                break;
                            default: // GPRS
                                netWorkState = NetWorkState.GPRS;
                                break;
                        }
                        postNetState(netWorkState);
                    }
                }
            };
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(ANDROID_NET_CHANGE_ACTION);
            mApplication.registerReceiver(mReceiver, intentFilter);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void initNetworkCallback() {
        mNetworkCallback = new ConnectivityManager.NetworkCallback() {
            /**
             * 网络可用的回调连接成功
             */
            @Override
            public void onAvailable(@NonNull Network network) {
                super.onAvailable(network);
                int netType = NetStateUtils.getAPNType(NetWorkMonitorManager.this.mApplication);
                NetWorkState netWorkState;
                switch (netType) {
                    case 0://None
                        netWorkState = NetWorkState.NONE;
                        break;
                    case 1://Wifi
                        netWorkState = NetWorkState.WIFI;
                        break;
                    default://GPRS
                        netWorkState = NetWorkState.GPRS;
                        break;
                }
                postNetState(netWorkState);
            }

            /**
             * 网络不可用时调用和onAvailable成对出现
             */
            @Override
            public void onLost(@NonNull Network network) {
                super.onLost(network);
                postNetState(NetWorkState.NONE);
            }

            /**
             * 在网络连接正常的情况下，丢失数据会有回调 即将断开时
             */
            @Override
            public void onLosing(@NonNull Network network, int maxMsToLive) {
                super.onLosing(network, maxMsToLive);
            }

            /**
             * 网络功能更改 满足需求时调用
             */
            @Override
            public void onCapabilitiesChanged(@NonNull Network network, @NonNull NetworkCapabilities networkCapabilities) {
                super.onCapabilitiesChanged(network, networkCapabilities);
            }

            /**
             * 网络连接属性修改时调用
             */
            @Override
            public void onLinkPropertiesChanged(@NonNull Network network, @NonNull LinkProperties linkProperties) {
                super.onLinkPropertiesChanged(network, linkProperties);
            }

            /**
             * 网络缺失network时调用
             */
            @Override
            public void onUnavailable() {
                super.onUnavailable();
            }
        };
    }

    /**
     * 反注册广播
     */
    public void onDestroy() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            mApplication.unregisterReceiver(mReceiver);
        }
    }

    /**
     * 注入
     *
     * @param object 对象
     */
    public void register(Object object) {
        if (object != null) {
            NetWorkStateReceiverMethod netWorkStateReceiverMethod = findMethod(object);
            if (netWorkStateReceiverMethod != null) {
                mNetWorkStateChangedMethodMap.put(object, netWorkStateReceiverMethod);
            }
        }
    }

    /**
     * 删除
     *
     * @param object 对象
     */
    public void unregister(Object object) {
        if (object != null) {
            mNetWorkStateChangedMethodMap.remove(object);
        }
    }

    /**
     * 网络状态发生变化，需要去通知更改
     *
     * @param netWorkState 网络状态
     */
    private void postNetState(NetWorkState netWorkState) {
        Set<Object> set = mNetWorkStateChangedMethodMap.keySet();
        for (Object object : set) {
            NetWorkStateReceiverMethod netWorkStateReceiverMethod = mNetWorkStateChangedMethodMap.get(object);
            invokeMethod(netWorkStateReceiverMethod, netWorkState);
        }
    }

    /**
     * 具体执行方法
     */
    private void invokeMethod(NetWorkStateReceiverMethod netWorkStateReceiverMethod, NetWorkState netWorkState) {
        if (netWorkStateReceiverMethod != null) {
            try {
                NetWorkState[] netWorkStates = netWorkStateReceiverMethod.getNetWorkState();
                for (NetWorkState myState : netWorkStates) {
                    if (myState == netWorkState) {
                        netWorkStateReceiverMethod.getMethod().invoke(netWorkStateReceiverMethod.getObject(), netWorkState);
                        return;
                    }
                }

            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 找到对应的方法
     */
    private NetWorkStateReceiverMethod findMethod(Object object) {
        NetWorkStateReceiverMethod targetMethod;
        if (object != null) {
            Class myClass = object.getClass();
            //获取所有的方法
            Method[] methods = myClass.getDeclaredMethods();
            for (Method method : methods) {
                //如果参数个数不是1个 直接忽略
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    if (method.getParameterCount() != 1) {
                        continue;
                    }
                }
                //获取方法参数
                Class[] parameters = method.getParameterTypes();
                if (parameters.length != 1) continue;
                //参数的类型需要是NetWorkState类型
                if (parameters[0].getName().equals(NetWorkState.class.getName())) {
                    //是NetWorkState类型的参数
                    NetWorkMonitor netWorkMonitor = method.getAnnotation(NetWorkMonitor.class);
                    targetMethod = new NetWorkStateReceiverMethod();
                    //如果没有添加注解，默认就是所有网络状态变化都通知
                    if (netWorkMonitor != null) {
                        NetWorkState[] netWorkStates = netWorkMonitor.monitorFilter();
                        targetMethod.setNetWorkState(netWorkStates);
                    }
                    targetMethod.setMethod(method);
                    targetMethod.setObject(object);
                    //只添加第一个符合的方法
                    return targetMethod;
                }
            }
        }
        return null;
    }
}
