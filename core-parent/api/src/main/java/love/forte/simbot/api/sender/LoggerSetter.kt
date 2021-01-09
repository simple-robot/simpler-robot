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

package love.forte.simbot.api.sender

import love.forte.simbot.LogAble
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.event.Level


/**
 * TODO
 * 一个通过Logger输出警告信息的 [Setter.Def] 送信器。
 * @author ForteScarlet
 * @since 2.0.0-BETA.9
 */
public class LoggerSetter(
    private val logLevel: Level,
    override val log: Logger = LoggerFactory.getLogger(LoggerGetter::class.java),
) : LogAble {

}