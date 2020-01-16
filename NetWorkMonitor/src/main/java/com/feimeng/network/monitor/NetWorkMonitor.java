package com.feimeng.network.monitor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Author: Feimeng
 * Time:   2020/1/15
 * Description:
 */
@Retention(RetentionPolicy.RUNTIME) // 运行时注解
@Target(ElementType.METHOD) // 标记在方法上
public @interface NetWorkMonitor {
    // 监听的网络状态变化 默认全部监听并提示
    NetWorkState[] monitorFilter() default {NetWorkState.GPRS, NetWorkState.WIFI, NetWorkState.NONE};
}
