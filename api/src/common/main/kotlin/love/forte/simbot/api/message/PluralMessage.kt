package love.forte.simbot.api.message

/**
 *
 * 复数的 [Message], 此消息代表其可以在 [Messages] 中重复出现。
 * 但同时，[PluralMessage] 也同样可能出现 冲突 的情况  ?TODO
 *
 * @author ForteScarlet
 */
public interface PluralMessage : AbsoluteMessage {
    override val key: Message.Key<*>
}

/**
 * 用于 [PluralMessage] 的 抽象 [Message.Key].
 *
 * 在默认情况下，[PluralMessageKey] 不与任何消息产生冲突。
 *
 */
public abstract class PluralMessageKey<M : AbsoluteMessage> : Message.Key<M> {
    override fun conflict(key: Message.Key<*>): Boolean = false // TODO
}