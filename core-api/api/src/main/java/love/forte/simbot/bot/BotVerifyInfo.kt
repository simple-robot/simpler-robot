/*
 *
 *  * Copyright (c) 2021. ForteScarlet All rights reserved.
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

@file:JvmName("BotVerifyInfos")
@file:Suppress("unused")

package love.forte.simbot.bot

import love.forte.simbot.SimbotIllegalStateException
import love.forte.simbot.bot.BotVerifyInfo.Companion.withCode
import love.forte.simbot.bot.BotVerifyInfo.Companion.withCodeVerification
import love.forte.simbot.bot.BotVerifyInfo.Companion.withToken
import love.forte.simbot.mark.Since
import java.util.*

/**
 * Bot 验证信息接口。
 *
 * 为了支持更灵活的bot验证机制，提供此接口以代替曾经的 code-verification 验证方式。
 *
 * 账号配置时类似于 `properties` 等键值对格式的配置方式，并解析为 [BotVerifyInfo] 实例。
 *
 * [BotVerifyInfo] 主要通过读取对应的bot配置资源文件得到，格式为 `.bot`, 其规则等同于properties。
 * 通常情况下，一个bot配置文件则对应一个 [BotVerifyInfo] 实例。
 *
 * Java 可以通过 `BotVerifyInfos` 工具类下相关方法来获取 [BotVerifyInfo] 实例，或者直接实现接口。
 * 也可以通过 [withCode] [withCodeVerification] [withToken] 获取。
 *
 *
 * @see StandardBotVerifyInfo
 *
 *
 *
 * @since 2.1.0
 */
@Since.SinceList(
    Since("2.1.0"),
    Since(value = "2.3.0", desc = ["不再提供code, 需要由组件自行取用并验证唯一标识。"])
)
public interface BotVerifyInfo {
    /**
     * 一个获取额外扩展参数的方法，可以得到一些其他参数。
     */
    operator fun get(key: String): String?

    companion object {
        @JvmStatic
        fun withCodeVerification(code: String, verification: String): CodeVerificationBotVerifyInfo =
            SimpleCodeVerificationBotVerifyInfo(code, verification)

        @JvmStatic
        fun withCode(code: String): CodeBotVerifyInfo = SimpleCodeBotVerifyInfo(code)

        @JvmStatic
        fun withToken(token: String): TokenBotVerifyInfo = SimpleTokenBotVerifyInfo(token)

        @JvmStatic
        fun withProperties(prop: Properties): PropertiesBotVerifyInfo = PropertiesBotVerifyInfo(prop)
    }


}


/**
 * 根据[多个key][keys]寻找一个符合结果的值。
 */
public fun BotVerifyInfo.findOrNull(vararg keys: String): String? {
    for (key in keys) {
        val found = this[key]
        if (found != null) return found
    }
    return null
}

/**
 *
 * @throws SimbotIllegalStateException 如果找不到任何的值
 */
public fun BotVerifyInfo.find(vararg keys: String): String {
    return findOrNull(*keys) ?: throw SimbotIllegalStateException("Cannot found value of keys: ${
        keys.joinToString(", ",
            "[",
            "]")
    }")
}


/**
 * 一般可代表为一个 `账号` 信息。
 *
 * 一个账号信息必须存在， 因为 [code] 将会作为一个bot的唯一ID。
 *
 */
public val BotVerifyInfo.code: String
    get() =
        if (this is CodeBotVerifyInfo) this.code
        else find("code")

/**
 * 一般指一个用于验证的信息，常见含义为 **密码**。
 */
public val BotVerifyInfo.verification: String?
    get() =
        if (this is CodeVerificationBotVerifyInfo) this.verification
        else findOrNull("verification", "password")


/**
 * 一般指一个用于验证的信息Token。
 */
public val BotVerifyInfo.token: String?
    get() =
        if (this is TokenBotVerifyInfo) this.token
        else get("token")


/**
 * 此bot所对应的组件。
 * 为未来版本预留的属性。
 */
public val BotVerifyInfo.component: String? get() = findOrNull("component")


internal val CODE_ALIAS = arrayOf("code")
internal val VERIFICATION_ALIAS = arrayOf("verification", "password")

/**
 * 获取一个基于[Map]的键值对实例。
 */
@JvmName("getPairInstance")
@JvmOverloads
fun pairBotVerifyInfo(
    map: Map<String, Any?>,
    codeAlias: Array<String> = CODE_ALIAS,
    verificationAlias: Array<String> = VERIFICATION_ALIAS,
): PairBotVerifyInfo {
    return PairBotVerifyInfo.MapPairBotVerifyInfo(map, codeAlias, verificationAlias)
}

/**
 * 获取一个基于[Properties]的键值对实例。
 */
@JvmName("getPairInstance")
@JvmOverloads
fun pairBotVerifyInfo(
    prop: Properties,
    codeAlias: Array<String> = CODE_ALIAS,
    verificationAlias: Array<String> = VERIFICATION_ALIAS,
): PairBotVerifyInfo {
    return PairBotVerifyInfo.PropertiesPairBotVerifyInfo(prop, codeAlias, verificationAlias)
}

/**
 * 只提供 [code] 与 [verification] 的 [BotVerifyInfo] 实现。[get] 将会失效。
 *
 */
@JvmName("getInstance")
fun botVerifyInfo(code: String, verification: String?): BotVerifyInfo {
    return if (verification == null) CodeOnlyBotVerifyInfo(code)
    else PairBotVerifyInfo.BasicBotVerifyInfo(code, verification)
}

private val SPLIT_REGEX = Regex(":")

/**
 * 转义经过逗号切割的字符串，其中应为 “xxx:xxx”的格式。
 *
 *
 * `code` 与 `verification` 出现以下内容会进行反转义：
 *
 * - `&nbsp;` -> `&`
 * - `&#44;` -> `,`
 * - `&#58;` -> `:`
 *
 */
@JvmName("getInstanceBySplit")
fun botVerifyInfoBySplit(configTextPair: String): BotVerifyInfo {
    // 切割后转义
    val split = configTextPair.split(SPLIT_REGEX, 2)
    return PairBotVerifyInfo.BasicBotVerifyInfo(
        CoreBotsDecoder.decoder(split[0]),
        if (split.size > 1) {
            CoreBotsDecoder.decoder(split[1])
        } else ""
    )
}


/**
 * 基于键值对的 [BotVerifyInfo] 基础实现，通过 [Map] 或者 [Properties] 进行实现。
 *
 *
 */
public sealed class PairBotVerifyInfo(
    /** 获取账号的别名。用于 [code] 从 [get] 函数中的获取。 */
    private val codeAlias: Array<String>,
    /** 获取验证信息的别名。用于 [verification] 从 [get] 函数中的获取。 */
    private val verificationAlias: Array<String>,
) : CodeVerificationBotVerifyInfo {

    override val code: String
        get() = find(*codeAlias)


    override val verification: String
        get() = find(*verificationAlias)

    /**
     * 基于 [Properties] 的 [PairBotVerifyInfo] 实现。
     */
    internal class PropertiesPairBotVerifyInfo(
        private val prop: Properties,
        codeAlias: Array<String> = CODE_ALIAS,
        verificationAlias: Array<String> = VERIFICATION_ALIAS,
    ) : PairBotVerifyInfo(codeAlias, verificationAlias) {
        override fun get(key: String): String? = prop.getProperty(key)
    }


    /**
     * 基于 [Map] 的 [PairBotVerifyInfo] 实现。
     */
    internal class MapPairBotVerifyInfo(
        private val map: Map<String, Any?>,
        codeAlias: Array<String> = CODE_ALIAS,
        verificationAlias: Array<String> = VERIFICATION_ALIAS,
    ) : PairBotVerifyInfo(codeAlias, verificationAlias) {
        override fun get(key: String): String? = map[key]?.toString()
    }

    /**
     * 基础的 [PairBotVerifyInfo] 实现。
     */
    internal class BasicBotVerifyInfo(override val code: String, override val verification: String) :
        PairBotVerifyInfo(emptyArray(), emptyArray()) {
        override fun get(key: String): String? = null
    }


}

/**
 * 没有任何信息的 [BotVerifyInfo] 实现。
 */
internal data class CodeOnlyBotVerifyInfo(override val code: String) : CodeBotVerifyInfo {
    override fun get(key: String): String? = null
}


/**
 *
 * 使用新的多文件配置后，使用 [BotVerifyInfo] 下的 [PairBotVerifyInfo] 来进行注册。
 *
 * @see BotVerifyInfo
 * @see PairBotVerifyInfo
 *
 * @property code 一般指账号信息。
 * @property verification 验证信息。一般可以代表账号的密码或者上报路径的链接。
 *
 * @since 2.0.0 初版注册信息, 其仅支持两个主要参数灵活性较差
 *
 */
@Deprecated("2.1.0开始建议使用新的方式")
public data class BotRegisterInfo(override val code: String, override val verification: String) :
    CodeVerificationBotVerifyInfo {
    override fun get(key: String): String? = null


    companion object {
        // private val SPLIT_REGEX = Regex(":")


        /**
         * @see botVerifyInfoBySplit
         */
        @Deprecated("使用新的BotVerifyInfos", ReplaceWith("botVerifyInfoBySplit(configTextPair)"))
        @JvmStatic
        public fun splitTo(configTextPair: String): BotVerifyInfo = botVerifyInfoBySplit(configTextPair)
    }
}