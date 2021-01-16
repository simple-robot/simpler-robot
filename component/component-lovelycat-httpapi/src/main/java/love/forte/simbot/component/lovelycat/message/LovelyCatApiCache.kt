/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  simple-robot-S
 *  * File     LovelyCatApiCache.kt
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

package love.forte.simbot.component.lovelycat.message


/**
 * 针对 [love.forte.simbot.component.lovelycat.LovelyCatApiTemplate] 中部分接口的缓存方案。
 *
 * 缓存中的各种 **计算** 建议使用同步锁而不是CAS以避免出现多次重复请求.
 *
 */
public interface LovelyCatApiCache {

    /**
     * 获取缓存或计算 [RobotName].
     */
    fun computeBotName(compute: () -> RobotName): RobotName

    /**
     * 清除 [RobotName] 缓存。
     */
    fun cleanBotName()

    /**
     * 获取缓存或计算 [RobotHeadImgUrl].
     */
    fun computeBotHeadImgUrl(compute: () -> RobotHeadImgUrl): RobotHeadImgUrl

    /**
     * 清除 [RobotHeadImgUrl] 缓存。
     */
    fun cleanBotHeadImgUrl()

    /**
     * 获取缓存或计算 [LoggedAccountList].
     */
    fun computeLoggedAccountList(compute: () -> LoggedAccountList): LoggedAccountList

    /**
     * 清除 [LoggedAccountList] 缓存。
     */
    fun cleanLoggedAccountList()

    /**
     * 获取缓存或计算 [CatFriendInfo] 列表。
     */
    fun computeCatFriendInfoList(compute: () -> List<CatFriendInfo>): List<CatFriendInfo>

    /**
     * 清除 [CatFriendInfo] 缓存。
     */
    fun cleanCatFriendInfoList()

    /**
     * 获取缓存或计算 [CatGroupInfo] 列表。
     */
    fun computeCatGroupInfoList(compute: () -> List<CatGroupInfo>): List<CatGroupInfo>

    /**
     * 清除 [CatGroupInfo] 缓存。
     */
    fun cleanCatGroupInfoList()

    /**
     * 获取缓存或计算 [CatGroupMemberInfo]。
     */
    fun computeCatGroupMemberInfo(compute: () -> CatGroupMemberInfo): CatGroupMemberInfo

    /**
     * 清除 [CatGroupMemberInfo] 缓存。
     */
    fun cleanCatGroupMemberInfo()

    /**
     * 获取缓存或计算 [CatSimpleGroupMemberInfo] 列表。
     */
    fun computeCatSimpleGroupMemberInfoList(compute: () -> List<CatSimpleGroupMemberInfo>): List<CatSimpleGroupMemberInfo>

    /**
     * 清除 [CatSimpleGroupMemberInfo] 缓存。
     */
    fun cleanCatSimpleGroupMemberInfoList()
}


/**
 * 虚假的 [LovelyCatApiCache] 实例，即每次获取实际上都是在直接计算。
 */
public object FakeLovelyCatApiCache : LovelyCatApiCache {
    override fun computeBotName(compute: () -> RobotName): RobotName = compute()
    override fun computeBotHeadImgUrl(compute: () -> RobotHeadImgUrl): RobotHeadImgUrl = compute()
    override fun computeLoggedAccountList(compute: () -> LoggedAccountList): LoggedAccountList = compute()
    override fun computeCatFriendInfoList(compute: () -> List<CatFriendInfo>): List<CatFriendInfo> = compute()
    override fun computeCatGroupInfoList(compute: () -> List<CatGroupInfo>): List<CatGroupInfo> = compute()
    override fun computeCatGroupMemberInfo(compute: () -> CatGroupMemberInfo): CatGroupMemberInfo = compute()
    override fun computeCatSimpleGroupMemberInfoList(compute: () -> List<CatSimpleGroupMemberInfo>): List<CatSimpleGroupMemberInfo> = compute()
    override fun cleanBotName() { }
    override fun cleanBotHeadImgUrl() { }
    override fun cleanLoggedAccountList() { }
    override fun cleanCatFriendInfoList() { }
    override fun cleanCatGroupInfoList() { }
    override fun cleanCatGroupMemberInfo() { }
    override fun cleanCatSimpleGroupMemberInfoList() { }
}
