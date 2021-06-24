/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  simpler-robot
 *  * File     GuildViewTest.kt
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

package love.test

import kotlinx.coroutines.runBlocking
import kotlinx.serialization.decodeFromString
import love.forte.simbot.component.kaiheila.api.ObjectResp
import love.forte.simbot.component.kaiheila.api.doRequestForData
import love.forte.simbot.component.kaiheila.api.v3.V3
import love.forte.simbot.component.kaiheila.api.v3.guild.GuildView
import love.forte.simbot.component.kaiheila.api.v3.guild.GuildViewReq
import love.forte.simbot.component.kaiheila.khlJson
import kotlin.test.Test


/**
 *
 * @author ForteScarlet
 */
class GuildViewTest {


    @Test
    fun guildViewTest() = runBlocking {
        // 文字频道: 7566099004366572
        val id = "6865507942900765"

        val guildView = GuildViewReq(id).doRequestForData(V3, client, GatewayApiConstant.token)!!

        println(guildView)

        println("==========================")

        for (role in guildView.roles) {
            println("role: $role")
        }

        println("==========================")
        for (channel in guildView.channels) {
            println("channel: $channel")
        }
    }


    @Test
    fun jsonParseTest() {
        val json = "{\"code\":0,\"message\":\"操作成功\",\"data\":{\"id\":\"6865507942900765\",\"name\":\"空羽龙月的方舟课堂\",\"topic\":\"\",\"master_id\":\"886154643\",\"is_master\":false,\"icon\":\"https://img.kaiheila.cn/icons/2021-06/0oDNdfaNqK06j06j.png/icon\",\"notify_type\":2,\"region\":\"beijing\",\"enable_open\":false,\"open_id\":\"0\",\"default_channel_id\":\"7566099004366572\",\"welcome_channel_id\":\"0\",\"features\":[],\"roles\":[{\"role_id\":81892,\"name\":\"管理员\",\"color\":0,\"position\":1,\"hoist\":0,\"mentionable\":0,\"permissions\":134217729,\"type\":0},{\"role_id\":205748,\"name\":\"法欧莉斯卡雷特\",\"color\":0,\"position\":2,\"hoist\":0,\"mentionable\":0,\"permissions\":268435390,\"type\":1},{\"role_id\":0,\"name\":\"@全体成员\",\"color\":0,\"position\":999,\"hoist\":0,\"mentionable\":0,\"permissions\":148691464,\"type\":255}],\"channels\":[{\"id\":\"7566099004366572\",\"master_id\":\"886154643\",\"parent_id\":\"3457936871358649\",\"name\":\"文字频道\",\"type\":1,\"level\":100,\"limit_amount\":0,\"is_category\":false,\"permission_sync\":0,\"permission_overwrites\":[{\"role_id\":0,\"allow\":0,\"deny\":0}],\"permission_users\":[]},{\"id\":\"9773712738545776\",\"master_id\":\"886154643\",\"parent_id\":\"6133006506915292\",\"name\":\"永不下班控制中枢\",\"type\":2,\"level\":100,\"limit_amount\":50,\"is_category\":false,\"permission_sync\":0,\"permission_overwrites\":[{\"role_id\":0,\"allow\":0,\"deny\":0}],\"permission_users\":[]},{\"id\":\"5745128252968190\",\"master_id\":\"886154643\",\"parent_id\":\"6133006506915292\",\"name\":\"音游房，音量警告\",\"type\":2,\"level\":100,\"limit_amount\":50,\"is_category\":false,\"permission_sync\":0,\"permission_overwrites\":[{\"role_id\":0,\"allow\":0,\"deny\":0}],\"permission_users\":[]},{\"id\":\"3457936871358649\",\"master_id\":\"886154643\",\"parent_id\":\"\",\"name\":\"文字分组\",\"type\":0,\"level\":100,\"limit_amount\":0,\"is_category\":true,\"permission_sync\":0,\"permission_overwrites\":[{\"role_id\":0,\"allow\":0,\"deny\":0}],\"permission_users\":[]},{\"id\":\"6133006506915292\",\"master_id\":\"886154643\",\"parent_id\":\"\",\"name\":\"语音分组\",\"type\":0,\"level\":100,\"limit_amount\":0,\"is_category\":true,\"permission_sync\":0,\"permission_overwrites\":[{\"role_id\":0,\"allow\":0,\"deny\":0}],\"permission_users\":[]}],\"user_config\":{\"notify_type\":null,\"nickname\":\"法欧莉斯卡雷特\",\"role_ids\":[205748],\"chat_setting\":1,\"security_limit\":null}}}\n"

        println(khlJson.decodeFromString<ObjectResp<GuildView>>(json))

    }

}