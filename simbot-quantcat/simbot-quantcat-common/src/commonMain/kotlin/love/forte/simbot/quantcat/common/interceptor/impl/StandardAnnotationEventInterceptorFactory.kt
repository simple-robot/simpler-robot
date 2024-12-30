/*
 *     Copyright (c) 2024. ForteScarlet.
 *
 *     Project    https://github.com/simple-robot/simpler-robot
 *     Email      ForteScarlet@163.com
 *
 *     This file is part of the Simple Robot Library.
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

package love.forte.simbot.quantcat.common.interceptor.impl

import love.forte.simbot.event.EventInterceptor
import love.forte.simbot.event.EventListenerContext
import love.forte.simbot.event.EventResult
import love.forte.simbot.quantcat.common.interceptor.AnnotationEventInterceptorFactory

/**
 * 一些默认提供的标准 [AnnotationEventInterceptorFactory] 实现。
 *
 * @author ForteScarlet
 */
public sealed class StandardAnnotationEventInterceptorFactory : AnnotationEventInterceptorFactory

/**
 * 会注册一个拦截器用以将 [EventListenerContext.plainText]
 * 使用 [String.trim] 处理。
 */
public data object ContentTrimEventInterceptorFactory : StandardAnnotationEventInterceptorFactory() {
    override fun create(context: AnnotationEventInterceptorFactory.Context): AnnotationEventInterceptorFactory.Result {
        return AnnotationEventInterceptorFactory.Result.build {
            interceptor(InterceptorImpl)
            configuration { priority = context.priority }
        }
    }

    private data object InterceptorImpl : EventInterceptor {
        override suspend fun EventInterceptor.Context.intercept(): EventResult {
            with(eventListenerContext) {
                plainText = plainText?.trim()
            }

            return invoke()
        }
    }
}

