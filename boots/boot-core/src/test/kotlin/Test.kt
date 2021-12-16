/*
 *  Copyright (c) 2021 ForteScarlet <https://github.com/ForteScarlet>
 *
 *  根据 Apache License 2.0 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.properties.Properties

@OptIn(ExperimentalSerializationApi::class)
fun main() {

    val json = Json {
        isLenient = true
    }

    val jsonResource = Thread.currentThread().contextClassLoader.getResourceAsStream("config-test.json")
    val jsonStr = jsonResource!!.reader().use { it.readText() }

    val jsonElement = json.parseToJsonElement(jsonStr)

    println(jsonElement)


    val properties = Properties(EmptySerializersModule)


    // val manager = coreBeanManager {
    //     this.plusProcessor { bean, manager ->
    //         bean.postValue { _, any ->
    //
    //             any
    //         }
    //     }
    // }


}


annotation class Config(val prefix: String = "")


@Serializable
@Config("simbot.core")
public class A {
    lateinit var name: String
    var size: Int = -1
}