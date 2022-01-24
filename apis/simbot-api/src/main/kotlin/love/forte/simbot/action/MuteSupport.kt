/*
 *  Copyright (c) 2022-2022 ForteScarlet <ForteScarlet@163.com>
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
 *
 */

package love.forte.simbot.action

import love.forte.simbot.Api4J
import love.forte.simbot.utils.runInBlocking
import java.util.concurrent.TimeUnit
import kotlin.time.Duration
import kotlin.time.Duration.Companion.microseconds


/**
 *
 * 一个**禁言**行为。
 *
 * 对于禁言，在支持的情况下存在如下场景：
 *
 * 1. 针对一个[成员][love.forte.simbot.definition.Member]，此操作在权限等外在因素允许的情况下，会对其进行 _禁言_ 。
 * 2. 针对一个[组织][love.forte.simbot.definition.Organization]，此操作在权限等外在因素允许的情况下，会对其整体进行 _禁言_ 。
 *
 * 目前来看，禁言可能存在两种形式：
 * 1. 文字方面的禁言，这一般体现在对于一个文字交流[群][love.forte.simbot.definition.Group]或者[频道][love.forte.simbot.definition.Channel].
 * 2. 语音方面的禁言，这一般体现在对于一个通过语音交流的群或频道。
 *
 * 除了上述两种目前已知的常见形式之外，可能会在未来产生新的情况，例如存在允许同时进行语音、文字交流的聊天室、视频聊天的聊天室等。
 *
 * 目前，[MuteSupport] 默认实现于 [love.forte.simbot.definition.Member] 和 [love.forte.simbot.definition.Organization]，
 * 但是api无法保证其实现者能够完美支持此行为，因此在行为不被支持的时候，可能会抛出 [NotSupportActionException] 异常或永远返回一个无效结果。
 *
 *
 * @author ForteScarlet
 */
public interface MuteSupport {

    /**
     * 对当前目标进行 **禁言** 操作。
     *
     * [duration] 代表禁言时长。[duration] 并不能保证其存在效果，对于一些不支持 [duration] 的场景下，
     * 可能会由组件内部通过开启一个延时的异步任务来模拟 [duration] 的效果，但是无论如何，绝大多数情况下，[duration] 的值都不允许小于或等于0。
     *
     * 当组件支持 [duration] 的时候，它们绝大多数情况下都会至少以 **秒** 为单位，除非你明确的知道当前实现能够支持更低量级，否则请尽可能使用秒或更高的单位。
     * 同样的，很多情况下对于 [duration] 的上限也同样有限制，请自行斟酌数值，选取合理范围。
     *
     * @throws NotSupportActionException 当此行为不被支持时
     *
     * @see unmute
     */
    @JvmSynthetic
    public suspend fun mute(duration: Duration): Boolean

    /**
     * 对当前目标进行 **解除禁言** 操作。
     *
     * @throws NotSupportActionException 当此行为不被支持时
     *
     * @see mute
     */
    @JvmSynthetic
    public suspend fun unmute(): Boolean

    /**
     * @see mute
     */
    @Api4J
    public fun muteBlocking(time: Long, unit: TimeUnit): Boolean = runInBlocking {
        mute(unit.toMillis(time).microseconds)
    }

    /**
     * @see unmute
     */
    @Api4J
    public fun unmuteBlocking(): Boolean = runInBlocking {
        unmute()
    }

}


