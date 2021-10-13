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
// import love.forte.simbot.kaiheila.api.v3.guild.GuildUserList
// import love.forte.simbot.kaiheila.api.v3.guild.guildUserListReq
// import love.test.GatewayApiConstant
// import love.test.client
// import kotlin.test.Test
//
//
// class GuildUserListTest {
//
//     suspend fun userList(): GuildUserList {
//
//         val gid = GuildApiTest().guildList().items[0].also {
//             println("Item: $it")
//         }.id
//
//         return guildUserListReq {
//             guildId = gid
//         }.doRequest(
//             V3, client, GatewayApiConstant.token
//         ).data!!
//     }
//
//     @Test
//     fun userListTest() = runBlocking {
//         val list = userList()
//         println(list)
//         println(list.userCount)
//
//     }
//
// }