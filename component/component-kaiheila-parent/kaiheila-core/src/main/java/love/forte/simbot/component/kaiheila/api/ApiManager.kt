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
@file:JvmName("KaiheilaApis")
@file:Suppress("unused")

package love.forte.simbot.component.kaiheila.api

import io.ktor.http.*


/**
 * 一个[开黑啦的API][https://developer.kaiheila.cn/doc/reference]版本信息。
 *
 * 其主要记录一个API的版本信息。
 *
 * [version] 属性将会作为 [Api] 的唯一标识。
 *
 *
 * @see BaseApi
 * @see ApiVersionData
 *
 */
public interface Api {
    /**
     * api的版本编号。以 `v3` 为例，版本号则为 `3`。
     */
    val versionNumber: Int

    /**
     * 版本号。以 `v3` 为例，得到的就是 `v3`。
     */
    val version: String
        get() = "v$versionNumber"

    /**
     * 得到当前版本的API请求路径。
     * 默认情况下即为 `${BASE_URL}/${version}` 的形式。
     */
    val apiUrl: String
        get() = "$BASE_URL/$version"


    companion object Base {

        const val HOST = "www.kaiheila.cn"

        /**
         * 开黑啦API的[基础url][https://developer.kaiheila.cn/doc/reference]。
         */
        const val BASE_URL = "https://www.kaiheila.cn/api"
    }
}
// https://www.kaiheila.cn/api

fun URLBuilder.toKhlBuild(api: Api, apiPath: List<String>) {
    protocol = URLProtocol.HTTPS
    host = Api.HOST
    if (apiPath.isEmpty()) {
        path("api", api.version)
    } else {
        val list = mutableListOf("api", api.version).also {
            it.addAll(apiPath)
        }
        path(list)
    }
}


/**
 * [Api] 基础抽象类。
 */
public abstract class BaseApi(override val versionNumber: Int) : Api {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BaseApi

        if (version != other.version) return false

        return true
    }

    override fun hashCode(): Int = version.hashCode()

    override fun toString(): String = "API(version=$version)"
}


/**
 * [Api]基础实现。
 */
public class ApiVersionData(versionNumber: Int) : BaseApi(versionNumber)


/**
 * get simple instance for [Api] by [Api.versionNumber] value.
 */
public fun apiVersion(versionNumber: Int): Api = ApiVersionData(versionNumber)


/**
 * get simple instance for [Api] by [Api.versionNumber] value.
 */
public inline fun apiVersion(versionNumber: () -> Int): Api = apiVersion(versionNumber())

