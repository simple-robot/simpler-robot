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

import java.io.InputStream


/**
 * Bot注册器。
 */
public interface BotRegistrar : ComponentContainer {

    /**
     * 根据通用配置信息注册一个BOT。
     * 此信息是从 `.bot` 配置文件中读取而来的 Properties格式文件。
     *
     * 可以考虑直接通过properties序列化进行。
     *
     * 对于任意一个组件，其注册方式可能存在其他任何可能的方式，
     * 但是 [BotManager] 要求实现 [register] 来为 `boot` 模块的自动注册服务。
     *
     * [register] 应当是同步的，直到其真正的验证完毕。
     *
     * @throws NoSuchComponentException 当找不到对应组件信息时.
     * @throws ComponentMismatchException 提供的组件不是当前管理器的组件时. 当且仅当抛出此异常的时候，core boot将不会抛出异常，而是直接忽略。
     * @throws VerifyFailureException 验证出现异常时.
     */
    public fun register(verifyInfo: BotVerifyInfo): Bot
}


public open class ComponentMismatchException : SimbotIllegalArgumentException {
    public constructor() : super()
    public constructor(message: String?) : super(message)
    public constructor(message: String?, cause: Throwable?) : super(message, cause)
    public constructor(cause: Throwable?) : super(cause)
}

public open class VerifyFailureException : SimbotIllegalStateException {
    public constructor() : super()
    public constructor(message: String?) : super(message)
    public constructor(message: String?, cause: Throwable?) : super(message, cause)
    public constructor(cause: Throwable?) : super(cause)
}


/**
 * BOT用于验证身份的信息，通过读取 `.bot` 文件解析而来.
 *
 * [BotVerifyInfo] 为 Json 格式的内容。
 *
 * 此处仅提供获取其输入流的方法.
 *
 */
public interface BotVerifyInfo {

    /**
     * 获取此资源的名称，一般代表其文件名。
     */
    public val infoName: String

    /**
     * 读取其输入流.
     */
    public fun inputStream(): InputStream
}


/**
 *
 * [Bot] 管理器。
 * [BotManager] 应当是 获取、注册 [Bot] 的唯一公开途径，
 * 所有 [BotManager] 均由 [OriginBotManager] 进行管理。
 *
 * [BotManager] 实现 [Survivable], 其存活周期与 [Bot] 无关。
 *
 * @author ForteScarlet
 */
public abstract class BotManager<B : Bot> : BotRegistrar, ComponentContainer, Survivable {
    init {
        if (isBeManaged()) {
            registerSelf()
        }
    }

    // kill warn
    private fun isBeManaged() = beManaged()
    protected open fun beManaged(): Boolean = true

    private fun registerSelf() {
        OriginBotManager.register(this)
    }

    /**
     *
     * 通过 [BotVerifyInfo] 注册并管理一个 [Bot].
     *
     * [BotVerifyInfo] 通常情况下是由组件读取 `*.bot` 文件并提供给当前 [BotManager], 作为用户一般情况下不需要使用函数。
     *
     * [BotManager] 的实现通常需要提供其他额外的友好api来使用。
     *
     * @throws BotAlreadyRegisteredException 出现验证标识冲突的时候
     */
    abstract override fun register(verifyInfo: BotVerifyInfo): Bot

    /**
     * 执行关闭操作。
     * [doCancel] 为当前manager的自定义管理，当前manager关闭后，将会从 [OriginBotManager] 剔除自己。
     */
    @JvmSynthetic
    override suspend fun cancel(reason: Throwable?): Boolean {
        // remove first.
        OriginBotManager.remove(this)
        return doCancel(reason)
    }

    /**
     * 使当前 manager 脱离 [OriginBotManager] 的管理。
     */
    public fun breakAway(): Boolean {
        return OriginBotManager.remove(this)
    }

    /**
     * botManager实现者自定义的close函数，
     * 例如关闭所有的BOT。
     */
    protected abstract suspend fun doCancel(reason: Throwable?): Boolean

    /**
     * 根据Bot的ID获取一个已经注册过的 [Bot]。
     *
     * [Bot] 通过 [BotManager] 进行注册，但是不通过 [BotManager] 进行销毁，而是通过 [Bot.cancel] 进行关闭。
     * 当 [Bot] 关闭后，[BotManager] 中不应能够再获取到此Bot。
     *
     */
    public abstract fun get(id: ID): B?

    /**
     * 获取当前管理器下的所有BOT列表。
     */
    public abstract fun all(): List<B>

}


////

/**
 * 当一个 [BotManager] 中已经存在对应ID的时候[Bot]，通过 [BotManager.register] 将会抛出此异常。
 */
public open class BotAlreadyRegisteredException : SimbotIllegalStateException {
    public constructor() : super()
    public constructor(message: String?) : super(message)
    public constructor(message: String?, cause: Throwable?) : super(message, cause)
    public constructor(cause: Throwable?) : super(cause)
}
