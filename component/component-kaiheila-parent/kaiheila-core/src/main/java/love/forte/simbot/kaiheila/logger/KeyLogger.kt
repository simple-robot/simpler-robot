/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  simpler-robot
 *  * File     KeyLogger.kt
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

package love.forte.simbot.kaiheila.logger

import love.forte.simbot.LogAble
import love.forte.simbot.kaiheila.api.ApiData
import org.slf4j.Logger
import org.slf4j.LoggerFactory


/**
 *
 * @author ForteScarlet
 */
public abstract class KeyLogger(
    override val id: String,
    override val log: Logger,
) : LogAble, ApiData.Req.Key


public abstract class TypedKeyLogger(
    id: String,
    type: Class<*>,
) : KeyLogger(id, LoggerFactory.getLogger(type))

public abstract class NamedKeyLogger(
    id: String,
    vararg loggerName: String,
) : KeyLogger(id,
    LoggerFactory.getLogger(
        loggerName.takeIf { it.isNotEmpty() }?.joinToString(".")
            ?: throw IllegalArgumentException("NamedLogger's name was empty.")
    )
)