/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  component-onebot
 *  * File     RestTeamplateHttpTemplate.java
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
package love.forte.simbot.http.template.spring

import love.forte.common.ioc.annotation.SpareBeans
import love.forte.simbot.http.template.*
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestTemplate
import java.util.*
import org.springframework.http.HttpHeaders as SpringHeaders

/**
 * 基于 [org.springframework.web.client.RestTemplate] 的 [love.forte.simbot.http.template.HttpTemplate] 实现。
 * @author ForteScarlet
 */
@SpareBeans
class RestTemplateHttpTemplate(private val restTemplate: RestTemplate) : BaseHttpTemplate() {

    /**
     * get请求。
     * @param responseType 响应body封装类型。
     * @param headers 请求头信息。
     * @param requestParam 请求参数。
     */
    override fun <T> get(
        url: String,
        headers: HttpHeaders?,
        cookies: HttpCookies?,
        requestParam: Map<String, Any?>?,
        responseType: Class<T>,
    ): HttpResponse<T> {
        val entity: ResponseEntity<T>
        val headerEmpty = headers == null || headers.isEmpty()
        val cookieEmpty = cookies == null || cookies.isEmpty()
        if (headerEmpty && cookieEmpty) {
            entity = if (requestParam != null) {
                restTemplate.getForEntity(url, responseType, requestParam)
            } else {
                restTemplate.getForEntity(url, responseType)
            }
        } else {
            val spHeaders = SpringHeaders()
            if (!headerEmpty) {
                spHeaders.putAll(headers!!)
            }
            if (!cookieEmpty) {
                val cookieList: MutableList<String> = ArrayList(cookies!!.size)
                val sb = StringBuilder()
                cookies.forEach {
                    sb.append(it.name).append('=').append(it.value)
                    cookieList.add(sb.toString())
                    sb.delete(0, sb.length)
                }

                spHeaders[SpringHeaders.COOKIE] = cookieList
            }
            val requestEntity = HttpEntity<Any>(spHeaders)
            entity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, responseType, requestParam)
        }
        return RestHttpResponse(entity)
    }

    /**
     * post请求。
     * @param responseType 响应body封装类型。
     * @param headers 请求头信息。
     * @param requestBody 请求参数。
     */
    override fun <T> post(
        url: String,
        headers: love.forte.simbot.http.template.HttpHeaders?,
        requestBody: Any?,
        responseType: Class<T>
    ): HttpResponse<T> {
        val entity: ResponseEntity<T>
        val headerEmpty = headers == null || headers.isEmpty()
        if (headerEmpty) {
            entity = restTemplate.postForEntity(url, requestBody, responseType)
        } else {
        }

        TODO()
    }

    /**
     * post/form 请求。
     * @param responseType 响应body封装类型。
     * @param headers 请求头信息。
     * @param requestForm 请求参数，一个对象实例，此对象实例只会获取其中一层字段值作为表单提交，不会像json那样嵌套获取。如果字段对应的是一个其他的实例，则会直接获取其toString的值。
     */
    override fun <T> form(
        url: String,
        headers: HttpHeaders?,
        requestForm: Map<String, Any?>?,
        responseType: Class<T>,
    ): HttpResponse<T> {
        TODO("Not yet implemented")
    }

    /**
     * 使用请求实例请求。
     * 如果 [request.type][HttpRequest.type] 为 [get][HttpRequestType.GET] 或者 [form][HttpRequestType.FORM]，
     * 则 [request.requestParam][HttpRequest.requestParam] 应该为 `Map<String></String>, Any?>?` 类型实例。
     */
    override fun <T> request(request: HttpRequest<T>): HttpResponse<T> {
        TODO()
    }

    /**
     * 请求多个，其中，如果 [HttpRequest.responseType] 为null，则其请求结果将不会出现返回值中。
     *
     * 如果[parallel] 为true，则应并行请求[requests]并按照顺序同步返回最终结果。如果为false，则按照顺序依次阻塞请求所有。
     *
     * @param parallel 是否异步请求。
     * @return 全部的响应结果，其顺序为 [requests] 中的顺序。
     */
    override fun requestAll(parallel: Boolean, vararg requests: HttpRequest<*>): List<HttpResponse<*>> {
        TODO()
    }
}