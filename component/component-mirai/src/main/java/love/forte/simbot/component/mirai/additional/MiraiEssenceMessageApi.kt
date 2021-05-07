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

package love.forte.simbot.component.mirai.additional

import kotlinx.coroutines.runBlocking
import love.forte.simbot.api.message.assists.Flag
import love.forte.simbot.api.message.events.GroupMsg
import love.forte.simbot.api.message.results.CarrierResult
import love.forte.simbot.api.message.results.toCarrierResult
import love.forte.simbot.component.mirai.message.messageSource
import net.mamoe.mirai.message.data.MessageSource


/**
 * mirai 设置群精华消息API。
 * @author ForteScarlet
 */
public class MiraiEssenceMessageApi(val group: Long, private val source: (Long) -> MessageSource) : MiraiSetterAdditionalApi<CarrierResult<Boolean>> {
    public constructor(group: Long, flag: Flag<GroupMsg.FlagContent>): this(group, { id -> flag.messageSource(id) })



    override val additionalApiName: String
        get() = "EssenceMessage"

    /**
     * 执行设置某个消息为精华消息。
     */
    override fun execute(setterInfo: SetterInfo): CarrierResult<Boolean> {
        // if (flag !is MiraiMessageFlag<*>) {
        //     throw IllegalArgumentException("Mirai only supports setting the essence message through the group Msg.flag under mirai, but type(${flag::class.java})")
        // }
        val bot = setterInfo.bot
        return runBlocking { bot.getGroupOrFail(group).setEssenceMessage(source(bot.id)) }.toCarrierResult()
    }
}