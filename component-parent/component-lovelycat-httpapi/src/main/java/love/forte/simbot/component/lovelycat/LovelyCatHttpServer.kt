/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  simple-robot-S
 *  * File     LovelyCatHttpServer.kt
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
@file:JvmName("lovelycatHttpServers")
package love.forte.simbot.component.lovelycat

import io.ktor.application.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.util.pipeline.*
import io.ktor.utils.io.*
import love.forte.simbot.bot.NoSuchBotException
import love.forte.simbot.component.lovelycat.configuration.LovelyCatServerProperties
import love.forte.simbot.component.lovelycat.message.event.LovelyCatParser
import love.forte.simbot.core.TypedCompLogger
import love.forte.simbot.listener.MsgGetProcessor
import love.forte.simbot.serialization.json.JsonSerializer
import love.forte.simbot.serialization.json.JsonSerializerFactory
import java.io.Closeable
import kotlin.concurrent.thread
import kotlin.reflect.jvm.jvmErasure


private val jsonContentType = ContentType.parse("application/json")
private val htmlContentType = ContentType.parse("text/html")

/**
 * maybe not use.
 */
private class JsonContentConverter(private val fac: JsonSerializerFactory) : ContentConverter {

    override suspend fun convertForReceive(
        context: PipelineContext<ApplicationReceiveRequest, ApplicationCall>
    ): Any? {
        val channel = context.subject.value as ByteReadChannel
        val message = StringBuilder().apply {
            var readLine: Boolean
            do {
                readLine = channel.readUTF8LineTo(this)
            } while(readLine)
            // while(content.readUTF8LineTo(this)) { }
            channel.cancel()
        }.toString()
        // val message = channel.readUTF8Line() ?: "{}"
        return fac.getJsonSerializer(context.subject.typeInfo.jvmErasure.java).fromJson(message)
    }

    override suspend fun convertForSend(
        context: PipelineContext<Any, ApplicationCall>,
        contentType: ContentType,
        value: Any
    ): Any? {
        val jsonSerializer: JsonSerializer<Any> = fac.getJsonSerializer(context.subject.javaClass)
        return jsonSerializer.toJson(context.subject)
    }



}


interface LovelyCatHttpServer : Closeable {
    @Throws(Exception::class)
    fun start()
}



public class LovelyCatKtorHttpServer(
    /** 类型转化函数，根据 'Event' 参数获取对应的解析对象 */
    lovelyCatParser: LovelyCatParser,
    applicationEngineFactory: ApplicationEngineFactory<ApplicationEngine, out ApplicationEngine.Configuration>,
    apiManager: LovelyCatApiManager,
    jsonSerializerFactory: JsonSerializerFactory,
    msgGetProcessor: MsgGetProcessor,
    private val lovelyCatServerProperties: LovelyCatServerProperties,
) : LovelyCatHttpServer {
    private companion object : TypedCompLogger(LovelyCatKtorHttpServer::class.java)

    private val mapSerializer = jsonSerializerFactory.getJsonSerializer<Map<String, *>>(Map::class.java)

    private val port get() = lovelyCatServerProperties.port
    private val path get() = lovelyCatServerProperties.path

    init {
        Runtime.getRuntime().addShutdownHook(thread(start = false) {
            close()
        })
    }

    /**
     * the server instance.
     */
    private val server: ApplicationEngine by lazy {
        embeddedServer(applicationEngineFactory, port) {

            install(ContentNegotiation) {
                register(jsonContentType, JsonContentConverter(jsonSerializerFactory))
            }

            if(lovelyCatServerProperties.cors) {
                install(CORS)
            }

            routing {
                // listen path.
                post(path) {
                    val originalData = call.receive<String>()

                    val params = mapSerializer.fromJson(originalData)

                    val eventType = params["Event"]?.toString()

                    println("eventType: $eventType")

                    if (eventType == null) {
                        // 404. no event.
                        call.response.status(HttpStatusCode.NotFound)
                        call.respondText { "param 'Event' not found: Event is Empty." }
                    } else {

                        val botId = (params["robot_wxid"] ?: params["rob_wxid"])?.toString()
                            ?: throw NoSuchBotException("no param 'robot_wxid' or 'rob_wxid' in lovelycat request param.")

                        val api = apiManager[botId] ?: throw IllegalStateException("cannot found Bot($botId)'s api template.")

                        val parse = lovelyCatParser.parse(eventType, originalData, api, jsonSerializerFactory, params)

                        if (parse != null) {
                            msgGetProcessor.onMsg(parse)
                        }

                        // ok status.
                        call.response.status(HttpStatusCode.OK)
                    }
                }

                get("/simbot/lovelyCat") {
                    call.respondText(htmlContentType) {
                        "<h2>lovely cat server enabled!</h2> "
                    }

                }

            }
        }
    }



    override fun start() {
        server.start()
        logger.info("lovelycat ktor server started on <address>:$port$path")

    }

    /**
     * close server.
     */
    override fun close() {
        server.stop(5000, 5000)
    }

}








