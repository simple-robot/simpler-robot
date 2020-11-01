/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     Json.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.simbot.serialization.json


/**
 *
 * JSON 类型标准接口。
 * JSON 一般就只有两种类型，一个是array类型，一个是object类型。
 * 他们都可以相互嵌套。
 *
 * @author ForteScarlet -> https://github.com/ForteScarlet
 */
public interface Json {
}


/**
 * 数组类型的Json。
 * 可以获取到对应索引的值。
 */
public interface JsonArray {

}


/**
 * object类型的Json。
 * 可以获取到对应的指定参数名的值。
 */
public interface JsonObject {
    /**
     * 获取指定参数名的值。
     */
    fun get(key: String): Any?
}
