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

package love.forte.simbot.component.mirai

import love.forte.simbot.bot.BotRegisterInfo
import love.forte.simbot.component.mirai.configuration.MiraiConfiguration
import net.mamoe.mirai.utils.BotConfiguration

/**
 * 用于获取一个根据bot对应的 [BotConfiguration] 实例。
 */
public interface MiraiBotConfigurationFactory {
    fun getMiraiBotConfiguration(botInfo: BotRegisterInfo, simbotMiraiConfig: MiraiConfiguration): BotConfiguration
}


/**
 * 默认的 [MiraiBotConfigurationFactory] 实现。
 */
public object DefaultMiraiBotConfigurationFactory : MiraiBotConfigurationFactory {
    override fun getMiraiBotConfiguration(botInfo: BotRegisterInfo, simbotMiraiConfig: MiraiConfiguration): BotConfiguration {
        return simbotMiraiConfig.botConfiguration(botInfo.code)
    }
}




//
// /**
//  * [SystemDeviceInfo] 实例，尝试着固定下随机值
//  * @param code bot的账号
//  */
// open class MiraiSystemDeviceInfo
// @JvmOverloads
// constructor(
//     code: Long,
//     seed: Long,
//     randomFactory: (code: Long, seed: Long) -> Random = { c, s -> Random(c * s) }
// ): SystemDeviceInfo() {
//     constructor(codeId: String, seedNum: Long): this(codeId.toLong(), seedNum)
//     constructor(codeId: String, seedNum: Long, randomFactory: (code: Long, seed: Long) -> Random):
//             this(codeId.toLong(), seedNum, randomFactory)
//
//
//     private val random: Random = randomFactory(code, seed)
//
//
//     override val display: ByteArray = "MIRAI-SIMBOT.200122.001".toByteArray()
//     override val product: ByteArray = "mirai-simbot".toByteArray()
//     override val device: ByteArray = "mirai-simbot".toByteArray()
//     override val board: ByteArray = "mirai-simbot".toByteArray()
//     override val model: ByteArray = "mirai-simbot".toByteArray()
//
//     override val fingerprint: ByteArray =
//         "mamoe/mirai/mirai:10/MIRAI.200122.001/${getRandomString(7, '0'..'9', random)}:user/release-keys".toByteArray()
//     override val bootId: ByteArray = generateUUID(SecureUtil.md5().digest(getRandomByteArray(16, random))).toByteArray()
//     override val procVersion: ByteArray =
//         "Linux version 3.0.31-${getRandomString(8, random)} (android-build@xxx.xxx.xxx.xxx.com)".toByteArray()
//
//     override val imsiMd5: ByteArray = SecureUtil.md5().digest(getRandomByteArray(16, random))
//     override val imei: String = getRandomString(15, '0'..'9', random)
// }
//
// /*
//  * 以下源代码修改自
//  * net.mamoe.mirai.utils.SystemDeviceInfo.kt、
//  * net.mamoe.mirai.utils.ExternalImage.kt
//  *
//  * 原源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
//  * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
//  * https://github.com/mamoe/mirai/blob/master/LICENSE
//  */
//
// /**
//  * 生成长度为 [length], 元素为随机 `0..255` 的 [ByteArray]
//  */
// internal fun getRandomByteArray(length: Int, r: Random): ByteArray = ByteArray(length) { r.nextInt(0..255).toByte() }
//
// /**
//  * 随机生成长度为 [length] 的 [String].
//  */
// internal fun getRandomString(length: Int, r: Random): String =
//     getRandomString(length, r, *defaultRanges)
//
// private val defaultRanges: Array<CharRange> = arrayOf('a'..'z', 'A'..'Z', '0'..'9')
//
// /**
//  * 根据所给 [charRange] 随机生成长度为 [length] 的 [String].
//  */
// internal fun getRandomString(length: Int, charRange: CharRange, r: Random): String =
//     String(CharArray(length) { charRange.random(r) })
//
// /**
//  * 根据所给 [charRanges] 随机生成长度为 [length] 的 [String].
//  */
// internal fun getRandomString(length: Int, r: Random, vararg charRanges: CharRange): String =
//     String(CharArray(length) { charRanges[r.nextInt(0..charRanges.lastIndex)].random(r) })
//
// private fun generateUUID(md5: ByteArray): String {
//     return "${md5[0, 3]}-${md5[4, 5]}-${md5[6, 7]}-${md5[8, 9]}-${md5[10, 15]}"
// }
// @JvmSynthetic
// internal operator fun ByteArray.get(rangeStart: Int, rangeEnd: Int): String = buildString {
//     for (it in rangeStart..rangeEnd) {
//         append(this@get[it].fixToString())
//     }
// }
// private fun Byte.fixToString(): String {
//     return when (val b = this.toInt() and 0xff) {
//         in 0..15 -> "0${this.toString(16).toUpperCase()}"
//         else -> b.toString(16).toUpperCase()
//     }
// }
