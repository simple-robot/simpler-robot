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

package love.forte.simbot.kaiheila

import org.slf4j.Logger
import kotlin.coroutines.CoroutineContext


/**
 * Key.
 * @author ForteScarlet
 */
public class CoroutineLogger(logger: Logger) : CoroutineContext.Element, Logger by logger {
    override val key: CoroutineContext.Key<*> get() = love.forte.simbot.kaiheila.CoroutineLogger.Key

    companion object Key : CoroutineContext.Key<love.forte.simbot.kaiheila.CoroutineLogger>
}