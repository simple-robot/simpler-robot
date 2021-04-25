/*
 *
 *  * Copyright (c) 2021. ForteScarlet All rights reserved.
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

package love.forte.simbot.component.kaiheila.`object`

import love.forte.simbot.api.message.containers.GroupInfo


/**
 *
 * 开黑啦objects - [服务器Guild](https://developer.kaiheila.cn/doc/objects#%E6%9C%8D%E5%8A%A1%E5%99%A8Guild)
 *
 * 类似于一个 **群信息**。
 *
 * 官方数据结构示例：
 * ```json
 * {
 *   "id": "2405000000000",
 *   "name": "工具",
 *   "topic": "",
 *   "master_id": "9200000000",
 *   "icon": "",
 *   "notify_type": 1,
 *   "region": "beijing",
 *   "enable_open": false,
 *   "open_id": "0",
 *   "default_channel_id": "2369000000000",
 *   "welcome_channel_id": "0",
 *   "roles": [
 *       {
 *           "role_id": 109472,
 *           "name": "管理员",
 *           "color": 0,
 *           "position": 1,
 *           "hoist": 0,
 *           "mentionable": 0,
 *           "permissions": 1
 *       }
 *   ],
 *   "channels": [
 *       {
 *           "id": "2369000000000",
 *           "master_id": "9200000000",
 *           "parent_id": "",
 *           "name": "你好",
 *           "type": 1,
 *           "level": 1,
 *           "limit_amount": 0,
 *           "is_category": false
 *       },
 *   ]
 * }
 * ```
 *
 *
 * @author ForteScarlet
 */
interface KaiheilaGuild : KaiheilaObjects, GroupInfo {



}