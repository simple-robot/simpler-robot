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

@file:JvmName("ApiDataUtil")

package love.forte.simbot.component.kaiheila.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.builder.Builder
import love.forte.simbot.component.kaiheila.api.ApiData.Req
import love.forte.simbot.component.kaiheila.api.ApiData.Resp
import kotlin.reflect.KClass


/**
 * 此接口定义一个与Api相关的数据类。
 *
 * 定义两个类型一个来回，分别为 [Request][Req] 和 [Response][Resp].
 *
 * @author ForteScarlet
 */
public sealed interface ApiData {

    /**
     * [Request][Req]. 请求相关的数据类。
     * 一次请求，都会有一个对应的 [响应][RESP].
     */
    public interface Req<RESP : Resp> : ApiData {
        /** 获取响应的数据类型。 */
        val respType: KClass<out RESP>

        /**
         * 此请求对应的api路由路径。
         * 例如：`/guild/list`
         * */
        val route: String

        /**
         * 此次请求所发送的数据。为null则代表没有参数。
         */
        val parameters: Any?

        /**
         * 获取请求的鉴权token。
         *
         * - 机器人。TOKEN_TYPE = Bot。 `Authorization: Bot BHsTZ4232tLatgV5AFyjoqZGAHHmpl9mTxYQ/u4/80=`
         * - Oauth2。TOKEN_TYPE = Bearer。 `Authorization: Bearer BHsTZ4232tLatgV5AFyjoqZGAHHmpl9mTxYQ/u4/80=`
         */
        val authorization: String?
    }

    /**
     * [Response][Resp]. 请求响应相关的数据类。
     *
     * @see BaseResp
     *
     */
    public interface Resp : ApiData

}


/**
 * 返回值 [data] 为一个json实例对象的结果。
 */
@Serializable
public data class ObjectResp<RESP : Resp>(
    /**
     * integer, 错误码，0代表成功，非0代表失败，具体的错误码参见错误码一览
     */
    val code: Int,
    /**
     * string, 错误消息，具体的返回消息会根据Accept-Language来返回。
     */
    val message: String,
    /**
     * mixed, 具体的数据。
     */
    val data: RESP?
)

/**
 * 返回值为一个列表（数组）实例对象的结果。
 *
 * 列表返回的时候会有三个属性
 * - `items`, 为列表结果
 * - `meta`, 为分页信息
 * - `sort`, 为排序信息
 *
 * 其中，如果无法确定sort类型（字段值），则直接使用 Map<String, Int>
 *
 */
@Serializable
public data class ListResp<RESP : Resp, SORT>(
    /**
     * integer, 错误码，0代表成功，非0代表失败，具体的错误码参见错误码一览
     */
    val code: Int,
    /**
     * string, 错误消息，具体的返回消息会根据Accept-Language来返回。
     */
    val message: String,
    /**
     * mixed, 具体的数据。
     */
    val data: ListRespData<RESP, SORT>
)

/**
 * 返回值为一个列表（数组）实例对象的结果。
 *
 * 列表返回的时候会有三个属性
 * - `items`, 为列表结果
 * - `meta`, 为分页信息
 * - `sort`, 为排序信息
 *
 * 其中，sort 类型为 Map<String, Int>
 *
 */
@Serializable
public data class ListRespForMapSort<RESP : Resp>(
    /**
     * integer, 错误码，0代表成功，非0代表失败，具体的错误码参见错误码一览
     */
    val code: Int,
    /**
     * string, 错误消息，具体的返回消息会根据Accept-Language来返回。
     */
    val message: String,
    /**
     * mixed, 具体的数据。
     */
    val data: ListRespDataForMapSort<RESP>
)


@Serializable
public data class ListRespData<RESP : Resp, SORT>(
    val items: List<RESP> = emptyList(),
    val meta: RespMeta,
    val sort: SORT? = null
)

@Serializable
public data class ListRespDataForMapSort<RESP : Resp>(
    val items: List<RESP> = emptyList(),
    val meta: RespMeta,
    val sort: Map<String, Int> = emptyMap()
)

@Serializable
public data class RespMeta(
    val page: Int,
    @SerialName("page_total")
    val pageTotal: Int,
    @SerialName("page_size")
    val pageSize: Int,
    val total: Int
)



/**
 * [Req] 基础抽象类。
 */
public abstract class BaseReq<RESP : Resp>(
    override val respType: KClass<out RESP>,
) : Req<RESP> {
    /**
     * api请求路径。
     */
    abstract override val route: String

}








public data class ReqData<RESP : Resp>
@JvmOverloads
constructor(
    override val respType: KClass<out RESP>,
    override val route: String,
    override val authorization: String? = null,
    override val parameters: Any? = null,
) : Req<RESP>


@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY)
@DslMarker
public annotation class ReqBuilderDsl


public class ReqBuilder<RESP : Resp>
@JvmOverloads
constructor(var respType: KClass<out RESP>? = null, var route: String? = null) :
    Builder<Req<RESP>> {
    @ReqBuilderDsl
    var authorization: String? = null

    @ReqBuilderDsl
    var parameters: Any? = null

    /** Build instance. */
    override fun build(): Req<RESP> = ReqData(
        requireNotNull(respType) { "Require respType was null." },
        requireNotNull(route) { "Require route was null." },
        authorization,
        parameters
    )

}


public inline fun <reified RESP : Resp> req(
    route: String? = null,
    block: ReqBuilder<RESP>.() -> Unit,
): Req<RESP> {
    return ReqBuilder(RESP::class, route).apply(block).build()
}
