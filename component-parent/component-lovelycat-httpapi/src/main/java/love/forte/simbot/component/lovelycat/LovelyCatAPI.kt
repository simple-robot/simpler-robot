/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  simple-robot-S
 *  * File     LovelyCat.kt
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

package love.forte.simbot.component.lovelycat

import love.forte.simbot.http.template.HttpTemplate
import love.forte.simbot.serialization.json.fastjson.FastJsonSerializerFactory


/**
 * loveCat api模板.
 * @property httpTemplate HttpTemplate
 * @constructor
 */
public class LovelyCatAPI(private val httpTemplate: HttpTemplate) {

}


fun main() {

    val serializer = FastJsonSerializerFactory().getJsonSerializer(A::class.java)

    val a = A("张三", 20, null)
    val jsonStr = serializer.toJson(a)
    println(jsonStr)
    val jsonStr2 = "{\"Age\":20,\"Name\":\"张三\"}";
    val fromJson = serializer.fromJson(jsonStr2)
    println(fromJson)
}

data class A(val name: String, val age: Int, val he: String?)





