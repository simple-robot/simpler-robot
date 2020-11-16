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

package love.forte.simbot.http.template

/**
 * http 请求模型。
 *
 * 目前支持 get与post。
 *
 */
public interface HttpRequest<T> {
    /**
     * 请求类型。
     * 此类型与 [HttpTemplate] 中提供的类型一致。
     */
    val type: HttpRequestType


    /** 请求路径。 */
    val url: String

    /**
     * 请求后的响应值类型。可以为null。
     * 如果为null则认为此请求的返回值将被忽略。
     */
    val responseType: Class<T>

    /**
     * 请求头。可以为null。
     */
    val headers: HttpHeaders?

    /**
     * 请求参数。
     * 如果不是 `post/json` 类型，则此值需要为一个 `Map<String, Any?>`。
     */
    val requestParam: Any?

}

/**
 * get 类型的请求。
 */
public data class GetHttpRequest<T>
@JvmOverloads
constructor(
    override val url: String,
    override val responseType: Class<T>,
    override val headers: HttpHeaders? = null,
    override val requestParam: Map<String, Any?>? = null
) : HttpRequest<T> {
    override val type: HttpRequestType
        get() = HttpRequestType.GET
}


/**
 * post 类型的请求。
 */
public data class PostHttpRequest<T>
@JvmOverloads
constructor(
    override val url: String,
    override val responseType: Class<T>,
    override val headers: HttpHeaders? = null,
    override val requestParam: Any? = null
) : HttpRequest<T> {
    override val type: HttpRequestType
        get() = HttpRequestType.POST
}

/**
 * post 类型的请求。
 */
public data class FormHttpRequest<T>
@JvmOverloads
constructor(
    override val url: String,
    override val responseType: Class<T>,
    override val headers: HttpHeaders? = null,
    override val requestParam: Map<String, Any?>? = null
) : HttpRequest<T> {
    override val type: HttpRequestType
        get() = HttpRequestType.POST
}


/**
 * 请求类型。
 */
public enum class HttpRequestType {
    GET, POST, FORM
}
