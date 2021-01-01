/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  component-onebot
 *  * File     RestHttpResponse.java
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

import love.forte.simbot.http.template.HttpHeaders
import love.forte.simbot.http.template.HttpResponse
import org.springframework.http.ResponseEntity

/**
 * [HttpResponse] 针对于 [ResponseEntity] 的委托实现。
 * @author ForteScarlet
 */
public class RestHttpResponse<T>(private val delegate: ResponseEntity<T>) : HttpResponse<T> {
    override val statusCode: Int
        get() = delegate.statusCodeValue

    override val body: T
        get() = delegate.body!!

    @Deprecated("Not Support", ReplaceWith("null"))
    override val content: String?
        get() = null

    override val headers: HttpHeaders = RestHttpHeader(delegate.headers)

    @Deprecated("Not Support", ReplaceWith("null"))
    override val message: String?
        get() = null

    /**
     * 需要重写toString
     */
    override fun toString(): String {
        return "RestHttpResponse(delegate=$delegate)"
    }
}