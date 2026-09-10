/*
 *     Copyright (c) 2026. ForteScarlet.
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

package love.forte.simbot.component.qguild.menu

import love.forte.simbot.component.qguild.QGObjectiveContainer
import love.forte.simbot.component.qguild.bot.QGBot
import love.forte.simbot.qguild.model.menu.CustomMenu
import love.forte.simbot.qguild.model.menu.CustomMenuBuilder
import love.forte.simbot.qguild.model.menu.CustomMenuSnapshot
import love.forte.simbot.qguild.model.menu.CustomMenuUpdated
import love.forte.simbot.suspendrunner.ST
import kotlin.jvm.JvmSynthetic

/**
 * 面向 [QGBot] 的 C2C 全局自定义菜单操作器。
 *
 * [QGCustomMenuManager] 不缓存远端菜单，每次读取或更新都会直接请求 QQ API。
 *
 * @since 4.7.0
 */
public interface QGCustomMenuManager {
    /**
     * 获取当前生效的自定义菜单快照。
     *
     * @throws love.forte.simbot.qguild.QQGuildApiException 请求被 QQ API 拒绝时抛出。
     */
    @ST
    public suspend fun get(): QGCustomMenuSnapshot

    /**
     * 整体覆盖当前自定义菜单。
     *
     * @throws love.forte.simbot.qguild.QQGuildApiException 请求被 QQ API 拒绝时抛出。
     */
    @ST
    public suspend fun update(menu: CustomMenu): QGCustomMenuUpdateReceipt

}
/**
 * 使用 [CustomMenuBuilder] DSL 整体覆盖当前自定义菜单。
 *
 * 构建器的字段组合校验仍由低层模型和服务端负责。
 *
 * @since 4.7.0
 */
@JvmSynthetic
public suspend inline fun QGCustomMenuManager.update(block: CustomMenuBuilder.() -> Unit): QGCustomMenuUpdateReceipt =
    update(CustomMenuBuilder().apply(block).build())

/**
 * 当前自定义菜单及其服务端版本的快照。
 *
 * @property version 服务端返回的菜单版本。
 * @property menu 当前生效的菜单；从未设置时为 `null`。
 * @property source 低层 API 返回的原始快照。
 * @since 4.7.0
 */
public class QGCustomMenuSnapshot internal constructor(
    override val source: CustomMenuSnapshot,
) : QGObjectiveContainer<CustomMenuSnapshot> {
    public val version: Int
        get() = source.version
    public val menu: CustomMenu?
        get() = source.menu

    override fun toString(): String {
        return "QGCustomMenuSnapshot(version=$version, menu=$menu)"
    }
}

/**
 * 自定义菜单更新后的服务端回执。
 *
 * @property version 服务端返回的更新后版本号。
 * @property source 低层 API 返回的原始回执。
 * @since 4.7.0
 */
public class QGCustomMenuUpdateReceipt(
    override val source: CustomMenuUpdated,
) : QGObjectiveContainer<CustomMenuUpdated> {
    public val version: Int
        get() = source.version

    override fun toString(): String {
        return "QGCustomMenuUpdateReceipt(version=$version)"
    }
}
