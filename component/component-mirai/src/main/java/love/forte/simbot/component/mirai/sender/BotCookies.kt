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
@file:JvmName("MiraiBotCookies")

package love.forte.simbot.component.mirai.sender

import love.forte.simbot.api.SimbotExperimentalApi
import love.forte.simbot.thing.StructuralThingWithName
import love.forte.simbot.thing.StructuralThingWithNameBuilder
import love.forte.simbot.thing.findValue
import love.forte.simbot.thing.resolveToMap
import net.mamoe.mirai.Bot
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.reflect.Method
import java.nio.charset.Charset
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaType

/*
    通过一个bot得到cookie信息
 */

internal val logger: Logger = LoggerFactory.getLogger("love.forte.simbot.component.mirai.MiraiBotCookies")

/**
 * 通过mirai安卓协议的[net.mamoe.mirai.internal.QQAndroidBot]得到部分敏感信息.
 * 无法保证此类永远可用。此类的信息依赖于mirai的内部代码构造。
 * 最后测试可用版本 `2.0-M2-2`
 *
 * @see net.mamoe.mirai.internal.QQAndroidBot
 * @see net.mamoe.mirai.internal.network.QQAndroidClient
 * @see net.mamoe.mirai.internal.network.WLoginSigInfo
 *
 */
@SimbotExperimentalApi("不建议外部使用此类")
object UnsafeViolenceAndroidBotCookieUtils {
    private const val MIRAI_PACKAGE = "net.mamoe.mirai.internal"
    private var success: Boolean = false
    private lateinit var botClientGetter: Method

    // private lateinit var clientClazz: Class<*>
    private lateinit var getWLoginSigInfoMethod: Method
    // private lateinit var wLoginSigInfoClazz: Class<*>

    /**
     * @see net.mamoe.mirai.internal.network.WLoginSigInfo
     */
    private lateinit var wLoginSigInfoClazz: Class<*>
    private lateinit var wLoginSigInfoProperties: Collection<KProperty1<*, *>>
    // ByteArray
    // KeyWithCreationTime
    // KeyWithExpiry
    // PSKeyMap

    // getter from wLoginSigInfo
    private lateinit var getSKeyMethod: Method // SKey -> data -> ByteArray
    private lateinit var getAccessTokenMethod: Method // AccessToken -> data -> ByteArray
    private lateinit var getSuperKeyMethod: Method // ByteArray
    private lateinit var getPsKeyMapMethod: Method // PsKeyMap
    private lateinit var getPt4TokenMapMethod: Method // Pt4TokenMap
    private lateinit var getPayTokenMethod: Method // ByteArray

    /**
     * @see net.mamoe.mirai.internal.network.WLoginSimpleInfo
     */
    // private lateinit var wLoginSimpleInfoClass: Class<*>
    // private lateinit var getWLoginSimpleInfoMethod: Method // WLoginSimpleInfo

    private lateinit var keyWithCreationTimeClazz: Class<*>
    private lateinit var getKeyWithCreationTimeDataMethod: Method
    private lateinit var getKeyWithCreationTimeCreationTimeMethod: Method

    private lateinit var keyWithExpiryClazz: Class<*>
    private lateinit var getKeyWithExpiryDataMethod: Method
    private lateinit var getKeyWithExpiryCreationTimeMethod: Method

    private var cause: Throwable? = null

    @JvmStatic
    fun main(args: Array<String>) {
        for (memberProperty in wLoginSigInfoClazz.kotlin.memberProperties) {
            println("${memberProperty.name} -> $memberProperty")
        }
    }

    init {
        try {
            // bot client field
            botClientGetter = Class.forName("$MIRAI_PACKAGE.QQAndroidBot")
                .getDeclaredMethod("getClient").also { it.isAccessible = true }

            // client clazz
            val clientClazz = Class.forName("$MIRAI_PACKAGE.network.QQAndroidClient")

            // get wLoginSigInfo method
            getWLoginSigInfoMethod = clientClazz.getDeclaredMethod("getWLoginSigInfo").also { it.isAccessible = true }

            // WLoginSigInfo class
            wLoginSigInfoClazz = Class.forName("$MIRAI_PACKAGE.network.WLoginSigInfo")
            wLoginSigInfoProperties = wLoginSigInfoClazz.kotlin.memberProperties


            // info getter
            getSKeyMethod = wLoginSigInfoClazz.getDeclaredMethod("getSKey").also { it.isAccessible = true }
            getAccessTokenMethod =
                wLoginSigInfoClazz.getDeclaredMethod("getAccessToken").also { it.isAccessible = true }
            getSuperKeyMethod = wLoginSigInfoClazz.getDeclaredMethod("getSuperKey").also { it.isAccessible = true }
            getPsKeyMapMethod = wLoginSigInfoClazz.getDeclaredMethod("getPsKeyMap").also { it.isAccessible = true }
            getPt4TokenMapMethod =
                wLoginSigInfoClazz.getDeclaredMethod("getPt4TokenMap").also { it.isAccessible = true }
            getPayTokenMethod = wLoginSigInfoClazz.getDeclaredMethod("getPayToken").also { it.isAccessible = true }
            // getWLoginSimpleInfoMethod = wLoginSigInfoClazz.getDeclaredMethod("getSimpleInfo").also { it.isAccessible = true }

            // data getter
            keyWithCreationTimeClazz = Class.forName("$MIRAI_PACKAGE.network.KeyWithCreationTime")
            getKeyWithCreationTimeDataMethod =
                keyWithCreationTimeClazz.getDeclaredMethod("getData").also { it.isAccessible = true }
            getKeyWithCreationTimeCreationTimeMethod =
                keyWithCreationTimeClazz.getDeclaredMethod("getCreationTime").also { it.isAccessible = true }

            keyWithExpiryClazz = Class.forName("$MIRAI_PACKAGE.network.KeyWithExpiry")
            getKeyWithExpiryDataMethod = keyWithExpiryClazz.getDeclaredMethod("getData").also { it.isAccessible = true }
            getKeyWithExpiryCreationTimeMethod =
                keyWithExpiryClazz.getDeclaredMethod("getCreationTime").also { it.isAccessible = true }

            success = true
        } catch (e: Throwable) {
            cause = e
        }
    }


    // private fun injectData(prop: KProperty1<*, *>, instance: Any, map: MutableMap<String, String>) {
    //     val jType = prop.returnType.javaType
    //     when {
    //         // is PsKeyMap
    //         prop.name == "psKeyMap" -> {
    //             val psKeyMap = prop.call(instance) as Map<*, *>
    //             psKeyMap.forEach { (k, v) ->
    //                 v?.let { value ->
    //                     (getKeyWithExpiryDataMethod(value) as ByteArray).encodeToBasicString().takeIf { it.isNotBlank() }
    //                         ?.let { bv ->
    //                             map["psKey:$k"] = bv
    //                         }
    //
    //                 }
    //             }
    //         }
    //         // is pt4TokenMap
    //         prop.name == "pt4TokenMap" -> {
    //             val pt4TokenMap = prop.call(instance) as Map<*, *>
    //             pt4TokenMap.forEach { (k, v) ->
    //                 v?.let { value ->
    //                     (getKeyWithExpiryDataMethod(value) as ByteArray).encodeToBasicString().takeIf { it.isNotBlank() }
    //                         ?.let { bv ->
    //                             map["pt4Token:$k"] = bv
    //                         }
    //                 }
    //             }
    //         }
    //         // is ByteArray
    //         jType == ByteArray::class.java -> (prop.call(instance) as ByteArray).encodeToBasicString()
    //             .takeIf { it.isNotBlank() }?.let { v ->
    //                 map[prop.name] = v
    //             }
    //         // is long
    //         jType == Long::class.javaPrimitiveType -> map[prop.name] = (prop.call(instance) as Long).toString()
    //         // is Long
    //         jType == Long::class.java -> map[prop.name] = (prop.call(instance) as Long).toString()
    //         // is KeyWithCreationTime
    //         jType == keyWithCreationTimeClazz -> {
    //             // get keyWithCreationTime first
    //             val keyWithCreationTime = prop.call(instance)
    //             (getKeyWithCreationTimeDataMethod(keyWithCreationTime) as ByteArray).encodeToBasicString()
    //                 .takeIf { it.isNotBlank() }?.let { v ->
    //                     map[prop.name] = v
    //                 }
    //         }
    //         // is KeyWithExpiry
    //         jType == keyWithExpiryClazz -> {
    //             val keyWithExpiry = prop.call(instance)
    //             (getKeyWithExpiryDataMethod(keyWithExpiry) as ByteArray).encodeToBasicString().takeIf { it.isNotBlank() }
    //                 ?.let { v ->
    //                     map[prop.name] = v
    //                 }
    //         }
    //     }
    // }

    private fun injectData(prop: KProperty1<*, *>, instance: Any): StructuralThingWithName<String>? {
        val builder = StructuralThingWithNameBuilder<String>()
        val jType = prop.returnType.javaType
        when {
            // is PsKeyMap
            prop.name == "psKeyMap" -> {
                builder.name = "psKey"
                builder.value("")
                val psKeyMap = prop.call(instance) as Map<*, *>
                psKeyMap.forEach { (k, v) ->
                    v?.let { value ->

                        builder.child(name = k.toString(), "") {

                            (getKeyWithExpiryDataMethod(value) as ByteArray).encodeToGeneralString().takeIf { it.isNotBlank() }
                                ?.let { bv -> child(name = "data", bv) }
                            // builder.child(name = k.toString(), bv)
                            // map["psKey:$k"] = bv
                            child(name = "creationTime", (getKeyWithExpiryCreationTimeMethod(value) as Long).toString())
                        }
                    }
                }
            }
            // is pt4TokenMap
            prop.name == "pt4TokenMap" -> {
                builder.name = "pt4Token"
                builder.value("")
                val pt4TokenMap = prop.call(instance) as Map<*, *>
                pt4TokenMap.forEach { (k, v) ->
                    v?.let { value ->

                        builder.child(name = k.toString(), "") {

                            (getKeyWithExpiryDataMethod(value) as ByteArray).encodeToGeneralString().takeIf { it.isNotBlank() }
                                ?.let { bv -> child(name = "data", bv) }
                            child(name = "creationTime", (getKeyWithExpiryCreationTimeMethod(value) as Long).toString())
                        }
                    }
                }
            }
            // is ByteArray
            jType == ByteArray::class.java -> {
                builder.name = prop.name
                (prop.call(instance) as ByteArray).encodeToGeneralString()
                    .takeIf { it.isNotBlank() }?.let { v ->
                        builder.value(v)
                    } ?: builder.value("")
            }

            // is long
            jType == Long::class.javaPrimitiveType -> {
                // map[prop.name] = (prop.call(instance) as Long).toString()
                builder.name = prop.name
                builder.value((prop.call(instance) as Long).toString())
            }

            // is Long
            jType == Long::class.java -> {
                // map[prop.name] = (prop.call(instance) as Long).toString()
                builder.name = prop.name
                builder.value((prop.call(instance) as Long).toString())
            }
            // is KeyWithCreationTime
            jType == keyWithCreationTimeClazz -> {
                builder.name = prop.name
                builder.value("")
                // get keyWithCreationTime first
                val keyWithCreationTime = prop.call(instance)

                (getKeyWithCreationTimeDataMethod(keyWithCreationTime) as ByteArray).encodeToGeneralString()
                    .takeIf { it.isNotBlank() }
                    ?.let { v -> builder.child(name = "data", value = v) }

                builder.child("creationTime",
                    (getKeyWithCreationTimeCreationTimeMethod(keyWithCreationTime) as Long).toString())
            }
            // is KeyWithExpiry
            jType == keyWithExpiryClazz -> {
                builder.name = prop.name
                builder.value("")
                val keyWithExpiry = prop.call(instance)

                (getKeyWithExpiryDataMethod(keyWithExpiry) as ByteArray).encodeToGeneralString()
                    .takeIf { it.isNotBlank() }
                    ?.let { v -> builder.child(name = "data", value = v) }

                builder.child("creationTime",
                    (getKeyWithExpiryCreationTimeMethod(keyWithExpiry) as Long).toString())
            }
        }
        if (builder.name == null) {
            return null
        }
        return builder.build()
    }


    /**
     * 得到 cookies
     * @throws IllegalStateException 如果[UnsafeViolenceAndroidBotCookieUtils]不可用，则会抛出此异常。
     * 如果[UnsafeViolenceAndroidBotCookieUtils.cause]不为null，则会同时输出其信息。
     * @throws Exception 可能会出现任何不可预测的异常。
     */
    @Throws(Exception::class)
    fun cookies(bot: Bot): Cookies {
        if (!success) {
            cause?.run { throw IllegalStateException("Can not use: " + this.localizedMessage, this) }
                ?: throw IllegalStateException("Can not use.")
        }

        // get bot client
        val client = botClientGetter(bot)
        // get wLoginSigInfo
        val wLoginSigInfo = getWLoginSigInfoMethod(client)

        // val map = mutableMapOf<String, String>()

        val builder = StructuralThingWithNameBuilder("", "")

        val uin = "o${bot.id}"
        // map["uin"] = uin
        // map["p_uin"] = uin
        builder.child("uin", uin)
        builder.child("p_uin", uin)

        // val skey: ByteArray = getKeyWithCreationTimeDataMethod(getSKeyMethod(wLoginSigInfo)) as ByteArray
        // map["skey"] = skey.encodeToBasicString()
        // val psKeyMap = getPsKeyMapMethod(wLoginSigInfo) as Map<*, *>
        // val psKey = psKeyMap["qun.qq.com"]?.let { getKeyWithCreationTimeDataMethod(it) } as ByteArray?

        wLoginSigInfoProperties.forEach { p ->
            injectData(p, wLoginSigInfo)?.let(builder::child)
        }

        val things = builder.build()


        val sKey = things.findValue { it.startsWith("sKey") } ?: ""
        val psKey = things.findValue { it.startsWith("psKey") } ?: ""

        return Cookies(
            things,
            uin,
            uin,
            sKey,
            psKey

        )

//        val accessToken = getDataMethod(getAccessTokenMethod(wLoginSigInfo)) as ByteArray
//        val superKey = getSuperKeyMethod(wLoginSigInfo) as ByteArray

        // psKeyMap
        /*
        maybe
        tenpay.com
        openmobile.qq.com
        docs.qq.com
        connect.qq.com
        qzone.qq.com
        vip.qq.com
        qun.qq.com
        game.qq.com
        qqweb.qq.com
        office.qq.com
        ti.qq.com
        mail.qq.com
        qzone.com
        mma.qq.com
         */


        // cookies info
        // return Cookies(
        //     uin,
        //     skey.encodeToBasicString(),
        //     pUin,
        //     psKey?.encodeToBasicString() ?: ""
        // )
    }
}


/**
 * 通过bot得到[Cookies]信息。
 */
@OptIn(SimbotExperimentalApi::class)
val Bot.cookies: Cookies?
    get() = try {
        UnsafeViolenceAndroidBotCookieUtils.cookies(this)
    } catch (e: Throwable) {
        logger.error("Cannot get bot cookies.", e)
        null
    }


/*
"uin=o${id};" +
" skey=${client.wLoginSigInfo.sKey.data.encodeToBasicString()};" +
" p_uin=o${id};" +
" p_skey=${client.wLoginSigInfo.psKeyMap["qun.qq.com"]?.data?.encodeToBasicString()}; "
 */

/**
 * bot的部分cookie信息
 */
data class Cookies(
    val cookiesRoot: StructuralThingWithName<String>,
    val uin: String,
    val pUin: String,
    val skey: String,
    val psKey: String, // p_skey
) {

    val cookies: List<StructuralThingWithName<String>> get() = cookiesRoot.children

    fun toCookiesMap(): Map<String, String> = cookiesRoot.resolveToMap(":", false) { it.isNotBlank() }

    /** bkn */
    val bkn: Int get() = toBkn(skey)

    /** 计算g_tk */
    val gTk: Long get() = toGtk(psKey)

    /** cookie string */
    override fun toString(): String {
        return "Cookies(cookies=$cookies)"
    }
}


/**
 * to bkn by skey
 */
internal fun toBkn(skey: String): Int {
    var hash = 5381
    for (element in skey) {
        hash += (hash shl 5) + element.code
    }
    return hash and 2147483647
}

/**
 * to g_tk by pskey
 */
internal fun toGtk(pskey: String): Long {
    var hash: Long = 5381
    for (element in pskey) {
        hash += (hash shl 5) + element.code
    }
    return hash and 0x7fffffff
}

@Suppress("NOTHING_TO_INLINE")
internal inline fun ByteArray.encodeToString(charset: Charset = Charsets.UTF_8): String = String(this, charset)
internal fun ByteArray.encodeToGeneralString(): String = joinToString(",")

