/*
 * Copyright (c) 2022-2024. ForteScarlet.
 *
 * This file is part of simbot-component-qq-guild.
 *
 * simbot-component-qq-guild is free software: you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * simbot-component-qq-guild is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with simbot-component-qq-guild.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot.qguild.api

import io.ktor.http.*
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.builtins.serializer
import love.forte.simbot.common.apidefinition.*
import love.forte.simbot.logger.Logger
import love.forte.simbot.logger.LoggerFactory
import love.forte.simbot.qguild.QQGuild

/**
 * 用于在 [QQGuildApi.request] 及其衍生API中输出相关日志的 [Logger]。
 * 开启 `love.forte.simbot.qguild.api` 的 `DEBUG` 级别日志可以得到更多在 API 请求过程中产生的信息。
 * 注意：这可能会暴露其中的参数、返回值等，请注意保护敏感信息。
 */
public val apiLogger: Logger = LoggerFactory.getLogger("love.forte.simbot.qguild.api")

/**
 * [有关 traceID](https://bot.q.qq.com/wiki/develop/api/openapi/error/error.html#%E6%9C%89%E5%85%B3-traceid)
 *
 * 在 openapi 的返回 http 头上，有一个 X-Tps-trace-ID 自定义头部，是平台的链路追踪 ID，
 * 如果开发者有无法自己定位的问题，需要找平台协助的时候，可以提取这个 ID，提交给平台方。
 *
 * 方便查询相关日志。
 *
 */
@PublishedApi
internal const val TRACE_ID_HEAD: String = "X-Tps-trace-ID"

/**
 * 表示为一个QQ频道的API。
 *
 * 通过 [requestData] 发起一次请求。
 *
 * ### 日志
 *
 * [QQGuildApi] 在进行请求的过程中会通过名称 `love.forte.simbot.qguild.api`
 * 输出 `DEBUG` 日志，其中：
 * ```
 * [   GET /foo/bar] =====> api.xxx.com, body: xxx
 * ```
 * 向右箭头的日志代表 `request` 相关的信息，
 * ```
 * [   GET /foo/bar] <===== status: xxx, body: xxx
 * ```
 * 向左的日志则代表 `response` 相关的信息。
 *
 * 如果希望关闭日志中 `request method` 的染色，添加JVM参数
 * ```
 * -Dsimbot.qguild.api.logger.color.enable=false
 * ```
 */
public interface QQGuildApi<out R : Any> : ApiDefinition<R> {

    /**
     * 得到响应值的反序列化器.
     */
    override val resultDeserializationStrategy: DeserializationStrategy<R>

    /**
     * 最终用于请求的目标地址。
     * 默认会使用 [QQGuild.URL]
     * 作为其域名地址。
     */
    override val url: Url

    /**
     * 此api请求方式
     */
    override val method: HttpMethod

    /**
     * 此次请求所发送的数据。为null则代表没有参数。
     */
    override val body: Any?

    public companion object
}

public interface QQGuildApiWithoutResult : QQGuildApi<Unit> {
    override val resultDeserializationStrategy: DeserializationStrategy<Unit>
        get() = Unit.serializer() //EmptyUnitSerializer
}

public abstract class GetQQGuildApi<R : Any> : GetApiDefinition<R>(), QQGuildApi<R> {
    protected abstract val path: Array<String>

    /**
     * 已经完成 [path] 拼接后、追加其他额外内容（例如parameter）时使用
     */
    protected open fun URLBuilder.buildUrl() {}

    override val url: Url
        get() = URLBuilder(QQGuild.URL).apply {
            appendEncodedPathSegments(components = path)
            buildUrl()
        }.build()
}

public abstract class PostQQGuildApi<R : Any> : PostApiDefinition<R>(), QQGuildApi<R> {
    protected abstract val path: Array<String>

    /**
     * 已经完成 [path] 拼接后、追加其他额外内容（例如parameter）时使用
     */
    protected open fun URLBuilder.buildUrl() {}

    override val url: Url
        get() = URLBuilder(QQGuild.URL).apply {
            appendEncodedPathSegments(components = path)
            buildUrl()
        }.build()
}

public abstract class PutQQGuildApi<R : Any> : PutApiDefinition<R>(), QQGuildApi<R> {
    protected abstract val path: Array<String>

    /**
     * 已经完成 [path] 拼接后、追加其他额外内容（例如parameter）时使用
     */
    protected open fun URLBuilder.buildUrl() {}

    override val url: Url
        get() = URLBuilder(QQGuild.URL).apply {
            appendEncodedPathSegments(components = path)
            buildUrl()
        }.build()
}

public abstract class DeleteQQGuildApi<R : Any> : DeleteApiDefinition<R>(), QQGuildApi<R> {
    protected abstract val path: Array<String>

    /**
     * 已经完成 [path] 拼接后、追加其他额外内容（例如parameter）时使用
     */
    protected open fun URLBuilder.buildUrl() {}

    override val url: Url
        get() = URLBuilder(QQGuild.URL).apply {
            appendEncodedPathSegments(components = path)
            buildUrl()
        }.build()
}

public abstract class PatchQQGuildApi<R : Any> : PutQQGuildApi<R>(), QQGuildApi<R> {
    override val method: HttpMethod
        get() = HttpMethod.Patch
}

internal fun HttpMethod.defaultForLogName(): String = when (this) {
    HttpMethod.Get -> "   GET"
    HttpMethod.Post -> "  POST"
    HttpMethod.Put -> "   PUT"
    HttpMethod.Patch -> " PATCH"
    HttpMethod.Delete -> "DELETE"
    else -> value
}

/**
 * 日志对齐
 */
@PublishedApi
internal expect val HttpMethod.logName: String
