/*
 *  Copyright (c) 2021-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  根据 GNU LESSER GENERAL PUBLIC LICENSE 3 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable


@Serializable
data class Hi(val name: String, val age: Int)


@OptIn(ExperimentalSerializationApi::class)
fun main() {
    // val info = mapOf("name" to "forte", "age" to "123").asBotVerifyInfo("abc")
    //
    // val p = Properties(EmptySerializersModule)
    //
    // val hi = p.decodeFromStringMap(Hi.serializer(), info)
    //
    // println(hi)
}