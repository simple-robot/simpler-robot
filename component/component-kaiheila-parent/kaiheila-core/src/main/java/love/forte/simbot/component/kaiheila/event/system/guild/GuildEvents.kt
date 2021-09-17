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

package love.forte.simbot.component.kaiheila.event.system.guild

import love.forte.simbot.component.kaiheila.event.Event


/**
 * [频道相关事件列表](https://developer.kaiheila.cn/doc/event/channel)
 *
 */
public interface GuildEventExtra<B : GuildEventExtraBody> : Event.Extra.Sys<B>


/**
 * [GuildEventExtra] 的 类型约束，一个标记用接口。
 *
 * @see GuildEventExtra
 * @see Event.Extra.Sys.body
 */
public interface GuildEventExtraBody