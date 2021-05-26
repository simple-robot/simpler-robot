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
public interface ApiData {

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
     */
    public interface Resp : ApiData

}


/**
 * [Req] 基础抽象类。
 */
public abstract class BaseReq<RESP : Resp>(override val respType: KClass<out RESP>) : Req<RESP> {
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
