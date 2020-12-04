/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
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

package love.forte.simbot.component.lovelycat.message

import love.forte.simbot.ReadWriteLock
import java.util.concurrent.locks.ReentrantReadWriteLock


/**
 *
 * @author ForteScarlet
 */
public class ReadWriteLovelyCatApiCache(private val time: Long) : LovelyCatApiCache {


    override fun computeBotName(compute: () -> RobotName): RobotName {
        TODO("Not yet implemented")
    }

    override fun cleanBotName() {
        TODO("Not yet implemented")
    }

    override fun computeBotHeadImgUrl(compute: () -> RobotHeadImgUrl): RobotHeadImgUrl {
        TODO("Not yet implemented")
    }

    override fun cleanBotHeadImgUrl() {
        TODO("Not yet implemented")
    }

    override fun computeLoggedAccountList(compute: () -> LoggedAccountList): LoggedAccountList {
        TODO("Not yet implemented")
    }

    override fun cleanLoggedAccountList() {
        TODO("Not yet implemented")
    }

    override fun computeCatFriendInfoList(compute: () -> List<CatFriendInfo>): List<CatFriendInfo> {
        TODO("Not yet implemented")
    }

    override fun cleanCatFriendInfoList() {
        TODO("Not yet implemented")
    }

    override fun computeCatGroupInfoList(compute: () -> List<CatGroupInfo>): List<CatGroupInfo> {
        TODO("Not yet implemented")
    }

    override fun cleanCatGroupInfoList() {
        TODO("Not yet implemented")
    }

    override fun computeCatGroupMemberInfo(compute: () -> CatGroupMemberInfo): CatGroupMemberInfo {
        TODO("Not yet implemented")
    }

    override fun cleanCatGroupMemberInfo() {
        TODO("Not yet implemented")
    }

    override fun computeCatSimpleGroupMemberInfoList(compute: () -> List<CatSimpleGroupMemberInfo>): List<CatSimpleGroupMemberInfo> {
        TODO("Not yet implemented")
    }

    override fun cleanCatSimpleGroupMemberInfoList() {
        TODO("Not yet implemented")
    }
}