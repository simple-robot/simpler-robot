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
// import love.forte.simbot.kaiheila.api.v3.guild.GuildMuteListReq
// import love.test.GUILD_ID
// import love.test.GatewayApiConstant
// import love.test.client
// import kotlin.test.Test
//
//
// /**
//  *
//  * @author ForteScarlet
//  */
// class GuildMuteListTest {
//
//     @Test
//     fun guildMuteListTestByDetail() = runBlocking {
//         val muteList = GuildMuteListReq.Detail(GUILD_ID)
//             .doRequest(V3, client, GatewayApiConstant.token).data!!
//
//         println(muteList)
//     }
//
//     @Test
//     @Suppress("DEPRECATION")
//     fun guildMuteListTestBySimple() = runBlocking {
//         val muteList = GuildMuteListReq.Simple(GUILD_ID)
//             .doRequest(V3, client, GatewayApiConstant.token).data!!
//
//         println(muteList)
//     }
//
// }