/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     KtorHttpTemplate.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.simbot.http.template.ktor

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.util.*
import io.ktor.utils.io.*
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import love.forte.simbot.http.template.HttpHeaders
import love.forte.simbot.http.template.HttpRequest
import love.forte.simbot.http.template.HttpResponse
import love.forte.simbot.http.template.HttpTemplate
import love.forte.simbot.serialization.json.JsonSerializerFactory
import io.ktor.client.statement.HttpResponse as KtorHttpResponse

/**
 *
 * 基于 ktor cio client 的 http请求模板。
 *
 * @author ForteScarlet -> https://github.com/ForteScarlet
 */
public class KtorHttpTemplate
@JvmOverloads
constructor(
    private val httpClient: HttpClient = HttpClient(),
    private val jsonSerializerFactory: JsonSerializerFactory
) : HttpTemplate {


    /**
     * ktor get请求。
     */
    override fun <T> get(url: String, responseType: Class<T>): HttpResponse<T> = runBlocking {
        val response: KtorHttpResponse = httpClient.get(url)

        if (responseType == String::class.java) {
            KtorHttpResponseImpl(response) { it } as HttpResponse<T>
        } else {
            val jsonSerializer = jsonSerializerFactory.getJsonSerializer(responseType)
            KtorHttpResponseImpl(response) { jsonSerializer.fromJson(it) }
        }
    }

    override fun <T> get(url: String, headers: HttpHeaders?, responseType: Class<T>): HttpResponse<T> {
        println("no.")
        TODO("Not yet implemented")
    }

    override fun <T> get(
        url: String,
        headers: HttpHeaders?,
        requestParam: Map<String, Any>?,
        responseType: Class<T>
    ): HttpResponse<T> {
        TODO("Not yet implemented")
    }

    override fun <T> postJson(url: String, responseType: Class<T>): HttpResponse<T> {
        TODO("Not yet implemented")
    }

    override fun <T> postJson(url: String, headers: HttpHeaders?, responseType: Class<T>): HttpResponse<T> {
        TODO("Not yet implemented")
    }

    override fun <T> postJson(
        url: String,
        headers: HttpHeaders?,
        requestBody: Any?,
        responseType: Class<T>
    ): HttpResponse<T> {
        TODO("Not yet implemented")
    }

    override fun <T> postForm(url: String, responseType: Class<T>): HttpResponse<T> {
        TODO("Not yet implemented")
    }

    override fun <T> postForm(url: String, headers: HttpHeaders?, responseType: Class<T>): HttpResponse<T> {
        TODO("Not yet implemented")
    }

    override fun <T> postForm(
        url: String,
        headers: HttpHeaders?,
        requestForm: Map<String, Any>?,
        responseType: Class<T>
    ): HttpResponse<T> {
        TODO("Not yet implemented")
    }


    override fun <T> request(request: HttpRequest<T>): HttpResponse<T?> {
        TODO("Not yet implemented")
    }

    override fun requestAll(parallel: Boolean, vararg requests: HttpRequest<*>): List<HttpResponse<*>> {
        TODO("Not yet implemented")
    }

}


public class KtorHttpResponseImpl<T>(
    response: KtorHttpResponse,
    ignoreContent: Boolean = false,
    bodySerializer: (String) -> T
) : HttpResponse<T> {

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
        content?.let { bodySerializer(it) } ?: throw IllegalStateException()
    }

    /** headers. */
    override val headers: HttpHeaders by lazy(LazyThreadSafetyMode.NONE) {
        HttpHeaders.fromMultiValueMap(response.headers.toMap())
    }

    /** error msg. */
    override val message: String? get() = if (statusCode < 300) null else content

}


// public class KtorHttpHeadersImpl(private val headers: Headers): HttpHeaders {
//    
// }















