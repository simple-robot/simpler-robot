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

import love.forte.common.ioc.exception.NoSuchDependException;

/**
 *
 * 依赖bean工厂，用于得到依赖工厂中管理的bean。
 *
 * @author <a href="https://github.com/ForteScarlet"> ForteScarlet </a>
 */
public interface DependBeanFactory {

    /**
     * 根据类型获取一个依赖实例。
     * @param type 类型
     * @throws NoSuchDependException 如果依赖没有找到则抛出异常
     * @return 实例
     */
    <T> T get(Class<T> type);

    /**
     * 根据名称和类型获取一个依赖实例。通过名称获取，并转化为type。
     * @param type 类型
     * @param name 依赖名称
     * @throws NoSuchDependException 如果依赖没有找到则抛出异常
     * @return 转化后的实例
     */
    <T> T get(Class<T> type, String name);

    /**
     * 根据名称获取一个依赖。
     * @param name 名称
     * @throws NoSuchDependException 如果依赖没有找到则抛出异常
     * @return 实例
     */
    Object get(String name);
    /**
     * 根据类型获取一个依赖实例。获取不到则会返回null。
     * @param type 类型
     * @return 实例
     */
    <T> T getOrNull(Class<T> type);

    /**
     * 根据名称和类型获取一个依赖实例。通过名称获取，并转化为type。获取不到则会返回null。
     * @param type 类型
     * @param name 依赖名称
     * @return 转化后的实例
     */
    <T> T getOrNull(Class<T> type, String name);

    /**
     * 根据名称获取一个依赖。获取不到则会返回null。
     * @param name 名称
     * @return 实例
     */
    Object getOrNull(String name);



}
