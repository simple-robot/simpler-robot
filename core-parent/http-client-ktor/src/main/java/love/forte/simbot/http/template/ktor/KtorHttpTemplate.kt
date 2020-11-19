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
@file:JvmName("KtorHttpTemplates")

package love.forte.simbot.http.template.ktor

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.util.*
import io.ktor.utils.io.*
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import love.forte.simbot.http.template.*
import love.forte.simbot.http.template.HttpHeaders
import love.forte.simbot.http.template.HttpRequest
import love.forte.simbot.serialization.json.JsonSerializerFactory
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import io.ktor.client.statement.HttpResponse as KtorHttpResponse


private val APPLICATION_JSON = ContentType("application", "json")
private val APPLICATION_FORM_URLENCODED = ContentType("application", "x-www-form-urlencoded")


/**
 *
 * 基于 ktor cio client 的 http 请求模板。
 *
 * @author ForteScarlet -> https://github.com/ForteScarlet
 */
public class KtorHttpTemplate
@JvmOverloads
constructor(
    private val client: HttpClient = HttpClient(),
    private val jsonSerializerFactory: JsonSerializerFactory
) : HttpTemplate {


    private fun <T> KtorHttpResponse.toResponse(responseType: Class<T>): HttpResponse<T> {
        return if (responseType == String::class.java) {
            KtorHttpResponseImpl(this) { it } as HttpResponse<T>
        } else {
            val jsonSerializer = jsonSerializerFactory.getJsonSerializer(responseType)
            KtorHttpResponseImpl(this) { jsonSerializer.fromJson(it) }
        }
    }

    /**
     * ktor get请求。
     */
    override fun <T> get(url: String, responseType: Class<T>): HttpResponse<T> =
        get(url, null, null, responseType)

    /**
     * ktor get请求。
     */
    override fun <T> get(url: String, headers: HttpHeaders?, responseType: Class<T>): HttpResponse<T> =
        get(url, headers, null, responseType)

    /**
     * ktor get请求。
     */
    override fun <T> get(
        url: String,
        headers: HttpHeaders?,
        requestParam: Map<String, Any?>?,
        responseType: Class<T>
    ): HttpResponse<T> = runBlocking {
        val response: KtorHttpResponse = client.get(url) {
            headers?.forEach { (k, vs) ->
                headers { appendAll(k, vs) }
            }
            requestParam?.forEach { (k, v) ->
                parameter(k, v)
            }
        }

        response.toResponse(responseType)
    }


    override fun <T> post(url: String, responseType: Class<T>): HttpResponse<T> =
        post(url, null, null, responseType)

    override fun <T> post(url: String, headers: HttpHeaders?, responseType: Class<T>): HttpResponse<T> =
        post(url, headers, null, responseType)

    override fun <T> post(
        url: String,
        headers: HttpHeaders?,
        requestBody: Any?,
        responseType: Class<T>
    ): HttpResponse<T> = runBlocking {
        val response: KtorHttpResponse = client.post(url) {
            headers?.forEach { (k, vs) ->
                headers { appendAll(k, vs) }
            }
            if (this.contentType() == null) {
                this.contentType(APPLICATION_JSON)
            }
            requestBody?.let {
                when (it) {
                    is List<*> -> jsonSerializerFactory.getJsonSerializer<Any>(List::class.java)
                    is Set<*> -> jsonSerializerFactory.getJsonSerializer<Any>(Set::class.java)
                    is Map<*, *> -> jsonSerializerFactory.getJsonSerializer<Any>(Map::class.java)
                    is Collection<*> -> jsonSerializerFactory.getJsonSerializer<Any>(Collection::class.java)
                    else -> jsonSerializerFactory.getJsonSerializer(it.javaClass)
                }?.apply {
                    body = toJson(it)
                }
            }
        }


        response.toResponse(responseType)
    }

    override fun <T> form(url: String, responseType: Class<T>): HttpResponse<T> =
        form(url, null, null, responseType)

    override fun <T> form(url: String, headers: HttpHeaders?, responseType: Class<T>): HttpResponse<T> =
        form(url, headers, null, responseType)

    override fun <T> form(
        url: String,
        headers: HttpHeaders?,
        requestForm: Map<String, Any?>?,
        responseType: Class<T>
    ): HttpResponse<T> = runBlocking {


        val response: KtorHttpResponse = client.submitForm(url) {
            headers?.forEach { (k, vs) ->
                headers { appendAll(k, vs) }
            }
            if (this.contentType() == null) {
                this.contentType(APPLICATION_FORM_URLENCODED)
            }
            requestForm?.also {
                it.forEach { (k, v) ->
                    parameter(k, v)
                }
            }
        }

        response.toResponse(responseType)
    }


    /**
     * 发送一个请求。
     * @param request HttpRequest<T>
     * @return HttpResponse<T> 响应体
     */
    override fun <T> request(request: HttpRequest<T>): HttpResponse<T> {
        val url = request.url
        val headers = request.headers
        val params = request.requestParam
        val responseType = request.responseType

        return when (request.type) {
            HttpRequestType.GET -> get(url, headers, params as? Map<String, Any?>, responseType)
            HttpRequestType.FORM -> form(url, headers, params as? Map<String, Any?>, responseType)
            HttpRequestType.POST -> post(url, headers, params, responseType)
        }
    }

    /**
     * 请求多个请求. 当 [parallel] 为true的时候，通过协程并行计算多个请求。
     *
     * 返回的 list 定义为 **read-only** list, 不应做修改。
     *
     * @param requests 请求列表。
     * @return List<HttpResponse<*>>
     */
    override fun requestAll(parallel: Boolean, vararg requests: HttpRequest<*>): List<HttpResponse<*>> {
        if (requests.isEmpty()) {
            return emptyList()
        }


        fun getBlock(headers: HttpHeaders?): HttpRequestBuilder.() -> Unit {
            return {
                headers?.forEach { (k, vs) ->
                    headers { appendAll(k, vs) }
                }
            }
        }

        return if (requests.size == 1) {
            // only one.
            listOf(request(requests.first()))
        } else {
            if (parallel) {
                requests.map { request ->
                    val url = request.url
                    val headers = request.headers
                    val params = request.requestParam
                    val responseType = request.responseType

                    val block: HttpRequestBuilder.() -> Unit = getBlock(headers)

                    // first:  type
                    // second: async response
                    responseType to
                            when (request.type) {

                                // get
                                HttpRequestType.GET -> {
                                    val getBlock: HttpRequestBuilder.() -> Unit = {
                                        block()
                                        val requestParams = params as? Map<String, Any?>
                                        requestParams?.forEach { (k, v) ->
                                            parameter(k, v)
                                        }
                                    }
                                    GlobalScope.async { client.get<KtorHttpResponse>(url, getBlock) }
                                }

                                // post
                                HttpRequestType.POST -> {
                                    val postBlock: HttpRequestBuilder.() -> Unit = {
                                        block()
                                        if (this.contentType() == null) {
                                            this.contentType(APPLICATION_JSON)
                                        }
                                        params?.let {
                                            when (it) {
                                                is List<*> -> jsonSerializerFactory.getJsonSerializer<Any>(List::class.java)
                                                is Set<*> -> jsonSerializerFactory.getJsonSerializer<Any>(Set::class.java)
                                                is Map<*, *> -> jsonSerializerFactory.getJsonSerializer<Any>(Map::class.java)
                                                is Collection<*> -> jsonSerializerFactory.getJsonSerializer<Any>(Collection::class.java)
                                                else -> jsonSerializerFactory.getJsonSerializer(it.javaClass)
                                            }?.apply {
                                                body = toJson(it)
                                            }
                                        }
                                    }
                                    GlobalScope.async { client.post<KtorHttpResponse>(url, postBlock) }
                                }

                                // form
                                HttpRequestType.FORM -> {
                                    val postBlock: HttpRequestBuilder.() -> Unit = {
                                        block()
                                        if (this.contentType() == null) {
                                            this.contentType(APPLICATION_FORM_URLENCODED)
                                        }
                                        val requestForm = params as? Map<String, Any?>?
                                        requestForm?.also {
                                            it.forEach { (k, v) ->
                                                parameter(k, v)
                                            }
                                        }
                                    }
                                    GlobalScope.async { client.submitForm<KtorHttpResponse>(url, block = postBlock) }
                                }
                            }
                }.map { runBlocking { it.second.await().toResponse(it.first) } }
            } else {
                // no pall, block.
                requests.map { request ->
                    val url = request.url
                    val headers = request.headers
                    val params = request.requestParam
                    val responseType = request.responseType

                    val block: HttpRequestBuilder.() -> Unit = getBlock(headers)

                    // first:  type
                    // second: response
                    val response = when (request.type) {

                        // get
                        HttpRequestType.GET -> {
                            val getBlock: HttpRequestBuilder.() -> Unit = {
                                block()
                                val requestParams = params as? Map<String, Any?>
                                requestParams?.forEach { (k, v) ->
                                    parameter(k, v)
                                }
                            }
                            runBlocking { client.get<KtorHttpResponse>(url, getBlock) }
                        }

                        // post
                        HttpRequestType.POST -> {
                            val postBlock: HttpRequestBuilder.() -> Unit = {
                                block()
                                if (this.contentType() == null) {
                                    this.contentType(APPLICATION_JSON)
                                }
                                params?.let {
                                    when (it) {
                                        is List<*> -> jsonSerializerFactory.getJsonSerializer<Any>(List::class.java)
                                        is Set<*> -> jsonSerializerFactory.getJsonSerializer<Any>(Set::class.java)
                                        is Map<*, *> -> jsonSerializerFactory.getJsonSerializer<Any>(Map::class.java)
                                        is Collection<*> -> jsonSerializerFactory.getJsonSerializer<Any>(Collection::class.java)
                                        else -> jsonSerializerFactory.getJsonSerializer(it.javaClass)
                                    }?.apply {
                                        body = toJson(it)
                                    }
                                }
                            }
                            runBlocking { client.post<KtorHttpResponse>(url, postBlock) }
                        }

                        // form
                        HttpRequestType.FORM -> {
                            val postBlock: HttpRequestBuilder.() -> Unit = {
                                block()
                                if (this.contentType() == null) {
                                    this.contentType(APPLICATION_FORM_URLENCODED)
                                }
                                val requestForm = params as? Map<String, Any?>?
                                requestForm?.also {
                                    it.forEach { (k, v) ->
                                        parameter(k, v)
                                    }
                                }
                            }
                            runBlocking { client.submitForm<KtorHttpResponse>(url, block = postBlock) }
                        }
                    }
                    response.toResponse(responseType)
                }
            }
        }

    }

}


public class KtorHttpResponseImpl<T>(
    response: KtorHttpResponse,
    ignoreContent: Boolean = false,
    bodySerializer: (String) -> T
) : HttpResponse<T> {

    private val logger: Logger = LoggerFactory.getLogger(KtorHttpResponseImpl::class.java)

    private var _contentAsync: Deferred<String?>? = if (ignoreContent) {
        null
    } else {
        GlobalScope.async {
            val content = response.content
            content.readUTF8Line().apply {
                content.cancel()
            }

        }
    }

    private var _content: String? = null
        get() {
            return if (_contentAsync == null) {
                field
            } else {
                synchronized(this) {
                    val contentAsync0 = _contentAsync
                    if (contentAsync0 == null) {
                        field
                    } else {
                        field = runBlocking { contentAsync0.await() }
                        _contentAsync = null
                        field
                    }
                }

            }

        }

    private val content: String? get() = _content

    /** status code. */
    override val statusCode: Int = response.status.value

    /** body. */
    override val body: T by lazy(LazyThreadSafetyMode.PUBLICATION) {
        logger.debug(content)
        content?.let { bodySerializer(it) } ?: throw IllegalStateException()
    }

    /** headers. */
    override val headers: HttpHeaders by lazy(LazyThreadSafetyMode.NONE) {
        HttpHeaders.fromMultiValueMap(response.headers.toMap())
    }

    /** error msg. */
    override val message: String? get() = if (statusCode < 300) null else content

    override fun toString(): String {
        return "KtorHttpResponse(status=$statusCode, content=$content)"
    }

}

















