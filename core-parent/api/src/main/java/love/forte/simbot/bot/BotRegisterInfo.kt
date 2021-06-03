/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  component-onebot
 *  * File     BotRegisterInfo.kt
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

@file:JvmName("BotVerifyInfos")

package love.forte.simbot.bot

import java.util.*

/**
 * Bot 验证信息接口。
 *
 * 为了支持更灵活的bot验证机制，提供此接口以代替曾经的 code-verification 验证方式。
 *
 * 除了提供了 [code]、[verification] 的参数之外，也提供了一个 [get] 方法支持获取额外的参数。
 *
 * 账号配置时类似于 `properties` 等键值对格式的配置方式，并解析为 [BotVerifyInfo] 实例。
 *
 *
 * [BotVerifyInfo] 主要通过读取对应的bot配置资源文件得到，例如 `*.properties` 或 `*.yml`文件。
 * 通常情况下，一个bot配置文件则对应一个 [BotVerifyInfo] 实例。
 *
 *
 * @since 2.1.0
 */
public interface BotVerifyInfo {
    /**
     * 一般可代表为一个 `账号` 信息。
     *
     * 一个账号信息必须存在， 因为 [code] 将会作为一个bot的唯一ID。
     *
     */
    val code: String

    /**
     * 一般指一个用于验证的信息，常见含义为 **密码**。
     */
    val verification: String?

    /**
     * 一个获取额外扩展参数的方法，可以得到一些其他参数。
     */
    operator fun get(key: String): String?
}


internal val CODE_ALIAS = arrayOf("code")
internal val VERIFICATION_ALIAS = arrayOf("verification")

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
 */
public sealed class PairBotVerifyInfo(
    /** 获取账号的别名。用于 [code] 从 [get] 函数中的获取。 */
    private val codeAlias: Array<String>,
    /** 获取验证信息的别名。用于 [verification] 从 [get] 函数中的获取。 */
    private val verificationAlias: Array<String>,
) : BotVerifyInfo {

    override val code: String
        get() {
            for (c in codeAlias) {
                val got = this[c]
                if (got != null) return got
            }
            throw IllegalStateException("The code value was not found in the ${codeAlias.joinToString(", ")}.")
        }

    override val verification: String?
        get() {
            for (v in verificationAlias) {
                val got = this[v]
                if (got != null) return got
            }
            return null
        }

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
    internal class BasicBotVerifyInfo(override val code: String, override val verification: String?) :
        PairBotVerifyInfo(emptyArray(), emptyArray()) {
        override fun get(key: String): String? = null
    }


}

/**
 * 没有任何信息的 [BotVerifyInfo] 实现。
 */
internal data class CodeOnlyBotVerifyInfo(override val code: String) : BotVerifyInfo {
    override val verification: String? get() = null
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
public data class BotRegisterInfo(override val code: String, override val verification: String) : BotVerifyInfo {
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