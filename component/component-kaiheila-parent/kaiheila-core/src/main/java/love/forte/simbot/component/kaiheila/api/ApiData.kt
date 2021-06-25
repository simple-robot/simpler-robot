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

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.*
import kotlinx.serialization.builtins.serializer
import love.forte.simbot.component.kaiheila.api.ApiData.Req


/**
 * 此接口定义一个与Api相关的数据类。
 *
 * 定义两个类型一个来回，分别为 [Request][Req] 和 [Response][RespData].
 *
 * @author ForteScarlet
 */
public sealed interface ApiData {

    /**
     * [Request][Req]. 请求相关的数据类。
     * 一次请求，都会有一个对应的 [响应][RESP].
     */
    public interface Req<HTTP_RESP : Resp<*>> : ApiData {

        /**
         * 拿到响应数据类型。
         *
         * @see objectResp
         * @see listResp
         *
         */
        val dataSerializer: DeserializationStrategy<HTTP_RESP>


        /**
         * 此api请求方式
         */
        val method: HttpMethod

        /**
         * 此请求对应的api路由路径以及路径参数。
         * 例如：`/guild/list`
         */
        fun route(builder: RouteInfoBuilder)


        /**
         * 此次请求所发送的数据。为null则代表没有参数。
         */
        val body: Any?

        /*
         * 获取请求的鉴权token。
         *
         * - 机器人。TOKEN_TYPE = Bot。 `Authorization: Bot BHsTZ4232tLatgV5AFyasgraefpl9mTxYQ/1380=`
         * - Oauth2。TOKEN_TYPE = Bearer。 `Authorization: Bearer BHsTZ4232tLatgVdvrfdboqZGAHHmasTxYQ/u41f0=`
         */
        // val authorization: String?


        /**
         * 得到一个 [Key]. 这个Key用于区分api。
         * 有时候，有可能对于同一个api的请求会提供多个不同的实现方案。
         */
        val key: Key


        /**
         * 这是一个 Key，
         */
        interface Key {
            val id: String
        }


    }


    /**
     * 此接口定义一个 开黑啦 http请求的响应值标准。 参考 [常规 http 接口规范](https://developer.kaiheila.cn/doc/reference#%E5%B8%B8%E8%A7%84%20http%20%E6%8E%A5%E5%8F%A3%E8%A7%84%E8%8C%83)
     *
     *
     * @see ObjectResp
     * @see ListResp
     */
    public sealed interface Resp<D> {
        /**
         * integer, 错误码，0代表成功，非0代表失败，具体的错误码参见错误码一览
         */
        val code: Int

        /**
         * string, 错误消息，具体的返回消息会根据Accept-Language来返回。
         */
        val message: String

        /**
         * 响应值。
         */
        val data: D


        /**
         * ResponseData. 请求响应相关的数据类。
         *
         * @see Resp
         *
         */
        interface Data : ApiData


        @Serializable
        object EmptySort

    }

}


public fun key(api: String): Req.Key = object : Req.Key {
    override val id: String = api
}


public interface RouteInfoBuilder {
    /**
     * 可以设置api路径
     */
    var apiPath: List<String>

    /**
     * 获取parameter的构建器
     */
    val parametersBuilder: ParametersBuilder


    companion object {
        @JvmStatic
        fun getInstance(parametersBuilder: ParametersBuilder): RouteInfoBuilder =
            RouteInfoBuilderImpl(parametersBuilder = parametersBuilder)
    }
}


public inline fun RouteInfoBuilder.parameters(block: ParametersBuilder.() -> Unit) {
    parametersBuilder.block()
}

public inline fun <reified T> ParametersBuilder.appendIfNotnull(name: String, value: T?, toStringBlock: (T) -> String = { it.toString() }) {
    value?.let { v -> append(name, toStringBlock(v)) }
}


private data class RouteInfoBuilderImpl(
    override var apiPath: List<String> = emptyList(),
    override val parametersBuilder: ParametersBuilder,
) : RouteInfoBuilder


/**
 * 返回值是一个非列表值. 指定一个响应元素类型 [RESP].
 */
public fun <RESP : ApiData.Resp.Data> objectResp(subSerializer: KSerializer<RESP>): KSerializer<ObjectResp<RESP>> =
    ObjectResp.serializer(subSerializer)

/**
 * 返回值是一个列表类型. 指定列表元素类型 [RESP] 和 排序参数类型 [SORT].
 */
public fun <RESP : ApiData.Resp.Data, SORT> listResp(
    subSerializer: KSerializer<RESP>,
    sorterSerializer: KSerializer<SORT>,
): KSerializer<ListResp<RESP, SORT>> = ListResp.serializer(subSerializer, sorterSerializer)






/**
 * 返回值是一个列表类型. 排序类型是 [String] 类型。
 */
public fun <RESP : ApiData.Resp.Data> listResp(
    subSerializer: KSerializer<RESP>,
): KSerializer<ListResp<RESP, String>> = ListResp.serializer(subSerializer, String.serializer())


/**
 * 返回值 [data] 为一个json实例对象的结果。
 */
@Serializable
public data class ObjectResp<RESP : ApiData.Resp.Data>(
    /**
     * integer, 错误码，0代表成功，非0代表失败，具体的错误码参见错误码一览
     */
    override val code: Int,
    /**
     * string, 错误消息，具体的返回消息会根据Accept-Language来返回。
     */
    override val message: String,
    /**
     * mixed, 具体的数据。
     */
    override val data: RESP?,
) : ApiData.Resp<RESP?>

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
public data class ListResp<RESP : ApiData.Resp.Data, SORT>(
    /**
     * integer, 错误码，0代表成功，非0代表失败，具体的错误码参见错误码一览
     */
    override val code: Int,
    /**
     * string, 错误消息，具体的返回消息会根据Accept-Language来返回。
     */
    override val message: String,
    /**
     * mixed, 具体的数据。
     */
    override val data: ListRespData<RESP, SORT>,
) : ApiData.Resp<ListRespData<RESP, SORT>>

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
public data class ListRespForMapSort<RESP : ApiData.Resp.Data>(
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
    val data: ListRespDataForMapSort<RESP>,
)


@Serializable
public data class ListRespData<RESP : ApiData.Resp.Data, SORT>(
    val items: List<RESP> = emptyList(),
    val meta: RespPageMeta,
    val sort: SORT,
)

@Serializable
public data class ListRespDataForMapSort<RESP : ApiData.Resp.Data>(
    val items: List<RESP> = emptyList(),
    val meta: RespPageMeta,
    val sort: Map<String, Int> = emptyMap(),
)

@Serializable
public data class RespPageMeta(
    val page: Int,
    @SerialName("page_total")
    val pageTotal: Int,
    @SerialName("page_size")
    val pageSize: Int,
    val total: Int,
)


// public data class ReqData<HTTP_RESP : ApiData.HttpResp<*>>
// @JvmOverloads
// constructor(
//     override val route: String,
//     override val authorization: String? = null,
//     override val body: Any? = null,
//     private val doClient: suspend (client: HttpClient, block: HttpRequestBuilder.() -> Unit) -> HTTP_RESP,
// ) : Req<HTTP_RESP> {
//     override suspend fun request(client: HttpClient, block: HttpRequestBuilder.() -> Unit): HTTP_RESP =
//         doClient(client, block)
// }
//
//
// @Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY)
// @DslMarker
// public annotation class ReqBuilderDsl
//
//
// public class ReqBuilder<HTTP_RESP : ApiData.HttpResp<*>>
// @JvmOverloads
// constructor(
//     var route: String? = null,
//     var doClient: (suspend (client: HttpClient, block: HttpRequestBuilder.() -> Unit) -> HTTP_RESP)? = null,
// ) {
//     @ReqBuilderDsl
//     var authorization: String? = null
//
//     @ReqBuilderDsl
//     var parameters: Any? = null
//
//     /** Build instance. */
//     fun build(): Req<HTTP_RESP> = ReqData(
//         route = requireNotNull(route) { "Require route was null." },
//         authorization = authorization,
//         body = parameters,
//         doClient = requireNotNull(doClient) { "Require doClient function was null." },
//     )
//
// }
//
//
// public inline fun <reified HTTP_RESP : ApiData.HttpResp<*>> req(
//     route: String? = null,
//     noinline doClient: suspend (client: HttpClient, block: HttpRequestBuilder.() -> Unit) -> HTTP_RESP,
//     block: ReqBuilder<HTTP_RESP>.() -> Unit,
// ): Req<HTTP_RESP> {
//     return ReqBuilder(route, doClient).apply(block).build()
// }
