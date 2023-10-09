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

package love.forte.simbot.id

import kotlinx.serialization.Serializable

/**
 *
 *
 *
 * @author ForteScarlet
 */
@Serializable(with = AsStringIDSerializer::class)
public actual sealed class ID actual constructor() : Comparable<ID>, Cloneable {
    /**
     * ID 的字面值字符串。
     *
     * @return 字面值字符串
     */
    actual abstract override fun toString(): String

    /**
     * ID 的源值 hashcode，等于对应的源值的 hashcode。
     *
     * 不同类型但字面值相同的ID可能会有不同的 hashCode，例如字符串ID `"1"` 和数字ID `1`。
     */
    actual abstract override fun hashCode(): Int

    /**
     * 判断另外一个 [ID] 是否与当前 [ID] **字面值相同**。
     *
     * 任意类型的 ID 的 [equals] 应始终可以与其他任意类型的 [ID] 进行字面值比对。
     * 例如一个字面值为字符串 `"1"` 的 [ID] 与字面值是数字 `1` 的 [ID] 通过 [equals]
     * 进行比对，那么结果将会是 `true`。
     *
     * 如果希望在比对的时候连带类型进行比对，参考使用 [equalsExact]。
     *
     * @see equalsExact
     */
    actual abstract override fun equals(other: Any?): Boolean

    /**
     * 判断另外一个 [ID] 是否与当前 [ID] **字面值与类型均相同**。
     *
     * 会同时比对类型与字面值，[equalsExact] 更类似于传统的 `equals` 逻辑。
     */
    public actual abstract fun equalsExact(other: Any?): Boolean

    /**
     * 复制一个当前ID。
     */
    public actual abstract fun copy(): ID

    /**
     * 克隆当前ID。
     */
    public override fun clone(): ID = super.clone() as ID
}
