/*
 *     Copyright (c) 2024-2026. ForteScarlet.
 *
 *     Project    https://github.com/simple-robot/simpler-robot
 *     Email      ForteScarlet@163.com
 *
 *     This file is part of the Simple Robot Library (Alias: simple-robot, simbot, etc.).
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     Lesser GNU General Public License for more details.
 *
 *     You should have received a copy of the Lesser GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package love.forte.simbot.component.onebot.v11.event

import love.forte.simbot.common.id.LongID
import love.forte.simbot.component.onebot.common.annotations.InternalOneBotAPI


/**
 * OneBot11的[事件](https://github.com/botuniverse/onebot-11/tree/master/event)。
 *
 * 这是 OneBot11 原始的事件对象结构体的定义，不是对simbot Event的实现，它的所有最终具体实现类均为普通的不可变数据类。
 *
 * ## 实例化与兼容性
 *
 * 此原始事件类型是用于序列化交互的数据类，它应当仅通过反序列化被构建，不建议直接使用它们的构造方式进行构建，
 * 也不保证它们的构造对外具有二进制兼容性。
 *
 * ## 基本描述
 *
 * > 下述内容来自OneBot11文档。
 *
 * 事件是用户需要从 OneBot 被动接收的数据，有以下几个大类：
 *
 * - 消息事件，包括私聊消息、群消息等
 * - 通知事件，包括群成员变动、好友变动等
 * - 请求事件，包括加群请求、加好友请求等
 * - 元事件，包括 OneBot 生命周期、心跳等
 *
 * 在所有能够推送事件的通信方式中（HTTP POST、正向和反向 WebSocket），事件都以 JSON 格式表示。
 *
 * ### 内容字段
 *
 * 每个事件都有 `time`、`self_id` 和 `post_type` 字段，如下：
 *
 * | 字段名 | 数据类型 | 说明 |
 * | ----- | ------- | ---- |
 * | `time` | number (int64) | 事件发生的时间戳 |
 * | `self_id` | number (int64) | 收到事件的机器人 QQ 号 |
 * | `post_type` | string | 事件类型 |
 *
 * 其中 `post_type` 不同字段值表示的事件类型对应如下：
 *
 * - `message`：消息事件
 * - `notice`：通知事件
 * - `request`：请求事件
 * - `meta_event`：元事件
 *
 * @author ForteScarlet
 */
public interface RawEvent {
    /**
     * 事件发生的时间戳
     */
    public val time: Long

    /**
     * 收到事件的机器人 QQ 号
     */
    public val selfId: LongID

    /**
     * 事件类型
     */
    public val postType: String
}

/**
 * 标记在一个具体地可序列化事件类型上，
 * 表示它的 [RawEvent.postType] 的预期值。
 *
 * ### 二级分类
 *
 * 一个事件通常通过两个属性来确定最终的类型：
 * 首先通过 [RawEvent.postType] 确定大分类（比如 `message`、`notice` 等），
 * 其次通过一个子类型来确定具体的类型(
 * 比如 `message` 事件中的 `message_type`,
 * `notice` 事件中的 `notice_type`
 * )。
 * 而子类型的字段名在不同的事件中是不一样的。这里的 [subType] 只关心值，不理会字段名。
 *
 * @suppress 用于项目内部代码生成器
 *
 */
@Retention(AnnotationRetention.SOURCE)
@InternalOneBotAPI
public annotation class ExpectEventType(val postType: String, val subType: String)

/**
 * 标记在作为大分类事件的类型上，用来描述它们对应的二级分类属性 (也就是[ExpectEventType.subType])
 * 是什么。
 * 属性的类型应当是一个不可空的字符串类型，例如 `val metaEventType: String`。
 *
 * @suppress 用于项目内部代码生成器
 */
@Retention(AnnotationRetention.SOURCE)
@InternalOneBotAPI
public annotation class ExpectEventSubTypeProperty(val value: String, val postType: String, val name: String)
