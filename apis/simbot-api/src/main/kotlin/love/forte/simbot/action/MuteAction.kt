package love.forte.simbot.action

import kotlinx.coroutines.runBlocking
import love.forte.simbot.Api4J


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
 * 目前，[MuteAction] 默认实现于 [love.forte.simbot.definition.Member] 和 [love.forte.simbot.definition.Organization]，
 * 但是api无法保证其实现者能够完美支持此行为，因此在行为不被支持的时候，可能会抛出 [NotSupportActionException] 异常或永远返回一个无效结果。
 *
 *
 * @author ForteScarlet
 */
public interface MuteAction {

    /**
     * 对当前目标进行 **禁言** 操作。
     *
     * @throws NotSupportActionException 当此行为不被支持时
     */
    @JvmSynthetic
    public suspend fun mute(): Boolean


    @Api4J
    public fun muteBlocking(): Boolean = runBlocking { mute() }
}


