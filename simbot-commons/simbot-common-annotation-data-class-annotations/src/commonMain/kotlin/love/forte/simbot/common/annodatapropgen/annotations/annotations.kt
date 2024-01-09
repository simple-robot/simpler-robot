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

package love.forte.simbot.common.annodatapropgen.annotations

import kotlin.reflect.KClass

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.ANNOTATION_CLASS)
annotation class GenDataClass(
    val targetName: String = "",
    val propertiesMutable: Boolean = true
)

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.ANNOTATION_CLASS)
@Repeatable
annotation class GenDataClassFrom(
    val from: KClass<out Annotation> = GenDataClassFrom::class,
    val fromClass: String = "",
    /**
     * Target package name.
     */
    val to: String = SAME_AS_FROM,
    val targetName: String = "",
    val propertiesMutable: Boolean = true
) {
    companion object {
        const val SAME_AS_FROM = "$\$SAME_AS_FROM$$"
        const val SAME_AS_CURRENT = "$\$SAME_AS_CURRENT$$"
    }
}
