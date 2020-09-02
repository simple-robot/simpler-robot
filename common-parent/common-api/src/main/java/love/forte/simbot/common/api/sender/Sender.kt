/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  simple-robot-S
 *  * File     Sender.kt
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

package love.forte.simbot.common.api.sender

/**
 *
 * 消息发送用的送信器。一般用来发送消息，例如 私聊、群聊等
 *
 * @author ForteScarlet <ForteScarlet@163.com>
 * @date 2020/9/2
 * @since
 */
public interface Sender {

    fun sendGroupMsg()

    fun sendPrivateMsg()


    /**
     * 发布群公告
     * 目前，top、toNewMember、confirm参数是无效的
     * @param group 群号
     * @param title 标题
     * @param text   正文
     * @param top    是否置顶，默认false
     * @param toNewMember 是否发给新成员 默认false
     * @param confirm 是否需要确认 默认false
     * @return 是否发布成功
     */
    fun sendGroupNotice(
        group: String,
        title: String?,
        text: String?,
        top: Boolean,
        toNewMember: Boolean,
        confirm: Boolean
    ): Boolean


}