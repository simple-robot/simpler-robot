@file:JvmName("GroupUtil")

package love.forte.simbot.kaiheila.api.v3.utils

import love.forte.simbot.api.message.results.GroupList
import love.forte.simbot.api.message.results.SimpleGroupInfo
import love.forte.simbot.kaiheila.api.v3.guild.GuildListRespData


public fun List<GuildListRespData>.asGroupList(limit: Int = -1): GroupList {
    val list = if (limit > 0) this.take(limit) else this
    return GuildGroupList(list)
}

private class GuildGroupList(
    override val results: List<SimpleGroupInfo>,
) : GroupList {
    override val originalData: String
        get() = results.toString()

}