/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     Listen.java
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.simbot.core.annotation;

import love.forte.simbot.core.api.message.MsgGet;

import java.lang.annotation.*;

/**
 * @author <a href="https://github.com/ForteScarlet"> ForteScarlet </a>
 */
@SuppressWarnings("unused")
@Retention(RetentionPolicy.RUNTIME)    //注解会在class字节码文件中存在，在运行时可以通过反射获取到
@Target({ElementType.TYPE}) //接口、类、枚举、注解、方法
@Documented
public @interface Listen {

    /**
     * 监听的类型。一般来讲是一个接口类型。
     *
     * @see love.forte.simbot.core.api.message.events.PrivateMsg
     * @see love.forte.simbot.core.api.message.events.GroupMsg
     *
     * @see love.forte.simbot.core.api.message.events.FriendIncrease
     * @see love.forte.simbot.core.api.message.events.GroupMemberIncrease
     *
     * @see love.forte.simbot.core.api.message.events.GroupMemberPermissionChanged
     * @see love.forte.simbot.core.api.message.events.GroupNameChanged
     * @see love.forte.simbot.core.api.message.events.GroupMemberRemarkChanged
     * @see love.forte.simbot.core.api.message.events.GroupMemberSpecialChanged
     * @see love.forte.simbot.core.api.message.events.FriendNicknameChanged
     * @see love.forte.simbot.core.api.message.events.FriendAvatarChanged
     *
     * @see love.forte.simbot.core.api.message.events.PrivateMsgRecall 私聊消息撤回。
     * @see love.forte.simbot.core.api.message.events.GroupMsgRecall 群聊消息撤回。
     *
     * @see love.forte.simbot.core.api.message.events.FriendReduce
     * @see love.forte.simbot.core.api.message.events.GroupReduce
     *
     * @see love.forte.simbot.core.api.message.events.FriendAddRequest
     * @see love.forte.simbot.core.api.message.events.GroupAddRequest 群添加请求。此时申请人尚未入群。
     *
     */
    Class<? extends MsgGet> value();


}
