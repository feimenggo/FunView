package com.feimeng.network.monitor;

import java.lang.reflect.Method;

/**
 * Author: Feimeng
 * Time:   2020/1/15
 * Description: 保存接受状态变化的方法对象
 */
class NetWorkStateReceiverMethod {
    /**
     * 网络改变执行的方法
     */
    private Method method;
    /**
     * 网络改变执行的方法所属的类
     */
    private Object object;
    /**
     * 监听的网络改变类型
     */
    private NetWorkState[] netWorkState = {NetWorkState.GPRS, NetWorkState.WIFI, NetWorkState.NONE};

    Method getMethod() {
        return method;
    }

    void setMethod(Method method) {
        this.method = method;
    }

    Object getObject() {
        return object;
    }

    void setObject(Object object) {
        this.object = object;
    }

    NetWorkState[] getNetWorkState() {
        return netWorkState;
    }

    void setNetWorkState(NetWorkState[] netWorkState) {
        this.netWorkState = netWorkState;
    }
}
