// /*
//  *
//  *  * Copyright (c) 2021. ForteScarlet All rights reserved.
//  *  * Project  simple-robot
//  *  * File     MiraiAvatar.kt
//  *  *
//  *  * You can contact the author through the following channels:
//  *  * github https://github.com/ForteScarlet
//  *  * gitee  https://gitee.com/ForteScarlet
//  *  * email  ForteScarlet@163.com
//  *  * QQ     1149159218
//  *
//  */
//
// package love.test.channel
//
// import kotlinx.coroutines.runBlocking
// import kotlinx.serialization.decodeFromString
// import love.forte.simbot.component.kaiheila.api.doRequest
// import love.forte.simbot.component.kaiheila.api.v3.V3
// import love.forte.simbot.component.kaiheila.api.v3.channel.ChannelDeleteReq
// import love.forte.simbot.component.kaiheila.api.v3.channel.ChannelView
// import love.forte.simbot.component.kaiheila.api.v3.channel.channelCreateReq
// import love.forte.simbot.component.kaiheila.khlJson
// import kotlin.test.Test
//
//
// /**
//  *
//  * @author ForteScarlet
//  */
// class ChannelCreateTest {
//
//     private val cj1 = "{\"id\":\"2803538284275176\",\"guild_id\":\"6865507942900765\",\"master_id\":\"248428728\",\"parent_id\":\"\",\"name\":\"法欧莉粉丝频道\",\"topic\":\"\",\"type\":1,\"level\":0,\"slow_mode\":0,\"limit_amount\":0,\"voice_quality\":2,\"is_category\":false,\"server_url\":\"bj-vs05.kaiheila.cn:443\",\"permission_sync\":0,\"permission_overwrites\":[{\"role_id\":0,\"allow\":0,\"deny\":0}],\"permission_users\":[]}"
//     val c1: ChannelView by lazy { khlJson.decodeFromString(cj1) }
//
//     private val cj2 = "{\"id\":\"2403788152589048\",\"guild_id\":\"6865507942900765\",\"master_id\":\"248428728\",\"parent_id\":\"\",\"name\":\"法欧莉粉丝频道2\",\"topic\":\"\",\"type\":1,\"level\":0,\"slow_mode\":0,\"limit_amount\":0,\"voice_quality\":2,\"is_category\":false,\"server_url\":\"bj-vs05.kaiheila.cn:443\",\"permission_sync\":0,\"permission_overwrites\":[{\"role_id\":0,\"allow\":0,\"deny\":0}],\"permission_users\":[]}"
//     val c2: ChannelView by lazy { khlJson.decodeFromString(cj2) }
//
//     @Test
//     fun createTest() = runBlocking {
//         val view = channelCreateReq {
//             guildId = "" // GUILD_ID
//             name = "法欧莉粉丝频道2"
//             textType()
//         }.doRequest(V3, client, GatewayApiConstant.token).data!!
//
//
//         println(view)
//     }
//
//     @Test
//     fun deleteTest(): Unit = runBlocking {
//         ChannelDeleteReq(c2.id).doRequest(
//             V3, client, GatewayApiConstant.token
//         ).also { println(it) }
//     }
//
// }