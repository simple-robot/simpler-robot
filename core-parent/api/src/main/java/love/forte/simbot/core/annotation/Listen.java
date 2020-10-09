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


import love.forte.simbot.core.api.message.*;
import love.forte.simbot.core.api.message.containers.*;
import love.forte.simbot.core.api.message.events.*;

import java.lang.annotation.*;

/**
 * <p>监听的类型。一般来讲是一个接口类型。</p>
 *
 * <p> 可复数注解同样支持注解继承。但是仅允许在 直接获取 {@link Listen} 获取不到的时候才会进行深度获取。 </p>
 *
 * <p>与具体对应的消息相关的有：</p>
 *
 * <ul>
 *     <li>{@link PrivateMsg} 私聊消息。</li>
 *     <li>{@link GroupMsg} 群消息</li>
 *
 *     <li>{@link FriendIncrease} 好友增加.</li>
 *     <li>{@link GroupMemberIncrease} 群友增加。</li>
 *
 *     <li>{@link GroupMemberPermissionChanged} 群成员权限变动事件。</li>
 *     <li>{@link GroupNameChanged} 群名称变动事件。</li>
 *     <li>{@link GroupMemberRemarkChanged} 群友群名片变动事件。</li>
 *     <li>{@link GroupMemberSpecialChanged} 群友头衔变动事件。</li>
 *     <li>{@link FriendNicknameChanged} 好友昵称变动事件</li>
 *     <li>{@link FriendAvatarChanged} 好友头像变动事件</li>
 *
 *     <li>{@link PrivateMsgRecall} 私聊消息撤回。</li>
 *     <li>{@link GroupMsgRecall} 群聊消息撤回。</li>
 *
 *     <li>{@link FriendReduce} 好友减少事件</li>
 *     <li>{@link GroupReduce} 群友减少事件。</li>
 *
 *     <li>{@link FriendAddRequest} 好友请求事件。 此时申请人尚未成为好友。</li>
 *     <li>{@link GroupAddRequest} 群添加请求。此时申请人尚未入群。</li>
 * </ul>
 *
 * <p>这些监听类型是应该最优先被考虑使用的，也是最被推荐使用的。</p>
 * <p>除了这些消息类型，它们大多数也存在一些父类型，来代表一个大范围内的消息类型。</p>
 *
 * <ul>
 *     <li>{@link MsgGet} 所有消息类型的父接口。</li>
 *     <li>{@link EventGet} 事件类型的父接口。</li>
 *     <li>{@link MessageEventGet} 与消息有关的事件父接口。</li>
 *     <li>{@link MessageRecallEventGet} 与消息撤回有关的事件父接口。</li>
 *     <li>{@link MemberChangesEventGet} 成员变动事件父接口。</li>
 *     <li>{@link IncreaseEventGet} 与增加有关的事件父接口。</li>
 *     <li>{@link ReduceEventGet} 与减少有关的事件父接口。</li>
 *     <li>{@link RequestGet} 与请求相关的父接口。</li>
 *     <li>{@link ChangedGet} 出现变化的事件相关的父接口。 </li>
 * </ul>
 *
 * <p>上述接口并不被优先推荐使用，因为它们可能代表的类型很多，且内容并不全面。</p>
 *
 * <p>除了消息类型的接口以外，你也可以将他们作为一种 <b>容器 (Container) </b>: </p>
 *
 * <ul>
 *     <li>{@link Container} 所有容器的父接口。</li>
 *     <li>{@link OriginalDataContainer} 原始信息容器。</li>
 *     <li>{@link PermissionContainer} 权限容器。</li>
 *     <li>{@link FlagContainer} 标识容器。</li>
 *     <li>{@link ActionMotivationContainer} 行动动机容器。</li>
 *
 *     <li>{@link BotContainer} bot基础信息容器。</li>
 *     <li>{@link AccountContainer} 用户基础信息容器。</li>
 *     <li>{@link GroupContainer} 群信息容器。</li>
 *     <li>{@link OperatorContainer} 操作者信息容器。</li>
 *     <li>{@link BeOperatorContainer} 被操作者信息容器。</li>
 *     <li>{@link OperatingContainer} 操作相关信息容器。</li>
 * </ul>
 *
 * <p>上述<b>容器</b>相关的类型是<b>不被推荐</b>用来作为<b>监听类型</b>的。</p>
 *
 *
 *
 * @see PrivateMsg 私聊消息。
 * @see GroupMsg 群消息
 *
 * @see FriendIncrease 好友增加.
 * @see GroupMemberIncrease 群友增加。
 *
 * @see GroupMemberPermissionChanged 群成员权限变动事件。
 * @see GroupNameChanged 群名称变动事件。
 * @see GroupMemberRemarkChanged 群友群名片变动事件。
 * @see GroupMemberSpecialChanged 群友头衔变动事件。
 * @see FriendNicknameChanged 好友昵称变动事件
 * @see FriendAvatarChanged 好友头像变动事件
 *
 * @see PrivateMsgRecall 私聊消息撤回。
 * @see GroupMsgRecall 群聊消息撤回。
 *
 * @see FriendReduce  好友减少事件
 * @see GroupReduce 群友减少事件。
 *
 * @see FriendAddRequest 好友请求事件。
 * @see GroupAddRequest 群添加请求。
 *
 * <!-- ------------------------------------------------------- -->
 *
 * @see MsgGet 所有消息类型的父接口。
 * @see EventGet 事件类型的父接口。
 * @see MessageEventGet 与消息有关的事件父接口。
 * @see MessageRecallEventGet 与消息撤回有关的事件父接口。
 * @see MemberChangesEventGet 成员变动事件父接口。
 * @see IncreaseEventGet 与增加有关的事件父接口。
 * @see ReduceEventGet 与减少有关的事件父接口。
 * @see RequestGet 与请求相关的父接口。
 * @see ChangedGet 出现变化的事件相关的父接口。
 *
 * <!-- ------------------------------------------------------- -->
 *
 * @see Container 所有容器的父接口。
 * @see OriginalDataContainer 原始信息容器。
 * @see PermissionContainer 权限容器。
 * @see FlagContainer 标识容器。
 * @see ActionMotivationContainer 行动动机容器。
 * @see BotContainer bot基础信息容器。
 * @see AccountContainer 用户基础信息容器。
 * @see GroupContainer 群信息容器。
 * @see OperatorContainer 操作者信息容器。
 * @see BeOperatorContainer 被操作者信息容器。
 * @see OperatingContainer 操作相关信息容器。
 *
 *
 * @author <a href="https://github.com/ForteScarlet"> ForteScarlet </a>
 */
// @Retention(RetentionPolicy.RUNTIME)
// @Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
// @Documented
@Repeatable(Listens.class)
public @interface Listen {

    /**
     * 具体的监听类型。
     */
    Class<? extends MsgGet> value();


}
