/*
 *     Copyright (c) 2022-2026. ForteScarlet.
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

import org.gradle.api.Project
import java.net.URI

sealed class Sonatype {
    abstract val name: String
    abstract val url: String
    fun Project.uri(): URI = uri(url)
    
    object Central : Sonatype() {
        const val NAME = "central"
        const val URL = "https://oss.sonatype.org/service/local/staging/deploy/maven2/"
        override val name: String get() = NAME
        override val url: String get() = URL
    }
    
    object Snapshot : Sonatype() {
        const val NAME = "snapshot"
        const val URL = "https://oss.sonatype.org/content/repositories/snapshots/"
        override val name: String get() = NAME
        override val url get() = URL
    }
}
