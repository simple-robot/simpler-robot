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
@file:JvmName("MiraiConfigurations")

package love.forte.simbot.component.mirai.configuration

import cn.hutool.crypto.SecureUtil
import love.forte.common.configuration.annotation.ConfigInject
import love.forte.common.ioc.annotation.Beans
import love.forte.simbot.component.mirai.SimbotMiraiLogger
import net.mamoe.mirai.utils.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import kotlin.random.Random
import kotlin.random.nextInt

/**
 * Mirai配置类
 *
 *
 */
@Beans
@AsMiraiConfig
public class MiraiConfiguration {

    /**
     * mirai心跳周期. 过长会导致被服务器断开连接. 单位毫秒
     * @see BotConfiguration.heartbeatPeriodMillis
     */
    @field:ConfigInject
    var heartbeatPeriodMillis: Long = BotConfiguration.Default.heartbeatPeriodMillis

    /**
     * 每次心跳时等待结果的时间.
     * 一旦心跳超时, 整个网络服务将会重启 (将消耗约 1s). 除正在进行的任务 (如图片上传) 会被中断外, 事件和插件均不受影响.
     * @see BotConfiguration.heartbeatTimeoutMillis
     */
    @field:ConfigInject
    var heartbeatTimeoutMillis: Long = BotConfiguration.Default.heartbeatTimeoutMillis

    /** 心跳失败后的第一次重连前的等待时间. */
    @field:ConfigInject
    var firstReconnectDelayMillis: Long = BotConfiguration.Default.firstReconnectDelayMillis

    /** 重连失败后, 继续尝试的每次等待时间 */
    @field:ConfigInject
    var reconnectPeriodMillis: Long = BotConfiguration.Default.reconnectPeriodMillis

    /** 最多尝试多少次重连 */
    @field:ConfigInject
    var reconnectionRetryTimes: Int = BotConfiguration.Default.reconnectionRetryTimes


    /** 使用协议类型 */
    @field:ConfigInject
    var protocol: BotConfiguration.MiraiProtocol = BotConfiguration.Default.protocol

    /** 关闭mirai的bot logger */
    @field:ConfigInject
    var noBotLog: Boolean = false

    /** 关闭mirai网络日志 */
    @field:ConfigInject
    var noNetworkLog: Boolean = false

    /** mirai bot log切换使用simbot的log */
    @field:ConfigInject
    var useSimbotBotLog: Boolean = true

    /** mirai 网络log 切换使用simbot的log */
    @field:ConfigInject
    var useSimbotNetworkLog: Boolean = true

    /** mirai配置自定义deviceInfoSeed的时候使用的随机种子。默认为1.  */
    @field:ConfigInject
    var deviceInfoSeed: Long = 1L


    // @field:ConfigInject("mirai.autoRelogin")
    // var autoRelogin: Boolean = false

    @field:ConfigInject
    var cacheType: MiraiCacheType = MiraiCacheType.FILE

    @field:ConfigInject
    var cacheDirectory: String? = null


    /**
     * mirai官方配置类获取函数，默认为其默认值
     * */
    // @set:Deprecated("use setPostBotConfigurationProcessor((code, conf) -> {...})")
    val botConfiguration: (String) -> BotConfiguration = { code ->
        val conf = BotConfiguration()
        conf.deviceInfo = { MiraiSystemDeviceInfo(code, deviceInfoSeed) }
        conf.heartbeatPeriodMillis = this.heartbeatPeriodMillis
        conf.heartbeatTimeoutMillis = this.heartbeatTimeoutMillis
        conf.firstReconnectDelayMillis = this.firstReconnectDelayMillis
        conf.reconnectPeriodMillis = this.reconnectPeriodMillis
        conf.reconnectionRetryTimes = this.reconnectionRetryTimes
        conf.protocol = this.protocol
        conf.fileCacheStrategy = when (this.cacheType) {
            // 内存缓存
            MiraiCacheType.MEMORY -> FileCacheStrategy.MemoryCache
            // 文件缓存
            MiraiCacheType.FILE -> {
                val cacheDir = this.cacheDirectory
                if (cacheDir?.isNotBlank() == true) {
                    val directory = File(cacheDir)
                    if (!directory.exists()) {
                        // 不存在，创建
                        directory.mkdirs()
                    }
                    if (!directory.isDirectory) {
                        throw IllegalArgumentException("'$cacheDir' is not a directory.")
                    }
                    FileCacheStrategy.TempCache(directory)
                } else {
                    FileCacheStrategy.MemoryCache
                }
            }
        }


        if (noBotLog) {
            conf.noBotLog()
        }
        if (noNetworkLog) {
            conf.noNetworkLog()
        }
        // MiraiLoggerWithSwitch
        // 默认情况下都是关闭状态的log
        if (useSimbotBotLog) {
            conf.botLoggerSupplier = {
                val logger: Logger = LoggerFactory.getLogger("love.forte.component.mirai.bot.${it.id}")
                SimbotMiraiLogger(logger).withSwitch(true)
            }
        } else {
            val oldBotLoggerSup = conf.botLoggerSupplier
            conf.botLoggerSupplier = {
                val logger = oldBotLoggerSup(it)
                if (logger is MiraiLoggerWithSwitch) logger else logger.withSwitch(true)
            }
        }
        if (useSimbotNetworkLog) {
            conf.networkLoggerSupplier = {
                val logger: Logger = LoggerFactory.getLogger("love.forte.component.mirai.net.${it.id}")
                SimbotMiraiLogger(logger).withSwitch(true)
            }
        } else {
            val oldNetworkLoggerSup = conf.networkLoggerSupplier
            conf.networkLoggerSupplier = {
                val logger = oldNetworkLoggerSup(it)
                if (logger is MiraiLoggerWithSwitch) logger else logger.withSwitch(true)
            }
        }

        conf
    }
}

/**
 * mirai的图片文件缓存策略
 */
enum class MiraiCacheType {
    /** 文件缓存 */
    FILE,

    /** 内存缓存 */
    MEMORY
}

/**
 * [SystemDeviceInfo] 实例，尝试着固定下随机值
 * @param code bot的账号
 */
open class MiraiSystemDeviceInfo
@JvmOverloads
constructor(
    code: Long,
    seed: Long,
    randomFactory: (code: Long, seed: Long) -> Random = { c, s -> Random(c * s) }
) : SystemDeviceInfo() {
    constructor(codeId: String, seedNum: Long) : this(codeId.toLong(), seedNum)
    constructor(codeId: String, seedNum: Long, randomFactory: (code: Long, seed: Long) -> Random) :
            this(codeId.toLong(), seedNum, randomFactory)


    private val random: Random = randomFactory(code, seed)


    override val display: ByteArray = "MIRAI-SIMBOT.200122.001".toByteArray()
    override val product: ByteArray = "mirai-simbot".toByteArray()
    override val device: ByteArray = "mirai-simbot".toByteArray()
    override val board: ByteArray = "mirai-simbot".toByteArray()
    override val model: ByteArray = "mirai-simbot".toByteArray()

    override val fingerprint: ByteArray =
        "mamoe/mirai/mirai:10/MIRAI.200122.001/${getRandomString(7, '0'..'9', random)}:user/release-keys".toByteArray()
    override val bootId: ByteArray = generateUUID(SecureUtil.md5().digest(getRandomByteArray(16, random))).toByteArray()
    override val procVersion: ByteArray =
        "Linux version 3.0.31-${getRandomString(8, random)} (android-build@xxx.xxx.xxx.xxx.com)".toByteArray()

    override val imsiMd5: ByteArray = SecureUtil.md5().digest(getRandomByteArray(16, random))
    override val imei: String = getRandomString(15, '0'..'9', random)
}

/*
 * 以下源代码修改自
 * net.mamoe.mirai.utils.SystemDeviceInfo.kt、
 * net.mamoe.mirai.utils.ExternalImage.kt
 *
 * 原源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 * https://github.com/mamoe/mirai/blob/master/LICENSE
 */

/**
 * 生成长度为 [length], 元素为随机 `0..255` 的 [ByteArray]
 */
internal fun getRandomByteArray(length: Int, r: Random): ByteArray = ByteArray(length) { r.nextInt(0..255).toByte() }

/**
 * 随机生成长度为 [length] 的 [String].
 */
internal fun getRandomString(length: Int, r: Random): String =
    getRandomString(length, r, *defaultRanges)

private val defaultRanges: Array<CharRange> = arrayOf('a'..'z', 'A'..'Z', '0'..'9')

/**
 * 根据所给 [charRange] 随机生成长度为 [length] 的 [String].
 */
internal fun getRandomString(length: Int, charRange: CharRange, r: Random): String =
    String(CharArray(length) { charRange.random(r) })

/**
 * 根据所给 [charRanges] 随机生成长度为 [length] 的 [String].
 */
internal fun getRandomString(length: Int, r: Random, vararg charRanges: CharRange): String =
    String(CharArray(length) { charRanges[r.nextInt(0..charRanges.lastIndex)].random(r) })

private fun generateUUID(md5: ByteArray): String {
    return "${md5[0, 3]}-${md5[4, 5]}-${md5[6, 7]}-${md5[8, 9]}-${md5[10, 15]}"
}

@JvmSynthetic
internal operator fun ByteArray.get(rangeStart: Int, rangeEnd: Int): String = buildString {
    for (it in rangeStart..rangeEnd) {
        append(this@get[it].fixToString())
    }
}

private fun Byte.fixToString(): String {
    return when (val b = this.toInt() and 0xff) {
        in 0..15 -> "0${this.toString(16).toUpperCase()}"
        else -> b.toString(16).toUpperCase()
    }
}

