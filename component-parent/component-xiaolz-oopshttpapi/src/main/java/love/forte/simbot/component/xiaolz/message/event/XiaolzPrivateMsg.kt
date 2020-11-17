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

package love.forte.simbot.component.xiaolz.message.event

/**
 * 小栗子私聊消息
 */
public data class XiaolzPrivateMsg(
    val selfId: String,

)

/*
私聊消息
self_id: 框架QQ
from_id: 发送人QQ
group_id: 消息群号
message: 消息内容
message_req: 消息Req
message_seq: 消息Seq
message_random: 消息Random
message_hand: 数据指针
message_get_timestamp: 消息接收时间
message_send_timestamp: 消息发送时间
message_cut: 消息分片序列
message_cut_num: 消息分片数量
message_cut_iden: 消息分片标识
message_type: 消息类型
message_subtype: 消息子类型
message_temp_subtype: 消息子临时类型
post_type: 上报消息类型(判断私聊/群聊/事件)
bubble_id: 气泡Id
redpack_type: 红包类型
inter_token: 会话token
event_from_id: 事件来源QQ
event_from_nickname: 事件来源QQ昵称
file_id: 文件Id
file_md5: 文件Md5
filename: 文件名
file_size: 文件大小
access_token: 用来判断通信权限
 */