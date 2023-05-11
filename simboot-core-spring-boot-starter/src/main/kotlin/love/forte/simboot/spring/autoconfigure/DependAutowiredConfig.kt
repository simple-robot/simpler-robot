/*
 * Copyright (c) 2021-2023 ForteScarlet.
 *
 * This file is part of Simple Robot.
 *
 * Simple Robot is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Simple Robot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Simple Robot. If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simboot.spring.autoconfigure

import love.forte.di.annotation.Depend
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor
import org.springframework.beans.factory.config.SmartInstantiationAwareBeanPostProcessor


private fun getDependAnnotationBeanPostProcessor(): AutowiredAnnotationBeanPostProcessor =
    AutowiredAnnotationBeanPostProcessor().also {
        it.setAutowiredAnnotationType(Depend::class.java)
    }

/**
 * 使 [Depend] 支持 autowired.
 * @author ForteScarlet
 */
public open class AutowiredConfig :
    SmartInstantiationAwareBeanPostProcessor
    by getDependAnnotationBeanPostProcessor()
