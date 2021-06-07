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
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import love.forte.common.configuration.annotation.ConfigInject
import love.forte.common.ioc.annotation.Beans
import love.forte.common.utils.ResourceUtil
import love.forte.simbot.component.mirai.SimbotMiraiLogger
import love.forte.simbot.core.TypedCompLogger
import net.mamoe.mirai.utils.BotConfiguration
import net.mamoe.mirai.utils.DeviceInfo
import net.mamoe.mirai.utils.MiraiLoggerWithSwitch
import net.mamoe.mirai.utils.withSwitch
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileWriter
import kotlin.random.Random
import kotlin.random.nextInt

private const val MIRAI_LOG_NAME_PREFIX = "love.forte.simbot.component.mirai"


public fun miraiBotLogger(botCode: Long, type: String? = null): Logger {
    return type?.let { t -> LoggerFactory.getLogger("$MIRAI_LOG_NAME_PREFIX.$t.$botCode") }
        ?: LoggerFactory.getLogger("$MIRAI_LOG_NAME_PREFIX.$botCode")
}

/**
 * Mirai配置类
 *
 *
 */
@Beans("miraiConfiguration")
@AsMiraiConfig
public class MiraiConfiguration {

    private companion object : TypedCompLogger(MiraiConfiguration::class.java)

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


    /**
     * 使用协议类型。
     * 默认使用 [安卓手机协议][BotConfiguration.MiraiProtocol.ANDROID_PHONE]。
     */
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


    @field:ConfigInject
    var deviceInfoFile: String? = ""

    /**
     *  是否输出设备信息
     */
    @field:ConfigInject
    var deviceInfoOutput: Boolean = false

    /**
     * @see BotConfiguration.highwayUploadCoroutineCount
     */
    @field:ConfigInject
    var highwayUploadCoroutineCount: Int = BotConfiguration.Default.highwayUploadCoroutineCount

    /**
     * mirai官方配置类获取函数，默认为其默认值
     * */
    // @set:Deprecated("use setPostBotConfigurationProcessor((code, conf) -> {...})")

    val botConfiguration: (String) -> BotConfiguration = {
        val conf = BotConfiguration()

        deviceInfoFile.takeIf { it?.isNotBlank() == true }?.runCatching {
            logger.info("Try to use device info file: $this")
            val jsonReader = ResourceUtil.getResourceUtf8Reader(this)
            val json = jsonReader.use { it.readText() }
            conf.loadDeviceInfoJson(json)
        }?.getOrElse { e ->
            logger.error("Load device Info json file: $deviceInfoFile failed. get device by simbot default.", e)
            null
        } ?: run {
            conf.deviceInfo = {
                val devInfo = simbotMiraiDeviceInfo(it.id, deviceInfoSeed)

                if (deviceInfoOutput) {
                    runCatching<Unit> {
                        val devInfoJson = Json {
                            isLenient = true
                            ignoreUnknownKeys = true
                            prettyPrint = true
                        }.encodeToString(devInfo)
                        val outFile = File("simbot-devInfo.json")
                        if (!outFile.exists()) {
                            outFile.apply {
                                parentFile?.mkdirs()
                                createNewFile()
                            }
                        }
                        FileWriter(outFile).use {
                                w -> w.write(devInfoJson)
                            logger.info("DevInfo write to ${outFile.canonicalPath}")
                        }
                    }.getOrElse { e ->
                        logger.error("Write devInfo failed: {}", e.localizedMessage)
                        if (!logger.isDebugEnabled) {
                            logger.error("Enable debug log for more information.")
                        }
                        logger.debug("Write devInfo failed.", e)
                    }
                }


                devInfo
            }
        }


        conf.heartbeatPeriodMillis = this.heartbeatPeriodMillis
        conf.heartbeatTimeoutMillis = this.heartbeatTimeoutMillis
        conf.firstReconnectDelayMillis = this.firstReconnectDelayMillis
        conf.reconnectPeriodMillis = this.reconnectPeriodMillis
        conf.reconnectionRetryTimes = this.reconnectionRetryTimes
        conf.protocol = this.protocol


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
                // val logger: Logger = LoggerFactory.getLogger("$MIRAI_LOG_NAME_PREFIX.bot.${it.id}")
                val logger: Logger = miraiBotLogger(it.id, "bot")
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
                val logger: Logger = miraiBotLogger(it.id, "net")
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



internal fun simbotMiraiDeviceInfo(c: Long, s: Long): DeviceInfo {
    val r = Random(c * s)
    return DeviceInfo(
        display = "MIRAI-SIMBOT.200122.001".toByteArray(),
        product = "mirai-simbot".toByteArray(),
        device = "mirai-simbot".toByteArray(),
        board = "mirai-simbot".toByteArray(),
        brand = "forte".toByteArray(),
        model = "mirai-simbot".toByteArray(),
        bootloader = "unknown".toByteArray(),
                    // mamoe/mirai/mirai:10/MIRAI.200122.001/
        fingerprint = "mamoe/mirai/mirai:10/MIRAI.200122.001/${getRandomString(7, '0'..'9', r)}:user/release-keys".toByteArray(),
        bootId = generateUUID(SecureUtil.md5().digest(getRandomByteArray(16, r))).toByteArray(),
        procVersion = "Linux version 3.0.31-${getRandomString(8, r)} (android-build@xxx.xxx.xxx.xxx.com)".toByteArray(),
        baseBand = byteArrayOf(),
        version = DeviceInfo.Version(),
        simInfo = "T-Mobile".toByteArray(),
        osType = "android".toByteArray(),
        macAddress = "02:00:00:00:00:00".toByteArray(),
        wifiBSSID = "02:00:00:00:00:00".toByteArray(),
        wifiSSID = "<unknown ssid>".toByteArray(),
        imsiMd5 = SecureUtil.md5().digest(getRandomByteArray(16, r)),
        imei = getRandomString(15, '0'..'9', r),
        apn = "wifi".toByteArray()

    )
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
@JvmSynthetic
internal fun getRandomByteArray(length: Int, r: Random): ByteArray = ByteArray(length) { r.nextInt(0..255).toByte() }

/**
 * 随机生成长度为 [length] 的 [String].
 */
@JvmSynthetic
internal fun getRandomString(length: Int, r: Random): String =
    getRandomString(length, r, *defaultRanges)

@JvmSynthetic
internal val defaultRanges: Array<CharRange> = arrayOf('a'..'z', 'A'..'Z', '0'..'9')

/**
 * 根据所给 [charRange] 随机生成长度为 [length] 的 [String].
 */
@JvmSynthetic
internal fun getRandomString(length: Int, charRange: CharRange, r: Random): String =
    String(CharArray(length) { charRange.random(r) })

/**
 * 根据所给 [charRanges] 随机生成长度为 [length] 的 [String].
 */
@JvmSynthetic
internal fun getRandomString(length: Int, r: Random, vararg charRanges: CharRange): String =
    String(CharArray(length) { charRanges[r.nextInt(0..charRanges.lastIndex)].random(r) })

@JvmSynthetic
internal fun generateUUID(md5: ByteArray): String {
    return "${md5[0, 3]}-${md5[4, 5]}-${md5[6, 7]}-${md5[8, 9]}-${md5[10, 15]}"
}

@JvmSynthetic
internal operator fun ByteArray.get(rangeStart: Int, rangeEnd: Int): String = buildString {
    for (it in rangeStart..rangeEnd) {
        append(this@get[it].fixToString())
    }
}

@JvmSynthetic
internal fun Byte.fixToString(): String {
    return when (val b = this.toInt() and 0xff) {
        in 0..15 -> "0${this.toString(16).uppercase()}"
        else -> b.toString(16).uppercase()
    }
}

