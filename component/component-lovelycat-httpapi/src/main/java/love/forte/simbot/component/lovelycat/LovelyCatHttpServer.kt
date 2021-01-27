/*
 *
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  simple-robot-S
 * File     LovelyCatHttpServer.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 *
 *
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
import love.forte.simbot.listener.ListenResult
import love.forte.simbot.listener.MsgGetProcessor
import love.forte.simbot.listener.onMsg
import love.forte.simbot.serialization.json.JsonSerializer
import love.forte.simbot.serialization.json.JsonSerializerFactory
import java.io.Closeable
import java.net.InetAddress
import java.time.LocalDateTime
import kotlin.concurrent.thread
import kotlin.reflect.jvm.jvmErasure


private val jsonContentType = ContentType.parse("application/json")
private val htmlContentType = ContentType.parse("text/html")

/**
 * maybe not use.
 */
private class JsonContentConverter(private val fac: JsonSerializerFactory) : ContentConverter {

    override suspend fun convertForReceive(
        context: PipelineContext<ApplicationReceiveRequest, ApplicationCall>,
    ): Any? {
        val channel = context.subject.value as ByteReadChannel
        val message = StringBuilder().apply {
            var readLine: Boolean
            do {
                readLine = channel.readUTF8LineTo(this)
            } while (readLine)
            channel.cancel()
        }.toString()
        return fac.getJsonSerializer(context.subject.typeInfo.jvmErasure.java).fromJson(message)
    }

    override suspend fun convertForSend(
        context: PipelineContext<Any, ApplicationCall>,
        contentType: ContentType,
        value: Any,
    ): Any? {
        val jsonSerializer: JsonSerializer<Any> = fac.getJsonSerializer(context.subject.javaClass)
        return jsonSerializer.toJson(context.subject)
    }


}


interface LovelyCatHttpServer : Closeable {
    @Throws(Exception::class)
    fun start()
}


/**
 * 可爱猫事件监听http server。
 */
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

    private var startedTime: LocalDateTime? = null
        set(value) {
            if (value != null) {
                showInfo =
                    """
                    <!DOCTYPE html>
                    <html lang="zh-CN">
                    <head>
                    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@3.3.7/dist/css/bootstrap.min.css" integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u" crossorigin="anonymous">
                    </head>
                    <body>
                    <div class='row'>
                          <div class="col-md-2  col-sm-0" ></div>
                          <div class="col-md-8 col-sm-12">
                              <div class="page-header">
                                <h1>Lovely cat http server <small>by <a href='https://github.com/ForteScarlet/simpler-robot'>simbot</a></small></h1>
                              </div>
                              <div class="jumbotron">
                                  <div class="container">
                                    <h1>Lovely cat server enabled!</h1>
                                    <p>Started time: $value.</p>
                                  </div>
                              </div>
                          </div>
                          <div class="col-md-2  col-sm-0" ></div>
                    </div>
                    </body>
                    </html>
                """.trimIndent()
            }
            field = value
        }

    private var showInfo: String = """
                    <!DOCTYPE html>
                    <html lang="zh-CN">
                    <head>
                    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@3.3.7/dist/css/bootstrap.min.css" integrity="sha384-BVYiiSIFeK1dGmJRAkycuHAHRg32OmUcww7on3RYdg4Va+PmSTsz/K68vbdEjh4u" crossorigin="anonymous">
                    </head>
                    <body>
                    <div class='row'>
                          <div class="col-md-2  col-sm-0" ></div>
                          <div class="col-md-8 col-sm-12">
                              <div class="page-header">
                                <h1>Lovely cat http server <small>by <a href='https://github.com/ForteScarlet/simpler-robot'>simbot</a></small></h1>
                              </div>
                              <div class="jumbotron">
                                <div class="container">
                                    <h1>Lovely cat server enabled?</h1>
                                </div>
                              </div>
                          </div>
                          <div class="col-md-2  col-sm-0" ></div>
                    </div>
                    </body>
                    </html>
                """.trimIndent()

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

            if (lovelyCatServerProperties.cors) {
                install(CORS)
            }

            routing {
                // listen path.
                post(path) {
                    try {
                        val originalData = call.receive<String>()

                        val params = mapSerializer.fromJson(originalData)

                        val eventType = params["Event"]?.toString()

                        logger.debug("On event request. type: $eventType, originalData: $originalData")

                        if (eventType == null) {
                            // 404. no event.
                            call.respond(HttpStatusCode.NotFound, message = "Param 'Event' not found: Event is Empty.")
                        } else {

                            val botId = (params["robot_wxid"] ?: params["rob_wxid"])?.toString()
                                ?: throw NoSuchBotException("No param 'robot_wxid' or 'rob_wxid' in lovelycat request param.")

                            val api = apiManager[botId]
                                ?: throw IllegalStateException("Cannot found Bot($botId)'s api template.")

                            try {
                                // val parse =
                                // if (parse != null) {
                                lovelyCatParser.type(eventType).let { t ->
                                    if (t != null) {
                                        msgGetProcessor.onMsg(t) {
                                            lovelyCatParser.parse(eventType,
                                                originalData,
                                                api,
                                                jsonSerializerFactory,
                                                params)
                                        }
                                    } else {
                                        val msg = lovelyCatParser.parse(eventType,
                                            originalData,
                                            api,
                                            jsonSerializerFactory,
                                            params)
                                        msg?.let { m -> msgGetProcessor.onMsg(m::class.java) { m } ?: ListenResult }
                                    }

                                }?.let {
                                    // ok
                                    call.respond(HttpStatusCode.OK, message = it.result ?: "{}")
                                } ?: kotlin.run {
                                    val respMsg = "Cannot found any event type for event '$eventType'."
                                    call.respond(HttpStatusCode.NotFound, message = respMsg)
                                    logger.warn("$respMsg response 404.")
                                }
                                // }
                                // ok status.
                                // call.respond(HttpStatusCode.OK)
                            } catch (e: Exception) {
                                call.respond(HttpStatusCode.InternalServerError, message = e.toString())
                                logger.error("Parse event instance failed by originalData: $originalData", e)
                            }

                        }
                    } catch (ex: Exception) {
                        call.respond(HttpStatusCode.InternalServerError, message = ex.toString())
                        logger.error("Internal server error.", ex)
                    }
                }

                get("/simbot/lovelyCat") {
                    call.respondText(htmlContentType) { showInfo }

                }


            }
        }
    }


    override fun start() {
        server.start()
        startedTime = LocalDateTime.now()
        try {
            val localHost = InetAddress.getLocalHost()
            val address = localHost.hostAddress
            logger.info("Lovelycat ktor server started on http://$address:$port$path")
            logger.info("You can try visit http://$address:$port/simbot/lovelyCat for test.")
        } catch (e: Exception) {
            logger.info("Lovelycat ktor server started on http://<IP>:$port$path")
        }

    }

    /**
     * close server.
     */
    override fun close() {
        server.stop(5000, 5000)
    }

}








