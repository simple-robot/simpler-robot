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

import love.forte.simbot.component.lovelycat.utils.LazyTimeLimitCache


/**
 * 可爱猫组件读写缓存器。
 * @author ForteScarlet
 */
public class ReadWriteLovelyCatApiCache(time: Long) : LovelyCatApiCache {

    private val botNameCache = LazyTimeLimitCache<RobotName>(time)
    private val botHeadImgUrlCache = LazyTimeLimitCache<RobotHeadImgUrl>(time)
    private val loggedAccountListCache = LazyTimeLimitCache<LoggedAccountList>(time)
    private val catFriendInfoListCache = LazyTimeLimitCache<List<CatFriendInfo>>(time)
    private val catGroupInfoListCache = LazyTimeLimitCache<List<CatGroupInfo>>(time)
    private val catGroupMemberInfoCache = LazyTimeLimitCache<CatGroupMemberInfo>(time)
    private val catSimpleGroupMemberInfoListCache = LazyTimeLimitCache<List<CatSimpleGroupMemberInfo>>(time)

    override fun computeBotName(compute: () -> RobotName): RobotName = botNameCache.compute(compute)

    override fun cleanBotName() {
        botNameCache.clean()
    }

    override fun computeBotHeadImgUrl(compute: () -> RobotHeadImgUrl): RobotHeadImgUrl = botHeadImgUrlCache.compute(compute)

    override fun cleanBotHeadImgUrl() {
        botHeadImgUrlCache.clean()
    }

    override fun computeLoggedAccountList(compute: () -> LoggedAccountList): LoggedAccountList = loggedAccountListCache.compute(compute)

    override fun cleanLoggedAccountList() {
        loggedAccountListCache.clean()
    }

    override fun computeCatFriendInfoList(compute: () -> List<CatFriendInfo>): List<CatFriendInfo> = catFriendInfoListCache.compute(compute)

    override fun cleanCatFriendInfoList() {
        catFriendInfoListCache.clean()
    }

    override fun computeCatGroupInfoList(compute: () -> List<CatGroupInfo>): List<CatGroupInfo> = catGroupInfoListCache.compute(compute)

    override fun cleanCatGroupInfoList() {
        catGroupInfoListCache.clean()
    }

    override fun computeCatGroupMemberInfo(compute: () -> CatGroupMemberInfo): CatGroupMemberInfo = catGroupMemberInfoCache.compute(compute)

    override fun cleanCatGroupMemberInfo() {
        catGroupMemberInfoCache.clean()
    }

    override fun computeCatSimpleGroupMemberInfoList(compute: () -> List<CatSimpleGroupMemberInfo>): List<CatSimpleGroupMemberInfo> = catSimpleGroupMemberInfoListCache.compute(compute)

    override fun cleanCatSimpleGroupMemberInfoList() {
        catSimpleGroupMemberInfoListCache.clean()
    }
}