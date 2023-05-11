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
package love.forte.annotationtool.core

/**
 *
 * A type converter. Converter an instance from type A to type B.
 *
 * @author ForteScarlet
 */
public interface Converter<FROM : Any, TO : Any> {

    /**
     * Converter an instance of type [FROM] to type [TO].
     * @param instance instance of type [FROM]
     * @return converted type. return null if it cannot be converted.
     * @throws ConvertException if it cannot be converted.
     *
     * @see ConvertException
     */
    public fun convert(instance: FROM): TO?
}
