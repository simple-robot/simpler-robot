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

import love.forte.common.utils.Carrier
import love.forte.simbot.LogAble
import love.forte.simbot.api.message.assists.Flag
import love.forte.simbot.api.message.events.GroupMsg
import love.forte.simbot.api.message.events.PrivateMsg
import org.slf4j.Logger
import org.slf4j.LoggerFactory


/**
 * 一个通过Logger输出警告信息的 [Sender.Def] 送信器。
 * @author ForteScarlet
 * @since 2.0.0-BETA.9
 */
@Suppress("DEPRECATION", "OverridingDeprecatedMember")
public object WarnSender : LogAble, Sender.Def {
    override val log: Logger = LoggerFactory.getLogger(WarnGetter::class.java)

    private inline fun apiWarn(name: String, def: () -> Any?) {
        log.warn("Sender api {} is not supported. Will return to the default value {}", name, def())
    }

    override suspend fun groupMsg(
        parent: String?,
        group: String,
        msg: String,
    ): Carrier<out Flag<GroupMsg.FlagContent>> = apiWarn("sendGroupMsg") { null }.let { Carrier.empty() }

    override suspend fun privateMsg(
        code: String,
        group: String?,
        msg: String,
    ): Carrier<out Flag<PrivateMsg.FlagContent>> =
        apiWarn("sendPrivateMsg") { null }.let { Carrier.empty() }

    override suspend fun groupNotice(
        group: String,
        title: String?,
        text: String?,
        popUp: Boolean,
        top: Boolean,
        toNewMember: Boolean,
        confirm: Boolean,
    ): Carrier<Boolean> = apiWarn("sendGroupNotice") { null }.let { Carrier.empty() }

    override fun sendGroupSign(group: String, title: String, message: String): Carrier<Boolean> =
        apiWarn("sendGroupSign") { null }.let { Carrier.empty() }
}