package love.forte.simbot.common.api.message.results

import love.forte.simbot.common.api.message.containers.AccountContainer
import love.forte.simbot.common.api.message.containers.GroupInfo


/**
 * 简单的群信息，只包含了[群基础信息][GroupInfo]
 */
public interface SimpleGroupInfo : Result, GroupInfo

/**
 *
 * 获取到的群详细信息。除了 [群简单信息][SimpleGroupInfo]以外，还有一些其他的信息。
 *
 * @author ForteScarlet -> https://github.com/ForteScarlet
 */
public interface GroupFullInfo : SimpleGroupInfo {

    /**
     * 群人数上限
     */
    val maximum: Int

    /**
     * 当前群人数
     */
    val total: Int

    /**
     * 建群时间
     */
    val createTime: Long

    /**
     * 简略介绍
     */
    val simpleIntroduction: String?

    /**
     * 完整介绍
     */
    val fullIntroduction: String?


    /**
     * 群主信息
     */
    val owner: GroupOwner


    /**
     * 群管理员列表
     */
    val admins: List<GroupAdmin>
}

/**
 * 群列表，得到一些群的基础信息
 */
public interface GroupList : MultipleResults<SimpleGroupInfo>


/**
 * 群主信息。
 * @see GroupOwnerImpl
 */
public interface GroupOwner : AccountContainer

/**
 * 通过一个 [AccountContainer] 来构建 [GroupOwner] 实例
 */
public data class GroupOwnerImpl(private val account: AccountContainer) : GroupOwner, AccountContainer by account


/**
 * 管理员信息
 * @see GroupAdminImpl
 */
public interface GroupAdmin : AccountContainer

/**
 * 通过一个 [AccountContainer] 来构建 [GroupAdmin] 实例
 */
public data class GroupAdminImpl(private val account: AccountContainer) : GroupAdmin, AccountContainer by account
