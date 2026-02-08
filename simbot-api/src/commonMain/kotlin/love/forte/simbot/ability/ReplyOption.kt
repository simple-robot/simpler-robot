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

package love.forte.simbot.ability

/**
 * 使用 [ReplySupport.reply] 时可以使用的额外选项。
 * 选项的实现由 [ReplySupport] 的实现方自由扩展。
 *
 * 对于实现方而言，当遇到 **可预期** 的选项出现无法使用、无法满足的条件等情况时，
 * 应当优先考虑抛出明确的异常。而对于 **预期外** 的选项（例如由其他实现方实现的未知类型），
 * 则优先考虑对其进行忽略过滤。
 *
 * @see ReplySupport.reply
 * @author ForteScarlet
 * @since 5.0
 */
public interface ReplyOption
