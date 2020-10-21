/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     Containers.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */
@file:JvmName("Containers")
@file:JvmMultifileClass
package love.forte.simbot.api.message.containers

import love.forte.simbot.annotation.ContainerType
import love.forte.simbot.api.message.assists.ActionMotivations
import love.forte.simbot.api.message.assists.Flag
import love.forte.simbot.api.message.assists.FlagContent
import love.forte.simbot.api.message.assists.Permissions


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













