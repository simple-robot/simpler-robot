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
@file:JvmName("Containers")
@file:JvmMultifileClass
package love.forte.simbot.api.message.containers

import love.forte.simbot.annotation.ContainerType
import love.forte.simbot.api.message.assists.ActionMotivations
import love.forte.simbot.api.message.assists.Flag
import love.forte.simbot.api.message.assists.FlagContent
import love.forte.simbot.api.message.assists.Permissions
import java.util.concurrent.TimeUnit


/**
 * 所有的Container的父接口
 */
@ContainerType("容器")
public interface Container


/**
 * 原始数据容器。
 * 定义可以得到原始数据的字符串信息。
 */
@ContainerType("原始数据容器")
public interface OriginalDataContainer : Container {
    /**
     * 得到原始数据字符串。
     * 数据不应该为null。
     *
     * 原始数据信息一般用于debug或测试用，大部分情况下功能类似于toString。
     */
    val originalData: String
}


/**
 * 权限容器，定义可以得到一个 [权限][Permissions]。
 *
 * 一般代表这个人在群里的权限
 */
@ContainerType("权限容器")
public interface PermissionContainer : Container {
    /**
     * 权限信息。
     */
    val permission: Permissions
}


/**
 * 匿名容器，代表此容器是 **可能匿名的**。
 */
@ContainerType("可匿名容器")
public interface AnonymousContainer : Container {
    /**
     * 当前是否为 **匿名** 状态。
     */
    val anonymous: Boolean
}


/**
 * 禁言时间容器，代表此容器能够获得一个 **禁言时间（毫秒值）**。
 */
public interface MuteTimeContainer : Container {
    /**
     * (剩余的)禁言时间。当不支持或无法获取的时候返回 `-1`。
     */
    val muteTime: Long
}


/**
 * 时间容器，代表此容器能够获得一个 **时间（毫秒值）**。
 */
public interface TimeContainer : Container {
    /**
     * 获取时间。少数获取不到的情况下会返回`-1`, 但是一般大多数情况以当前时间戳代替。
     * 不出意外，此值代表毫秒值。
     */
    val time: Long


    /**
     * 根据给定的时间格式进行时间转化。不出意外的话，其中的[TimeUnit]必然是[TimeUnit.MILLISECONDS]。
     *
     * In Kotlin:
     *
     * ```kotlin
     *
     * // Demo1.kt
     *
     * val timeContainer: TimeContainer = ...
     * // 获取时间的秒值信息。
     * val secondTime = t.time { ::toSeconds }
     *
     * println(secondTime)
     *
     * ```
     *
     * In Java:
     *
     * ```java
     *
     * // Demo2.java
     *
     * TimeContainer timeContainer = ...;
     * // 获取时间对应的秒值。
     * long second = timeContainer.getTime(unit -> unit::toSeconds);
     *
     * System.out.println(second);
     *
     * ```
     *
     */
    @JvmDefault
    fun <N> getTime(unit: TimeUnit.() -> ((Long) -> N)): N = unit(TimeUnit.MILLISECONDS)(time)

    companion object {
        @JvmSynthetic
        inline fun <N> TimeContainer.time(unit: TimeUnit.() -> ((Long) -> N)): N = unit(TimeUnit.MILLISECONDS)(time)
    }

}






/**
 * 标识容器。定义可以得到一个标识。
 */
@ContainerType("标识容器")
public interface FlagContainer<out T : FlagContent> : Container {
    /** 标识 */
    val flag: Flag<T>
}


/**
 * [行动动机][ActionMotivations]容器, 定义可以得到当前类型的动机类型。
 *
 * 一般来讲，此容器使用在枚举类上，例如消息事件中特有的类型枚举.
 *
 * @property actionMotivations ActionMotivations 得到对应的 [行动动机][ActionMotivations]
 */
@ContainerType("行动动机容器")
public interface ActionMotivationContainer : Container {
    val actionMotivations: ActionMotivations
}













