/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     MsgGetClans.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.simbot.common.annotation

import love.forte.simbot.common.api.message.MessageEventGet
import love.forte.simbot.common.api.message.MsgGet
import love.forte.simbot.common.api.message.containers.FlagContainer
import love.forte.simbot.common.api.message.events.PrivateMsg
import kotlin.annotation.AnnotationTarget.CLASS

/*
 *
 * msg get clans(氏族)
 *
 * 此模块定义一些用于标记的注解,
 * 标注那些是用作**主要事件**监听而存在的,
 * 而哪些是作为**事件父类**而存在的。
 *
 *
 * 这些注解仅用于标记与提醒
 *
 * @author ForteScarlet <ForteScarlet@163.com>
 * @date 2020/9/1
 * @since
 */

/**
 * ### **主要监听事件类型** 标记。
 *
 *
 * [MainListenerType] 是一个 **主要监听事件类型** 标记。 一般标注一个 [MsgGet] 的具体子接口。
 * 例如 [PrivateMsg]。
 *
 * #### 应优先被使用
 *
 * 被标注的接口代表了这个接口是主要用于监听的接口。
 * 以 [PrivateMsg] 举例来说，[PrivateMsg] 继承了 [MessageEventGet] 接口，
 * 而 [MessageEventGet] 又继承了 [MsgGet] 与 [FlagContainer] 。
 *
 * 在这其中，如果你要监听一个事件, 那么**最优先**推荐监听最下游事件 [PrivateMsg] 类型而不是他的任何父接口
 *
 * #### 继承 [MsgGet]
 * 所有的 **父类监听事件类型**与 **主要监听事件类型** 都应该是继承自 [MsgGet] 的接口类型。
 *
 * @see MsgGet
 * @see ParentListenerType
 *
 *
 * @property comment String 额外说明或描述
 */
@Target(allowedTargets = [CLASS])
@Retention(AnnotationRetention.SOURCE)
@MustBeDocumented
public annotation class MainListenerType(val comment: String = "")


/**
 *
 * ### **父类监听事件类型**标记
 *
 *
 * [ParentListenerType] 是一个**父类监听事件类型**标记。
 * 主要标记一个非 [主要监听事件类型][MainListenerType] 的接口。
 *
 * 相对于 [**主要监听事件类型**][MainListenerType]，
 * 父类监听事件类型是他们的父类，也是不应该优先被监听的类型。
 *
 *
 * ##### 不应被优先使用
 *
 * 这些 **父类监听事件类型** 一般并非作为最下游接口，而是作为父类接口存在。
 * 他们的存在主要是为了最下游接口提供必要的统一能力，而不是为了被监听。
 *
 * ##### 继承 [MsgGet]
 * 所有的 **父类监听事件类型**与 **主要监听事件类型** 都应该是继承自 [MsgGet] 的接口类型。
 *
 *
 * @see MsgGet
 * @see MainListenerType
 *
 * @property comment String 额外说明或描述
 */
@Target(allowedTargets = [CLASS])
@Retention(AnnotationRetention.SOURCE)
@MustBeDocumented
public annotation class ParentListenerType(val comment: String = "")


/**
 *
 * ### **容器类型**标记
 *
 *
 * [ContainerType] 是一个**容器类型**标记。主要标记一个**容器**类型的接口。
 * **容器**主要为了赋予实现类 **可以得到某种信息** 的能力，主要为了用于一些方法的参数与调用上。
 * 但是一般来讲，不应当使用一个 **容器** 作为监听事件的参数或类型。
 *
 *
 * @property comment String 额外说明或描述
 */
@Target(allowedTargets = [CLASS])
@Retention(AnnotationRetention.SOURCE)
@MustBeDocumented
public annotation class ContainerType(val comment: String = "")


