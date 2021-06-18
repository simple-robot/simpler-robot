/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  simpler-robot
 *  * File     MiraiSpecialEvent.kt
 *  *
 *  * You can contact the author through the following channels:
 *  * github https://github.com/ForteScarlet
 *  * gitee  https://gitee.com/ForteScarlet
 *  * email  ForteScarlet@163.com
 *  * QQ     1149159218
 *  *
 *  *
 *
 */

package love.forte.simbot.component.mirai.message.event

import net.mamoe.mirai.event.events.BotEvent


/**
 * 此接口标记一个 mirai 提供的事件，来允许使用者直接获取到原生的 [事件][] 对象。
 * @author ForteScarlet
 */
interface MiraiSpecialEvent<out E : BotEvent> {
    val event: E
}