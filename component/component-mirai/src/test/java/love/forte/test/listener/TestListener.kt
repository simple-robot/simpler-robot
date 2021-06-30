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

package love.forte.test.listener

import kotlinx.coroutines.CoroutineDispatcher
import love.forte.common.ioc.annotation.Beans
import love.forte.simbot.annotation.Filter
import love.forte.simbot.annotation.Filters
import love.forte.simbot.annotation.OnGroup
import love.forte.simbot.api.message.events.GroupMsg
import love.forte.simbot.api.sender.Sender
import kotlin.coroutines.coroutineContext


/**
 *
 * @author ForteScarlet
 */
@Beans
class TestListener {

    @OptIn(ExperimentalStdlibApi::class)
    @Filters(value = [
        Filter(".h1", trim = true),
        Filter(".h2", trim = true),
        Filter(".h3", trim = true)],
        bots = ["2370606773"],
        groups = ["703454734"]
    )
    @OnGroup
    suspend fun GroupMsg.listen(sender: Sender) {
        println(coroutineContext[CoroutineDispatcher])
        println("${this.botInfo}: $this")
        sender.sendGroupMsg(this, "Yes!")
    }

}
