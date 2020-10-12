/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     MiraiGroupList.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.simbot.component.mirai.message.result

import love.forte.simbot.core.api.message.results.GroupList
import love.forte.simbot.core.api.message.results.SimpleGroupInfo
import net.mamoe.mirai.Bot


/**
 * mirai [GroupList] 实现。
 */
public class MiraiGroupList(bot: Bot, limit: Int = -1) : GroupList {
    override val results: List<SimpleGroupInfo> by lazy(LazyThreadSafetyMode.NONE) {
        if(limit > 0) {
            bot.groups.asSequence().take(limit).map { MiraiGroupInfo(it) }.toList()
        } else {
            bot.groups.map { MiraiGroupInfo(it) }
        }
    }

    override val originalData: String = "MiraiGroupList(bot=$bot)"
}