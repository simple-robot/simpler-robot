package love.forte.simbot.bot

import java.util.*

/**
 * 一些标准的，或者说常见的 [BotVerifyInfo] 实例类型。
 *
 * @see PropertiesBotVerifyInfo
 * @see CodeBotVerifyInfo
 * @see CodeVerificationBotVerifyInfo
 * @see TokenBotVerifyInfo
 */
public sealed interface StandardBotVerifyInfo : BotVerifyInfo

/**
 * 基于 [Properties] 的 [BotVerifyInfo].
 */
public open class PropertiesBotVerifyInfo(open val properties: Properties) : StandardBotVerifyInfo {
    override fun get(key: String): String? = properties.getProperty(key)
}


/**
 * 能够提供 [code] 的 [BotVerifyInfo].
 */
public interface CodeBotVerifyInfo : StandardBotVerifyInfo {
    val code: String
}


public data class SimpleCodeBotVerifyInfo(override val code: String) : CodeBotVerifyInfo {
    override fun get(key: String): String? =
        if (key == "code") code else null
}


/**
 * 能够提供 [code] 与 [verification] 的 [BotVerifyInfo].
 */
public interface CodeVerificationBotVerifyInfo : CodeBotVerifyInfo {
    override val code: String
    val verification: String
}

public data class SimpleCodeVerificationBotVerifyInfo(
    override val code: String,
    override val verification: String,
) :
    CodeVerificationBotVerifyInfo {
    override fun get(key: String): String? =
        when (key) {
            "code" -> code
            "verification", "password" -> verification
            else -> null
        }
}


/**
 * 需要提供Token的 [BotVerifyInfo]
 */
public interface TokenBotVerifyInfo : StandardBotVerifyInfo {
    val token: String
}

public data class SimpleTokenBotVerifyInfo(override val token: String) : TokenBotVerifyInfo {
    override fun get(key: String): String? =
        if (key == "token") token else null
}






