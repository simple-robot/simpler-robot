/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
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

import net.mamoe.mirai.Bot
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.reflect.Method
import java.nio.charset.Charset

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
object UnsafeViolenceAndroidBotCookieUtils {
    private const val MIRAI_PACKAGE = "net.mamoe.mirai.internal"
    private var success: Boolean = false
    private lateinit var botClientGetter: Method
    // private lateinit var clientClazz: Class<*>
    private lateinit var getWLoginSigInfoMethod: Method
    // private lateinit var wLoginSigInfoClazz: Class<*>

    // getter
    private lateinit var getSKeyMethod: Method // SKey -> data -> ByteArray
    private lateinit var getAccessTokenMethod: Method // AccessToken -> data -> ByteArray
    private lateinit var getSuperKeyMethod: Method // ByteArray
    private lateinit var getPsKeyMapMethod: Method // PsKeyMap
    private lateinit var getPt4TokenMapMethod: Method // Pt4TokenMap
    private lateinit var getPayTokenMethod: Method // ByteArray

    /**
     * @see net.mamoe.mirai.internal.network.WLoginSimpleInfo
     */
    private lateinit var getWLoginSimpleInfoMethod: Method // WLoginSimpleInfo

    // private lateinit var keyWithCreationTimeClazz: Class<*>
    private lateinit var getDataMethod: Method
    private var cause: Throwable? = null



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
            val wLoginSigInfoClazz = Class.forName("$MIRAI_PACKAGE.network.WLoginSigInfo")

            // info getter
            getSKeyMethod = wLoginSigInfoClazz.getDeclaredMethod("getSKey").also { it.isAccessible = true }
            getAccessTokenMethod =
                wLoginSigInfoClazz.getDeclaredMethod("getAccessToken").also { it.isAccessible = true }
            getSuperKeyMethod = wLoginSigInfoClazz.getDeclaredMethod("getSuperKey").also { it.isAccessible = true }
            getPsKeyMapMethod = wLoginSigInfoClazz.getDeclaredMethod("getPsKeyMap").also { it.isAccessible = true }
            getPt4TokenMapMethod =
                wLoginSigInfoClazz.getDeclaredMethod("getPt4TokenMap").also { it.isAccessible = true }
            getPayTokenMethod = wLoginSigInfoClazz.getDeclaredMethod("getPayToken").also { it.isAccessible = true }
            getWLoginSimpleInfoMethod = wLoginSigInfoClazz.getDeclaredMethod("getSimpleInfo").also { it.isAccessible = true }

            // data getter
            val keyWithCreationTimeClazz = Class.forName("$MIRAI_PACKAGE.network.KeyWithCreationTime")
            getDataMethod = keyWithCreationTimeClazz.getDeclaredMethod("getData").also { it.isAccessible = true }

            success = true
        } catch (e: Throwable) {
            cause = e
        }
    }

    /**
     * 得到cookies
     * @throws IllegalStateException 如果[UnsafeViolenceAndroidBotCookieUtils]不可用，则会抛出此异常。
     * 如果[UnsafeViolenceAndroidBotCookieUtils.cause]不为null，则会同时输出其信息。
     * @throws Exception 可能会出现任何不可预测的异常。
     */
    @Throws(Exception::class)
    fun cookies(bot: Bot): Cookies {
        if (!success) {
            cause?.run { throw IllegalStateException("Can not use.", this) }
                ?: throw IllegalStateException("Can not use.")
        }
        // get bot client
        val client = botClientGetter(bot)
        // get wLoginSigInfo
        val wLoginSigInfo = getWLoginSigInfoMethod(client)

        val uin = "o${bot.id}"
        val pUin: String = uin
        val skey: ByteArray = getDataMethod(getSKeyMethod(wLoginSigInfo)) as ByteArray
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
        val psKeyMap = getPsKeyMapMethod(wLoginSigInfo) as Map<*, *>
        val psKey = psKeyMap["qun.qq.com"]?.let { getDataMethod(it) } as ByteArray?


        // cookies info
        return Cookies(
            uin,
            skey.encodeToString(),
            pUin,
            psKey?.encodeToString() ?: ""
        )
    }
}


/**
 * 通过bot得到[Cookies]信息。
 */
val Bot.cookies: Cookies? get() = try {
    UnsafeViolenceAndroidBotCookieUtils.cookies(this)
} catch (e: Throwable) {
    logger.error("Cannot get bot cookies.", e)
    null
}


/*
"uin=o${id};" +
" skey=${client.wLoginSigInfo.sKey.data.encodeToString()};" +
" p_uin=o${id};" +
" p_skey=${client.wLoginSigInfo.psKeyMap["qun.qq.com"]?.data?.encodeToString()}; "
 */

/**
 * bot的部分cookie信息
 */
data class Cookies(
    val uin: String,
    val skey: String,
    val pUin: String,
    val psKey: String // p_skey
) {

    @Deprecated("Use 'pUin'.", ReplaceWith("pUin"))
    val p_uin: String get() = pUin

    /** bkn */
    val bkn: Int get() = toBkn(skey)

    /** 计算g_tk */
    val gTk: Long get() = toGtk(psKey)

    /** cookie maps */
    val cookiesMap: MutableMap<String, String>
        get() {
            return mutableMapOf(
                "uin" to uin,
                "p_uin" to pUin,
                "skey" to skey,
                "p_skey" to psKey
            )
        }

    /** cookie string */
    override fun toString(): String {
        return "uin=$uin; skey=$skey; p_skey=$psKey"
    }
}


/**
 * to bkn by skey
 */
internal fun toBkn(skey: String): Int {
    var hash = 5381
    for (element in skey) {
        hash += (hash shl 5/* << 5*/) + element.toInt()
    }
    return hash and 2147483647 /*& 2147483647*/
}

//　window.g_qzonetoken = (function(){ try{return
// "1cf5c9fa0001be9c6d7fb32819d6cc533f4a037101040b5740621bb048ecba0555e7aa2722f02a9778";}　catch\(e\)


/**
 * to g_tk by pskey
 */
internal fun toGtk(pskey: String): Long {
    val p_skey = pskey
    var hash: Long = 5381
    for (element in p_skey) {
        hash += (hash shl 5) + element.toInt()
        // hash += (hash shl 5 and 0x7fffffff) + element.toInt() and 0x7fffffff
        // hash = hash and 0x7fffffff
    }
    return hash and 0x7fffffff
}

@Suppress("NOTHING_TO_INLINE")
internal inline fun ByteArray.encodeToString(charset: Charset = Charsets.UTF_8): String = String(this, charset)

