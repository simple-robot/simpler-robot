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
package love.forte.simbot.interception

/**
 * 标识一种可拦截操作，并关联其业务上下文 [C] 与返回值 [R]。
 *
 * 通常由组件或插件以单例 `object` 定义，注册与执行必须使用同一个拦截点实例。
 * 注册表按拦截点的 `equals` / `hashCode` 查找，因此不同类型的点不得比较为相等，
 * 也不得在注册期间改变这些方法的结果。
 *
 * @param C 非空的业务上下文类型。
 * @param R 操作返回值类型，可以为空。
 * @since 5.0
 * @author ForteScarlet
 */
@Suppress("unused")
public interface InterceptionPoint<C : Any, R>
