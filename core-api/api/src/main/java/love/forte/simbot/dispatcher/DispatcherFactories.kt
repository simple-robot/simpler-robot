/*
 *
 *  * Copyright (c) 2021. ForteScarlet All rights reserved.
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

package love.forte.simbot.dispatcher

import love.forte.simbot.listener.ListenerContext


/**
 * 用于事件调度的调度器工厂。
 */
public interface EventDispatcherFactory : DispatcherFactory


/**
 * 用于 [ListenerContext.Scope.CONTINUOUS_SESSION] 作用域下会话调度的调度器工厂。
 */
public interface ContinuousSessionDispatcherFactory : DispatcherFactory

