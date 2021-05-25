/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  component-onebot
 *  * File     ApiManager.kt
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
@file:JvmName("KaiheilaApis")
@file:Suppress("unused")

package love.forte.simbot.component.kaiheila.api


/**
 * 一个[开黑啦的API][https://developer.kaiheila.cn/doc/reference]信息。
 *
 * 其主要记录一个API的版本信息。
 *
 * [version] 属性将会作为 [KaiheilaApi] 的唯一标识。
 *
 *
 * @see BaseKaiheilaApi
 * @see KaiheilaApiData
 *
 */
public interface KaiheilaApi {
    /**
     * api的版本编号。以 `v3` 为例，版本号则为 `3`。
     */
    val versionNumber: Int

    /**
     * 版本号。以 `v3` 为例，得到的就是 `v3`。
     */
    @JvmDefault
    val version: String
        get() = "v$versionNumber"

    /**
     * 得到当前版本的API请求路径。
     * 默认情况下即为 `${BASE_URL}/${version}` 的形式。
     */
    @JvmDefault
    val apiUrl: String
        get() = "$BASE_URL/$version"


    companion object Base {
        /**
         * 开黑啦API的[基础url][https://developer.kaiheila.cn/doc/reference]。
         */
        const val BASE_URL = "https://www.kaiheila.cn/api"
    }
}


/**
 * [KaiheilaApi] 基础抽象类。
 */
public abstract class BaseKaiheilaApi(override val versionNumber: Int) : KaiheilaApi {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BaseKaiheilaApi

        if (version != other.version) return false

        return true
    }

    override fun hashCode(): Int = version.hashCode()

    override fun toString(): String = "API(version=$version)"
}


/**
 * [KaiheilaApi]基础实现。
 */
public class KaiheilaApiData(versionNumber: Int) : BaseKaiheilaApi(versionNumber)


/**
 * get instance for [KaiheilaApi] by [KaiheilaApi.versionNumber] value.
 */
public inline fun kaiheilaApi(versionNumber: () -> Int) = KaiheilaApiData(versionNumber())