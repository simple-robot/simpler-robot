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
package love.forte.simbot.component.xiaolz

import io.ktor.application.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.util.pipeline.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import love.forte.simbot.component.xiaolz.utils.toParamMap
import love.forte.simbot.serialization.json.JsonSerializer
import love.forte.simbot.serialization.json.JsonSerializerFactory
import love.forte.simbot.serialization.json.fastjson.FastJsonSerializerFactory
import java.io.InputStream
import java.nio.charset.Charset

val jsonContentType = ContentType.parse("application/json")

class JsonContentConverter(private val fac: JsonSerializerFactory) : ContentConverter {
    override suspend fun convertForReceive(context: PipelineContext<ApplicationReceiveRequest, ApplicationCall>): Any? {
        println(context.subject.type)
        return null
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

fun main() {

    val fac = FastJsonSerializerFactory()

    val client = HttpClient()

    val server = embeddedServer(Netty, 8080) {
        install(ContentNegotiation) {
            register(jsonContentType, JsonContentConverter(fac))
        }


        routing {
            post("/oops") {
                // read text.
                val request = withContext(Dispatchers.IO) {
                    call.receive<InputStream>().use {
                        it.reader(Charset.forName("GB2312")).readText()
                    }
                }

                val pMap = request.toParamMap()

                /*

                self_id: 框架QQ(长整数型)
                to_id: 好友QQ(长整数型)
                message: 发送内容(文本型)
                message_random: 消息Random(长整数型,可空,撤回消息用)
                message_req: 消息Req(整数型,可空,撤回消息用)
                 */
                val p = mapOf(
                    "self_id" to pMap["selfId"],
                    "to_id" to "1149159218",
                    "message" to pMap["eventFromNickname"]
                )

                val json = fac.getJsonSerializer(Map::class.java).toJson(p)

                println("json: $json")

                val resp = client.post<HttpResponse>(Url("http://127.0.0.1:10101/发送消息")) {
                    body = json
                    contentType(jsonContentType)
                }

                println("resp: $resp")
                println("resp.status: ${resp.status}")
                val respText = withContext(Dispatchers.IO) {
                    resp.receive<InputStream>().use {
                        it.reader(Charset.forName("GB2312")).readText()
                    }
                }
                println("respText: $respText")

                call.respond(HttpStatusCode.OK)
            }

        }
    }


    server.start(wait = true)
}
