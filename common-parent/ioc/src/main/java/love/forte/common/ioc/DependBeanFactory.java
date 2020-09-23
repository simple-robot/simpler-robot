/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     DependBeanFactory.java
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.common.ioc;

/**
 *
 * 依赖bean工厂，用于得到依赖工厂中管理的bean。
 *
 * @author <a href="https://github.com/ForteScarlet"> ForteScarlet </a>
 */
public interface DependBeanFactory {

    <T> T get(Class<T> type);
    <T> T get(Class<T> type, String name);
    Object get(String name);

    <T> T getOrNull(Class<T> type);
    <T> T getOrNull(Class<T> type, String name);
    Object getOrNull(String name);



}
