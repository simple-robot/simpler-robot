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
// package love.test.guild
//
// import kotlinx.coroutines.runBlocking
// import love.forte.simbot.kaiheila.api.doRequestForData
// import love.forte.simbot.kaiheila.api.v3.V3
// import love.forte.simbot.kaiheila.api.v3.guild.GuildViewReq
// import love.test.GatewayApiConstant
// import love.test.client
// import kotlin.test.Test
//
//
// /**
//  *
//  * @author ForteScarlet
//  */
// class GuildViewTest {
//
//
//     @Test
//     fun guildViewTest() = runBlocking {
//         // 文字频道: 7566099004366572
//         val id = "6865507942900765"
//
//         val guildView = GuildViewReq(id).doRequestForData(V3, client, GatewayApiConstant.token)!!
//
//         println(guildView)
//
//         println("==========================")
//
//         for (role in guildView.roles) {
//             println("role: $role")
//         }
//
//         println("==========================")
//         for (channel in guildView.channels) {
//             println("channel: $channel")
//         }
//     }
//
//
//
// }