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
import love.forte.simbot.component.lovelycat.message.event.BaseLovelyCatMsg
import love.forte.simbot.serialization.json.JsonSerializer
import love.forte.simbot.serialization.json.JsonSerializerFactory
import java.io.Closeable



private val jsonContentType = ContentType.parse("application/json")



private class JsonContentConverter(private val fac: JsonSerializerFactory) : ContentConverter {

    override suspend fun convertForReceive(
        context: PipelineContext<ApplicationReceiveRequest, ApplicationCall>
    ): Any? {
        val channel = context.subject.value as ByteReadChannel
        val message = channel.readUTF8Line() ?: "{}"
        return fac.getJsonSerializer(context.subject.type.java).fromJson(message)
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
    // private val msgParser: Map<String, (Map<String, *>) -> BaseLovelyCatMsg>,
    applicationEngineFactory: ApplicationEngineFactory<ApplicationEngine, out ApplicationEngine.Configuration>,
    jsonSerializerFactory: JsonSerializerFactory,
    port: Int,
    path: String
) : LovelyCatHttpServer {

    init {
        Runtime.getRuntime().addShutdownHook(Thread {
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

            routing {
                // listen path.
                post(path) {
                    // TODO
                    val params = call.receive<Map<String, *>>()
                    println(params)
                    val eventType = params["Event"]?.toString()

                    if (eventType == null) {
                        // 404. no event.
                        call.response.status(HttpStatusCode.NotFound)
                        call.respondText { "param 'Event' not found: is Empty." }
                    } else {
                        // ok status.
                        call.response.status(HttpStatusCode.OK)

                    }

                }
            }
        }
    }



    override fun start() {
        server.start()
    }

    /**
     * close server.
     */
    override fun close() {
        server.stop(5000, 5000)
    }

}



private data class ResponseData(
    private val message: String
)








