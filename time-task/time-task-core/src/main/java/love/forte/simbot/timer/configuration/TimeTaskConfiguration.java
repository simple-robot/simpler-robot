/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  simple-robot
 *  * File     MiraiAvatar.kt
 *  *
 *  * You can contact the author through the following channels:
 *  * github https://github.com/ForteScarlet
 *  * gitee  https://gitee.com/ForteScarlet
 *  * email  ForteScarlet@163.com
 *  * QQ     1149159218
 *
 */

package love.forte.simbot.timer.configuration;

import love.forte.common.ioc.DependBeanFactory;
import love.forte.common.ioc.annotation.ConfigBeans;
import love.forte.common.ioc.annotation.Depend;
import love.forte.common.utils.annotation.AnnotationUtil;
import love.forte.simbot.listener.ListenerManager;
import love.forte.simbot.listener.ListenerRegistered;
import love.forte.simbot.timer.*;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Set;
import java.util.function.Supplier;

/**
 * 定时任务配置类。
 * @author ForteScarlet
 */
@ConfigBeans
public class TimeTaskConfiguration implements ListenerRegistered {

    @Depend
    private DependBeanFactory dependBeanFactory;

    @Depend
    private TimerManager timerManager;

    /**
     * 监听函数注册结束后。扫描并注册定时任务。
     */
    @Override
    public void onRegistered(@NotNull ListenerManager manager) {
        Set<String> allBeans = dependBeanFactory.getAllBeans();
        for (String bean : allBeans) {
            Class<?> type = dependBeanFactory.getType(bean);
            if (AnnotationUtil.containsAnnotation(type, EnableTimeTask.class)) {
                Cron cron;
                Fixed fixed;
                String id, name;

                // enable time task
                for (Method method : type.getMethods()) {
                    id = methodToId(method);
                    name = methodToName(method);

                    cron = AnnotationUtil.getAnnotation(method, Cron.class);
                    fixed = AnnotationUtil.getAnnotation(method, Fixed.class);

                    if (cron == null && fixed == null) {
                        continue;
                    }

                    Supplier<Object> supplier;
                    if (Modifier.isStatic(method.getModifiers())) {
                        supplier = () -> null;
                    } else {
                        supplier = () -> dependBeanFactory.get(bean);
                    }

                    if (cron != null) {
                        long delay = cron.delay();
                        Task cronTask = new CronMethodTask(id, name, cron.value(), cron.repeat(), delay, method, supplier);
                        if (delay > 0) {
                            timerManager.addTask(cronTask, delay);
                        } else {
                            timerManager.addTask(cronTask);
                        }
                    }

                    if (fixed != null) {
                        long delay = fixed.delay();
                        Task fixedTask = new FixedMethodTask(id, name, fixed.value(), fixed.timeUnit(), fixed.repeat(), delay, method, supplier);
                        if (delay > 0) {
                            timerManager.addTask(fixedTask, delay);
                        } else {
                            timerManager.addTask(fixedTask);
                        }
                    }
                }
            }
        }
    }


    private static String methodToId(Method method) {
        return method.toGenericString();
    }

    private static String methodToName(Method method) {
        String className = method.getDeclaringClass().getSimpleName();
        return className + "." + method.getName();
    }

}
