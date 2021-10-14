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
// import love.forte.simbot.kaiheila.api.doRequest
// import love.forte.simbot.kaiheila.api.v3.V3
// import love.forte.simbot.kaiheila.api.v3.guild.GuildNicknameReq
// import love.test.GatewayApiConstant
// import love.test.client
// import kotlin.test.Test
//
//
// /**
//  *
//  * @author ForteScarlet
//  */
// class GuildNicknameTest {
//
//
//     @Test
//     fun nicknameTest() = runBlocking {
//         val guild = GuildApiTest().guildList().items[0]
//
//         val result = GuildNicknameReq(
//             guildId = guild.id,
//             nickname = "天下第一可爱法欧莉"
//         )
//             .doRequest(V3, client, GatewayApiConstant.token)
//
//         println(result)
//
//     }
//
// }