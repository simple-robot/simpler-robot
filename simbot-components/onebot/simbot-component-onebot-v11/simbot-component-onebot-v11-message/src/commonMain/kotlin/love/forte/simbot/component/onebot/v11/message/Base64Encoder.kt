/*
 *     Copyright (c) 2025-2026. ForteScarlet.
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

package love.forte.simbot.component.onebot.v11.message

import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.jvm.JvmField


/**
 * A base64 encoder.
 *
 * @since 1.6.1
 *
 * @author ForteScarlet
 */
public interface Base64Encoder {
    /**
     * Encodes [source] ByteArray to a Base64 encoded ByteArray.
     */
    public fun <A : Appendable> encodeToAppendable(source: ByteArray, destination: A): A

    public companion object {
        /**
         * Get a [Base64Encoder] instance based on [kotlin.io.encoding.Base64.Default].
         */
        @ExperimentalEncodingApi
        @JvmField
        public val Default: Base64Encoder = KotlinBase64Encoder(Base64.Default)

        /**
         * Get a [Base64Encoder] instance based on [kotlin.io.encoding.Base64.Mime].
         */
        @ExperimentalEncodingApi
        @JvmField
        public val Mime: Base64Encoder = KotlinBase64Encoder(Base64.Mime)

        /**
         * Get a [Base64Encoder] instance based on [kotlin.io.encoding.Base64.UrlSafe].
         */
        @ExperimentalEncodingApi
        @JvmField
        public val UrlSafe: Base64Encoder = KotlinBase64Encoder(Base64.UrlSafe)
    }
}

@ExperimentalEncodingApi
private class KotlinBase64Encoder(val base64: Base64) : Base64Encoder {
    override fun <A : Appendable> encodeToAppendable(source: ByteArray, destination: A): A {
        base64.encodeToAppendable(source, destination)
        return destination
    }
}

@OptIn(ExperimentalEncodingApi::class)
internal fun standardEncoderByName(name: String): Base64Encoder? {
    return when {
        name.equals("Default", true) -> Base64Encoder.Default
        name.equals("Mime", true) -> Base64Encoder.Mime
        name.equals("UrlSafe", true) || name.equals("url_safe", true)
        -> Base64Encoder.UrlSafe

        else -> null
    }
}

@OptIn(ExperimentalEncodingApi::class)
internal fun Base64Encoder.standardName(): String? {
    return when (this) {
        Base64Encoder.Default -> "Default"
        Base64Encoder.Mime -> "Mime"
        Base64Encoder.UrlSafe -> "UrlSafe"
        else -> null
    }
}

@OptIn(ExperimentalEncodingApi::class)
internal val Base64Encoder.base64: Base64? get() = (this as? KotlinBase64Encoder)?.base64
