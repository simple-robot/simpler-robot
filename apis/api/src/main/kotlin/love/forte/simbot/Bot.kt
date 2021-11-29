/*
 *  Copyright (c) 2021-2021 ForteScarlet <https://github.com/ForteScarlet>
 *
 *  根据 Apache License 2.0 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */

package love.forte.simbot

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import love.forte.simbot.definition.*
import love.forte.simbot.event.EventProcessor
import love.forte.simbot.message.Image
import love.forte.simbot.resources.Resource
import kotlin.coroutines.CoroutineContext


/**
 *
 * 一个 [Bot]. 同时, [Bot] 也属于一个用户 [User]。
 *
 * Bot是一个活动个体，通过 [BotManager] 构建而来。
 * 其作为一个 [CoroutineScope] 来持有自己的协程上下文。
 *
 * @author ForteScarlet
 */
public interface Bot : User, CoroutineScope {
    override val coroutineContext: CoroutineContext

    override val id: ID
    override val bot: Bot get() = this
    override val username: String
    override val avatar: String

    /**
     * 每个bot都肯定会由一个 [BotManager] 进行管理。
     *
     */
    public val manager: BotManager<out Bot>

    /**
     * 对于一个Bot，其应当存在一个事件处理器。
     */
    public val eventProcessor: EventProcessor

    /**
     * 每个Bot都有一个所属组件。
     *
     */
    public val component: Component

    /**
     * 当前Bot的用户状态。
     */
    override val status: UserStatus

    // friends
    /**
     * 根据分组和限流信息得到此bot下的好友列表。
     *
     *
     * *分组不一定存在，限流器也不一定生效，这两个参数的有效情况取决于当前 [Bot] 的实现情况。*
     *
     */
    @JvmSynthetic
    public suspend fun friends(grouping: Grouping, limiter: Limiter): Flow<Friend>

    public fun getFriends(grouping: Grouping, limiter: Limiter): List<Friend> {
        return runBlocking { friends(grouping, limiter).toList() }
    }


    // organizations
    /**
     * 获取群列表。
     *
     * *分组不一定存在，限流器也不一定生效，这两个参数的有效情况取决于当前 [Bot] 的实现情况。*
     */
    @JvmSynthetic
    public suspend fun groups(grouping: Grouping, limiter: Limiter): Flow<Group>

    public fun getGroups(grouping: Grouping, limiter: Limiter): List<Group> {
        return runBlocking { groups(grouping, limiter).toList() }
    }

    /**
     * 获取当前的所有频道服务器列表
     *
     * *分组不一定存在，限流器也不一定生效，这两个参数的有效情况取决于当前 [Bot] 的实现情况。*
     */
    @JvmSynthetic
    public suspend fun guilds(grouping: Grouping, limiter: Limiter): Flow<Group>

    public fun getGuilds(grouping: Grouping, limiter: Limiter): List<Group> {
        return runBlocking { guilds(grouping, limiter).toList() }
    }

    // resources

    /**
     * 上传一个资源作为资源，并在预期内得到一个 [Image] 结果。
     */
    @JvmSynthetic
    public suspend fun uploadImage(resource: Resource): Image


    // public suspend fun uploadFile(resource: Resource): File


    // self
    /**
     * 真正的启动这个BOT。
     * 但是已经关闭的 [Bot] 无法再次 [start].
     *
     * @return 尚未启动且本次启动成功后得到 `true`。
     */
    @JvmSynthetic
    public suspend fun start(): Boolean


    /**
     * 让当前bot挂起当前协程直至其被 [cancel]
     */
    @JvmSynthetic
    public suspend fun join()

    /**
     * 关闭此Bot。
     *
     * 当此 [Bot] 关闭后，应当同时从对应的 [manager] 中移除。
     *
     *
     * @return 已经启动、尚未关闭且本次关闭成功则得到 `true`。
     *
     */
    @JvmSynthetic
    public suspend fun cancel(): Boolean

    /**
     * 是否已经启动过了。
     */
    public val isStarted: Boolean

    /**
     * 是否正在运行，即启动后尚未关闭。
     */
    public val isActive: Boolean

    /**
     * 是否已经被取消。
     */
    public val isCancelled: Boolean

}

/**
 * 一个Bot的信息。同时其也属于一个 [UserInfo].
 */
public interface BotInfo : UserInfo {
    override val id: ID
    override val avatar: String
    override val username: String
}