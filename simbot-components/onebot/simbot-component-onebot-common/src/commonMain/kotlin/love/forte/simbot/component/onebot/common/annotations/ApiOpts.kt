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

package love.forte.simbot.component.onebot.common.annotations

/**
 * 标记在作为 `OneBotApi` 的响应体数据类的构造上，
 * 并做出如下警告：
 * 这是用作 `OneBotApi` 返回值的类型，它应当仅通过序列化器构造。
 * 作为数据类，它可能无法保证兼容性，请避免直接构造它。
 */
@Target(AnnotationTarget.CONSTRUCTOR)
@Retention(AnnotationRetention.BINARY)
@RequiresOptIn(
    message = "这是用作OneBotApi返回值的类型，" +
        "它应当仅通过序列化器构造。" +
        "作为数据类，它可能无法保证兼容性，请避免直接构造它。",
    level = RequiresOptIn.Level.ERROR
)
@MustBeDocumented
public annotation class ApiResultConstructor
