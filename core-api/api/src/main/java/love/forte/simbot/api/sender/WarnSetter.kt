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
import love.forte.common.utils.toCarrier
import love.forte.simbot.LogAble
import love.forte.simbot.api.message.assists.Flag
import love.forte.simbot.api.message.events.FriendAddRequest
import love.forte.simbot.api.message.events.GroupAddRequest
import love.forte.simbot.api.message.events.MessageGet
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit


/**
 * 一个通过Logger输出警告信息的 [Setter.Def] 送信器。
 * @author ForteScarlet
 * @since 2.0.0-BETA.9
 */
public object WarnSetter : LogAble, Setter.Def {
    override val log: Logger = LoggerFactory.getLogger(WarnGetter::class.java)

    private inline fun apiWarn(name: String, def: () -> Any?) {
        log.warn("Setter api {} is not supported. Will return to the default value {}", name, def())
    }

    override suspend fun friendAddRequest(
        flag: Flag<FriendAddRequest.FlagContent>,
        friendRemark: String?,
        agree: Boolean,
        blackList: Boolean,
    ) = apiWarn("setFriendAddRequest") { false }.let { false.toCarrier() }

    override suspend fun groupAddRequest(
        flag: Flag<GroupAddRequest.FlagContent>,
        agree: Boolean,
        blackList: Boolean,
        why: String?,
    ) = apiWarn("setGroupAddRequest") { false }.let { false.toCarrier() }

    override suspend fun groupAdmin(groupCode: String, memberCode: String, promotion: Boolean) =
        apiWarn("setGroupAdmin") { false }.let { false.toCarrier() }

    override suspend fun groupAnonymous(group: String, agree: Boolean): Carrier<Boolean> =
        apiWarn("setGroupAnonymous") { false }.let { false.toCarrier() }

    override suspend fun groupBan(groupCode: String, memberCode: String, time: Long, timeUnit: TimeUnit) =
        apiWarn("setGroupBan") { false }.let { false.toCarrier() }

    override suspend fun groupWholeBan(groupCode: String, mute: Boolean): Carrier<Boolean> =
        apiWarn("setGroupWholeBan") { false }.let { false.toCarrier() }

    override suspend fun groupRemark(groupCode: String, memberCode: String, remark: String?): Carrier<String> =
        apiWarn("setGroupRemark") { null }.let { Carrier.empty() }

    override suspend fun groupQuit(groupCode: String, forcibly: Boolean): Carrier<Boolean> =
        apiWarn("setGroupQuit") { false }.let { false.toCarrier() }

    override suspend fun groupMemberKick(
        groupCode: String,
        memberCode: String,
        why: String?,
        blackList: Boolean,
    ): Carrier<Boolean> = apiWarn("setGroupMemberKick") { false }.let { false.toCarrier() }

    override suspend fun groupMemberSpecialTitle(
        groupCode: String,
        memberCode: String,
        title: String?,
    ): Carrier<String> =
        apiWarn("setGroupMemberSpecialTitle") { null }.let { Carrier.empty() }

    override suspend fun msgRecall(flag: MessageGet.MessageFlag<MessageGet.MessageFlagContent>): Carrier<Boolean> =
        apiWarn("setMsgRecall") { false }.let { false.toCarrier() }

    override suspend fun groupName(groupCode: String, name: String): Carrier<String> =
        apiWarn("setGroupName") { null }.let { Carrier.empty() }

    override suspend fun friendDelete(friend: String): Carrier<Boolean> =
        apiWarn("setFriendDelete") { false }.let { false.toCarrier() }
}