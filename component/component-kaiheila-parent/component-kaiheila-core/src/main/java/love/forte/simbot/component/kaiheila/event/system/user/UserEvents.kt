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

@file:JvmName("UserEvents")

package love.forte.simbot.component.kaiheila.event.system.user

import love.forte.simbot.component.kaiheila.event.Event


/**
 * [用户相关事件列表](https://developer.kaiheila.cn/doc/event/user) 中事件体的 `body`
 *
 */
public interface UserEventExtraBody : Event.Extra.Sys.Body


/**
 * [用户相关事件列表](https://developer.kaiheila.cn/doc/event/user) 的事件体 `extra`
 *
 */
public interface UserEventExtra<B : UserEventExtraBody> : Event.Extra.Sys<B>
