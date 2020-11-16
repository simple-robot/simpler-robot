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

@file:JvmName("HttpTemplateUtil")
package love.forte.simbot.http.template


/**
 *
 * ## 简易的 http 客户端
 *
 * 简易的 http 客户端，提供部分最常见的一些可以得到返回值的 **同步** 请求方式。
 *
 *
 * ## 同步请求
 *
 * 此模板下的所有请求均为 **同步请求**，即它们均可以得到一个或多个 [响应体][HttpResponse]。
 *
 *
 * ## Json序列化
 *
 * 针对于请求与响应的 json 序列化是通过 `serialization-json-*(json序列化相关模块)` 完成的。
 *
 * 但是此模块不会引入 `serialization-json-*` 相关依赖，如果任何实现模块有需要则自行引入。
 * 一般子模块在实现的时候，只需要引入 `serialization-json-core` 而不是一个具体实现的模块,
 * 其序列化具体实现则取决于使用者的实际项目环境而定。
 *
 * @author ForteScarlet -> https://github.com/ForteScarlet
 */
public interface HttpTemplate {

    /**
     * get请求。
     * @param responseType 响应body封装类型。如果为null则认为忽略返回值，则response中的getBody也为null。
     */
    fun <T> get(url: String, responseType: Class<T>): HttpResponse<T>

    /**
     * get请求。
     * @param responseType 响应body封装类型。
     * @param headers 请求头信息。
     */
    fun <T> get(url: String, headers: HttpHeaders?, responseType: Class<T>): HttpResponse<T>

    /**
     * get请求。
     * @param responseType 响应body封装类型。
     * @param headers 请求头信息。
     * @param requestParam 请求参数。
     */
    fun <T> get(url: String, headers: HttpHeaders?, requestParam: Map<String, Any?>?, responseType: Class<T>): HttpResponse<T>

    /**
     * post/json 请求。
     * @param responseType 响应body封装类型。
     */
    fun <T> post(url: String, responseType: Class<T>): HttpResponse<T>

    /**
     * post/json 请求。
     * @param responseType 响应body封装类型。
     * @param headers 请求头信息。
     */
    fun <T> post(url: String, headers: HttpHeaders?, responseType: Class<T>): HttpResponse<T>

    /**
     * post/json 请求。
     * @param responseType 响应body封装类型。
     * @param headers 请求头信息。
     * @param requestBody 请求参数，一个对象实例，或者一个json字符串。
     */
    fun <T> post(url: String, headers: HttpHeaders?, requestBody: Any?, responseType: Class<T>): HttpResponse<T>

    /**
     * post/form 请求。
     * @param responseType 响应body封装类型。
     */
    fun <T> form(url: String, responseType: Class<T>): HttpResponse<T>

    /**
     * post/form 请求。
     * @param responseType 响应body封装类型。
     * @param headers 请求头信息。
     */
    fun <T> form(url: String, headers: HttpHeaders?, responseType: Class<T>): HttpResponse<T>

    /**
     * post/form 请求。
     * @param responseType 响应body封装类型。
     * @param headers 请求头信息。
     * @param requestForm 请求参数，一个对象实例，此对象实例只会获取其中一层字段值作为表单提交，不会像json那样嵌套获取。如果字段对应的是一个其他的实例，则会直接获取其toString的值。
     */
    fun <T> form(url: String, headers: HttpHeaders?, requestForm: Map<String, Any?>?, responseType: Class<T>): HttpResponse<T>


    /**
     * 使用请求实例请求。
     * 如果 [request.type][HttpRequest.type] 为 [get][HttpRequestType.GET] 或者 [form][HttpRequestType.FORM]，
     * 则 [request.requestParam][HttpRequest.requestParam] 应该为 `Map<String, Any?>?` 类型实例。
     */
    fun <T> request(request: HttpRequest<T>): HttpResponse<T>

    /**
     * 请求多个，其中，如果 [HttpRequest.responseType] 为null，则其请求结果将不会出现返回值中。
     *
     * 如果[parallel] 为true，则应并行请求[requests]并按照顺序同步返回最终结果。如果为false，则按照顺序依次阻塞请求所有。
     *
     * @param parallel 是否异步请求。
     * @return 全部的响应结果，其顺序为 [requests] 中的顺序。
     */
    fun requestAll(parallel: Boolean, vararg requests: HttpRequest<*>): List<HttpResponse<*>>


}


/**
 * http 的请求相应简易模型。
 * 记得重写 [toString]
 */
public interface HttpResponse<T> {
    /**
     * 响应状态码
     */
    val statusCode: Int


    /**
     * 响应体
     */
    val body: T


    /**
     * 获取响应的请求头信息。
     */
    val headers: HttpHeaders


    /**
     * 如果响应出现错误等情况，则此处可能为响应的错误信息。
     */
    val message: String?

    /**
     * 需要重写toString
     */
    override fun toString(): String
}


public data class HttpResponseData<T>(
    override val statusCode: Int,
    override val body: T,
    override val headers: HttpHeaders,
    override val message: String?
): HttpResponse<T>




















