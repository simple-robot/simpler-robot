/*
 * Copyright (c) 2023 ForteScarlet.
 *
 * This file is part of Simple Robot.
 *
 * Simple Robot is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Simple Robot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Simple Robot. If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.di.annotation

import org.springframework.context.annotation.Primary

/**
 * 标记一个注入对象为主要目标。
 * 当在bean管理中存在多个相似类型的时候（例如某类型的多个子类或子类实例），通过标记一个 [Preferred] 来指定一个**主要**的目标。
 *
 * 一次类型获取中，主要目标应至多一个。
 *
 * @see love.forte.di.Bean.isPreferred
 *
 */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Primary // for spring
public annotation class Preferred
