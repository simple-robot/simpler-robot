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

package love.forte.simbot.processor.message.element.polymorphic.include

import com.squareup.kotlinpoet.KModifier
import kotlinx.serialization.Serializable


/**
 * configurations for [MessageElementPolymorphicIncludeProcessor].
 *
 * @author ForteScarlet
 */
@Serializable
public open class MessageElementPolymorphicIncludeConfiguration {
    public open var baseClass: String = "love.forte.simbot.message.Message.Element"
    public open var enable: Boolean = true
    public open var localOnly: Boolean = false
    public open var visibility: String = "internal"
    public open var generateFunName: String = "includeMessageElementPolymorphic"
    public open var outputPackage: String? = null
    public open var outputFileName: String = "MessageElementPolymorphicInclude.generated"
    public open var outputFileJvmName: String? = null
    public open var outputFileJvmMultifile: Boolean = false

    public open fun visibilityValue(): KModifier {
        return when {
            visibility.equals("internal", ignoreCase = true) -> KModifier.INTERNAL
            visibility.equals("public", ignoreCase = true) -> KModifier.PUBLIC
            else -> throw IllegalArgumentException("Unknown visibility: $visibility, not in ['internal', 'public']")
        }
    }

    public companion object {
        public const val CONFIG_PREFIX: String = "simbot.processor.message-element-polymorphic-include"

    }
}

