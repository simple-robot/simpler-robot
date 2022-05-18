/*
 *  Copyright (c) 2021-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  本文件是 simply-robot (或称 simple-robot 3.x 、simbot 3.x ) 的一部分。
 *
 *  simply-robot 是自由软件：你可以再分发之和/或依照由自由软件基金会发布的 GNU 通用公共许可证修改之，无论是版本 3 许可证，还是（按你的决定）任何以后版都可以。
 *
 *  发布 simply-robot 是希望它能有用，但是并无保障;甚至连可销售和符合某个特定的目的都不保证。请参看 GNU 通用公共许可证，了解详情。
 *
 *  你应该随程序获得一份 GNU 通用公共许可证的复本。如果没有，请看:
 *  https://www.gnu.org/licenses
 *  https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *  https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 */

package love.forte.simbot

import kotlinx.coroutines.CompletionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import love.forte.simbot.ability.Survivable
import love.forte.simbot.definition.User
import love.forte.simbot.definition.UserInfo
import love.forte.simbot.definition.UserStatus
import love.forte.simbot.event.EventProcessor
import love.forte.simbot.message.Image
import love.forte.simbot.resources.Resource
import love.forte.simbot.utils.runInBlocking
import org.slf4j.Logger
import kotlin.coroutines.CoroutineContext


/**
 *
 * 一个 [Bot]. 同时, [Bot] 也属于一个用户 [User]。
 *
 * [Bot] 是一个活动个体，通过 [BotManager] 构建而来。
 * 其作为一个 [CoroutineScope] 来持有自己的协程上下文。
 *
 * @see LoggerContainer
 * @see ComponentContainer
 * @see FriendsContainer
 * @see GroupsContainer
 * @see GuildsContainer
 *
 * @author ForteScarlet
 */
public interface Bot : User, CoroutineScope, Survivable,
    LoggerContainer, ComponentContainer,
    FriendsContainer, GroupsContainer, GuildsContainer {
    override val coroutineContext: CoroutineContext
    
    /**
     * Bot的唯一标识。此处的唯一标识通常指的是在其所属的 [BotManager] 中的唯一标识，
     * 而不代表其在对应平台系统内的唯一标识。
     *
     * 举个简单的例子，有可能bot的 [id] 指的是某种 `clientId`, 而不是类似于 [User.id] 中的用户ID。
     *
     * 这可能会造成bot的 [id] 和 bot作为 [User] 时的ID不一致的情况，因此如果你希望判断一个bot的id是否为指定值，可以参考使用 [isMe].
     *
     */
    override val id: ID
    override val bot: Bot get() = this
    override val username: String
    override val avatar: String
    override val logger: Logger
    
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
    override val component: Component
    
    /**
     * 当前Bot的用户状态。
     */
    override val status: UserStatus
    
    /**
     * 用于检测一个 [ID] 是否属于当前BOT。一个bot可能会存在多个领域的ID，例如作为bot的client ID和作为user的普通ID。
     */
    public fun isMe(id: ID): Boolean
    
    
    //// image api
    
    /**
     * 上传一个资源作为资源，并在预期内得到一个 [Image] 结果。
     * 这个 [Image] 不一定是真正已经上传后的结果，它有可能只是一个预处理类型。
     * 在执行 [uploadImage] 的过程中也不一定出现真正的挂起行为，具体细节请参考具体实现。
     */
    @JvmSynthetic
    public suspend fun uploadImage(resource: Resource): Image<*>
    
    /**
     * @see uploadImage
     */
    @Api4J
    public fun uploadImageBlocking(resource: Resource): Image<*> = runInBlocking { uploadImage(resource) }
    
    
    /**
     *  尝试通过解析一个 [ID] 并得到对应的可用于发送的图片实例。
     *  这个 [Image] 不一定是真正远端图片结果，它有可能只是一个预处理类型。
     *  在执行 [resolveImage] 的过程中也不一定出现真正的挂起行为，具体细节请参考具体实现。
     */
    public suspend fun resolveImage(id: ID): Image<*>
    
    
    /**
     * @see resolveImage
     */
    @Api4J
    public fun resolveImageBlocking(id: ID): Image<*> = runInBlocking { resolveImage(id) }
    
    
    // self
    /**
     * 真正的启动这个BOT。
     * 但是已经关闭的 [Bot] 无法再次 [start].
     *
     * @return 尚未启动且本次启动成功后得到 `true`。
     */
    @JvmSynthetic
    override suspend fun start(): Boolean
    
    /**
     * 让当前bot挂起当前协程直至其被 [cancel]
     */
    @JvmSynthetic
    override suspend fun join()
    
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
    override suspend fun cancel(reason: Throwable?): Boolean
    
    override fun invokeOnCompletion(handler: CompletionHandler) {
        coroutineContext[Job]?.invokeOnCompletion(handler) ?: throw IllegalStateException("No Job in here.")
    }
    
    /**
     * 是否已经启动过了。
     */
    override val isStarted: Boolean
    
    /**
     * 是否正在运行，即启动后尚未关闭。
     */
    override val isActive: Boolean
    
    /**
     * 是否已经被取消。
     */
    override val isCancelled: Boolean
    
}


/**
 * 一个Bot的信息。同时其也属于一个 [UserInfo].
 */
public interface BotInfo : UserInfo {
    override val id: ID
    override val avatar: String
    override val username: String
}


/**
 *
 * [Bot.isMe] 的取反。
 *
 * @see Bot.isMe
 */
public fun Bot.isNotMe(id: ID): Boolean = !isMe(id)
