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

package love.forte.simbot

/**
 * 处理器，针对于一个目标进行处理的函数。
 *
 * @author ForteScarlet
 */
public interface Processor<T, R> {

    /**
     * 对目标进行处理, 并得到一个结果。
      */
    @JvmSynthetic
    public suspend fun process(target: T): R

}


/**
 * 一个 [异常][Throwable] [处理器][Processor].
 */
public interface ExceptionProcessor<R> : Processor<Throwable, R>
