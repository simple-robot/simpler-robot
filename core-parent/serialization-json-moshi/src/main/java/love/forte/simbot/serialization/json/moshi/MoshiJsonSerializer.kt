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

@file:JvmName("MoshiJsonSerializers")
package love.forte.simbot.serialization.json.moshi

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import love.forte.simbot.serialization.json.JsonSerializer
import love.forte.simbot.serialization.json.JsonSerializerFactory
import java.lang.reflect.Type

/**
 *
 * moshi json解析器.
 *
 * @author ForteScarlet -> https://github.com/ForteScarlet
 */
public class MoshiJsonSerializer<T>(private val adapter: JsonAdapter<T>) : JsonSerializer<T> {
    /**
     * entity转化为json。
     */
    override fun toJson(entity: T): String = adapter.toJson(entity)

    /**
     * json转化为entity。
     */
    override fun fromJson(json: String): T = adapter.fromJson(json) ?: throw IllegalStateException("from json '$json' return null.")
}


/**
 * Moshi解析器。
 * Moshi内部自带缓存，因此不对adapter进行处理。
 */
public class MoshiJsonSerializerFactory(private val moshi: Moshi): JsonSerializerFactory {
    /** 得到对应类型的json解析器。*/
    override fun <T : Any?> getJsonSerializer(type: Type): JsonSerializer<T> = moshi.adapter<T>(type).toJsonSerializer()

    /** 得到对应类型的json解析器。 */
    override fun <T : Any?> getJsonSerializer(type: Class<T>): JsonSerializer<T> = when {
        List::class.java.isAssignableFrom(type) -> moshi.adapter(List::class.java).toJsonSerializer() as JsonSerializer<T>
        Map::class.java.isAssignableFrom(type) -> moshi.adapter(Map::class.java).toJsonSerializer() as JsonSerializer<T>
        else -> moshi.adapter(type).toJsonSerializer()
    }
}



private fun <T> JsonAdapter<T>.toJsonSerializer(): MoshiJsonSerializer<T> = MoshiJsonSerializer(this)
