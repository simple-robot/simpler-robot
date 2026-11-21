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
 * 标记仅供框架内部实现或继承的拦截器 API。
 *
 * 被标记的类型可由用户正常使用，但自行继承需要显式 opt-in。
 * 框架可能为这些类型增加抽象成员，不保证第三方实现的源码或二进制兼容性。
 * 构建自定义执行链时应优先使用 [Interceptor.Chain.create]。
 *
 * @since 5.0
 */
@Retention(AnnotationRetention.BINARY)
@RequiresOptIn(
    message = "此拦截器类型仅供框架内部继承，不保证第三方实现的兼容性；请优先使用公开工厂。"
)
@MustBeDocumented
public annotation class InternalInterceptorInheritanceApi
