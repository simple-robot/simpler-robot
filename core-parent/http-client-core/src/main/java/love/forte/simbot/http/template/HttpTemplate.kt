/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     HttpTemplate.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

@file:JvmName("HttpTemplateUtil")
package love.forte.simbot.http.template


/**
 * 简易的 http 客户端，提供部分最常见的一些请求方式。
 * @author ForteScarlet -> https://github.com/ForteScarlet
 */
public interface HttpTemplate {

    /**
     * get请求。
     * @param responseType 响应body封装类型。
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
    fun <T> get(url: String, headers: HttpHeaders?, requestParam: Map<String, Any>?, responseType: Class<T>): HttpResponse<T>

    /**
     * post/json 请求。
     * @param responseType 响应body封装类型。
     */
    fun <T> postJson(url: String, responseType: Class<T>): HttpResponse<T>

    /**
     * post/json 请求。
     * @param responseType 响应body封装类型。
     * @param headers 请求头信息。
     */
    fun <T> postJson(url: String, headers: HttpHeaders?, responseType: Class<T>): HttpResponse<T>

    /**
     * post/json 请求。
     * @param responseType 响应body封装类型。
     * @param headers 请求头信息。
     * @param requestBody 请求参数，一个对象实例，或者一个json字符串。
     */
    fun <T> postJson(url: String, headers: HttpHeaders?, requestBody: Any?, responseType: Class<T>): HttpResponse<T>

    /**
     * post/form 请求。
     * @param responseType 响应body封装类型。
     */
    fun <T> postForm(url: String, responseType: Class<T>): HttpResponse<T>

    /**
     * post/form 请求。
     * @param responseType 响应body封装类型。
     * @param headers 请求头信息。
     */
    fun <T> postForm(url: String, headers: HttpHeaders?, responseType: Class<T>): HttpResponse<T>

    // /**
    //  * post/form 请求。
    //  * @param responseType 响应body封装类型。
    //  * @param headers 请求头信息。
    //  * @param requestForm 请求参数，一个对象实例，此对象实例只会获取其中一层字段值作为表单提交，不会像json那样嵌套获取。如果字段对应的是一个其他的实例，则会直接获取其toString的值。
    //  */
    // fun <T> postForm(url: String, headers: HttpHeaders?, requestForm: Any?, responseType: Class<T>): HttpResponse<T>

    /**
     * post/form 请求。
     * @param responseType 响应body封装类型。
     * @param headers 请求头信息。
     * @param requestForm 请求参数，一个对象实例，此对象实例只会获取其中一层字段值作为表单提交，不会像json那样嵌套获取。如果字段对应的是一个其他的实例，则会直接获取其toString的值。
     */
    fun <T> postForm(url: String, headers: HttpHeaders?, requestForm: Map<String, Any>?, responseType: Class<T>): HttpResponse<T>

}


/**
 * http 的请求相应简易模型。
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
}

//
// /**
//  * 将一个实例对象解析为单层提交表单。
//  */
// public fun Any.toRequestForm(): Map<String, Any> {
//     return when(this) {
//         is String, is Number, is Char, is Boolean -> mapOf("" to this)
//
//         is Date -> mapOf("" to time)
//         is Instant -> mapOf("" to this.toEpochMilli())
//         is Map<*, *> -> this.asSequence()
//             .filter { it.key != null && it.value != null }
//             .map { it.key.toString() to it.value.toString() }
//             .toMap()
//         is Pair<*, *> -> if (first != null && second != null) mapOf(first.toString() to second.toString()) else emptyMap()
//         is Map.Entry<*, *> -> if (key != null && value != null) mapOf(key.toString() to value.toString()) else emptyMap()
//         is Collection<*> -> mapOf("" to this)
//         else -> {
//             if ()
//             this::class.members
//
//
//             TODO()
//         }
//     }
// }





















