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

package love.forte.annotationtool

import kotlin.reflect.KClass


/**
 * Annotated on annotation type.
 * Just like... An annotation extends other annotation?
 *
 * @author ForteScarlet
 */
@Target(AnnotationTarget.ANNOTATION_CLASS)
public annotation class AnnotationMapper(
    vararg val value: KClass<out Annotation>
) {
    
    /**
     * Annotation's property's mapper.
     * Should use on annotation's property method.
     */
    @MustBeDocumented
    @Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
    @Retention(
        AnnotationRetention.RUNTIME
    )
    @JvmRepeatable(
        Properties::class
    )
    public annotation class Property(
        /**
         * Target annotation type.
         *
         *
         * if [AnnotationMapper.value]'s length <= 1, this can be ignored.
         */
        val target: KClass<out Annotation> = Annotation::class,
        /**
         * Target annotation's property name.
         */
        val value: String
    )
    
    
    @MustBeDocumented
    @Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
    @Retention(
        AnnotationRetention.RUNTIME
    )
    public annotation class Properties(vararg val value: Property)
}
