/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     ListenTemplate.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.simbot.core.annotation

import love.forte.common.utils.annotation.AnnotateMapping
import love.forte.simbot.core.api.message.events.*

/*
    注：模板注解与原本的
 */

/**
 * 监听私聊消息。
 *
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.ANNOTATION_CLASS)
@MustBeDocumented
@Listen(PrivateMsg::class)
@AnnotateMapping(value = Listen::class)
public annotation class OnPrivate


/**
 * 监听群消息。
 *
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.ANNOTATION_CLASS)
@MustBeDocumented
@Listen(GroupMsg::class)
@AnnotateMapping(value = Listen::class)
public annotation class OnGroup(val d: Int = 5)


/**
 * 监听群成员权限变动事件。
 *
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.ANNOTATION_CLASS)
@MustBeDocumented
@Listen(GroupMemberPermissionChanged::class)
@AnnotateMapping(value = Listen::class)
public annotation class OnGroupMemberPermissionChanged

/**
 * 监听群名称变动事件。
 *
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.ANNOTATION_CLASS)
@MustBeDocumented
@Listen(GroupNameChanged::class)
@AnnotateMapping(value = Listen::class)
public annotation class OnGroupNameChanged

/**
 * 监听群友群名片变动事件。
 *
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.ANNOTATION_CLASS)
@MustBeDocumented
@Listen(GroupMemberRemarkChanged::class)
@AnnotateMapping(value = Listen::class)
public annotation class OnGroupMemberRemarkChanged

/**
 * 监听群友头衔变动事件。
 *
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.ANNOTATION_CLASS)
@MustBeDocumented
@Listen(GroupMemberSpecialChanged::class)
@AnnotateMapping(value = Listen::class)
public annotation class OnGroupMemberSpecialChanged

/**
 * 监听好友昵称变动事件。
 *
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.ANNOTATION_CLASS)
@MustBeDocumented
@Listen(FriendNicknameChanged::class)
@AnnotateMapping(value = Listen::class)
public annotation class OnFriendNicknameChanged

/**
 * 监听好友头像变动事件。
 *
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.ANNOTATION_CLASS)
@MustBeDocumented
@Listen(FriendAvatarChanged::class)
@AnnotateMapping(value = Listen::class)
public annotation class OnFriendAvatarChanged

/**
 * 监听好友增加事件。
 *
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.ANNOTATION_CLASS)
@MustBeDocumented
@Listen(FriendIncrease::class)
@AnnotateMapping(value = Listen::class)
public annotation class OnFriendIncrease

/**
 * 监听群友增加事件。
 *
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.ANNOTATION_CLASS)
@MustBeDocumented
@Listen(GroupMemberIncrease::class)
@AnnotateMapping(value = Listen::class)
public annotation class OnGroupMemberIncrease

/**
 * 监听私聊消息撤回事件。
 *
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.ANNOTATION_CLASS)
@MustBeDocumented
@Listen(PrivateMsgRecall::class)
@AnnotateMapping(value = Listen::class)
public annotation class OnPrivateMsgRecall

/**
 * 监听群聊消息撤回事件。
 *
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.ANNOTATION_CLASS)
@MustBeDocumented
@Listen(GroupMsgRecall::class)
@AnnotateMapping(value = Listen::class)
public annotation class OnGroupMsgRecall

/**
 * 监听群友减少事件。
 *
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.ANNOTATION_CLASS)
@MustBeDocumented
@Listen(GroupReduce::class)
@AnnotateMapping(value = Listen::class)
public annotation class OnGroupReduce

/**
 * 监听好友减少事件。
 *
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.ANNOTATION_CLASS)
@MustBeDocumented
@Listen(FriendReduce::class)
@AnnotateMapping(value = Listen::class)
public annotation class OnFriendReduce


/**
 * 监听好友请求事件。
 *
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.ANNOTATION_CLASS)
@MustBeDocumented
@Listen(FriendAddRequest::class)
@AnnotateMapping(value = Listen::class)
public annotation class OnFriendAddRequest


/**
 * 监听群添加请求事件。
 *
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.ANNOTATION_CLASS)
@MustBeDocumented
@Listen(GroupAddRequest::class)
@AnnotateMapping(value = Listen::class)
public annotation class OnGroupAddRequest

