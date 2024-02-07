/*
 *     Copyright (c) 2024. ForteScarlet.
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

package love.forte.simbot.extension.continuous.session

/**
 * 冲突的 session key.
 */
public class ConflictSessionKeyException(message: String?) : IllegalArgumentException(message)

/**
 * 因出现冲突的 session key 而被替换
 */
public class ReplacedBecauseOfConflictSessionKeyException(message: String?) : IllegalStateException(message)

/**
 * 当使用 [ContinuousSessionProvider.push] 推送失败时，
 * 会将异常包装在 [SessionPushOnFailureException.cause] 中。
 */
public class SessionPushOnFailureException : IllegalStateException {
    public constructor(message: String?) : super(message)
    public constructor(message: String?, cause: Throwable?) : super(message, cause)
    public constructor(cause: Throwable?) : super(cause)
}

/**
 * 当使用 [ContinuousSessionProvider.push] 推送成功、
 * 但是在 [ContinuousSessionReceiver.await] 过程中出现异常时（例如构造返回给 `push` 的结果时出现异常）
 * 则此异常会使用 [SessionAwaitOnFailureException] 进行包装。
 */
public class SessionAwaitOnFailureException : IllegalStateException {
    public constructor(message: String?) : super(message)
    public constructor(message: String?, cause: Throwable?) : super(message, cause)
    public constructor(cause: Throwable?) : super(cause)
}
