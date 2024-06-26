/*
 *     Copyright (c) 2022-2024. ForteScarlet.
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

package utils

import Env
import isSnapshot

data class PublishConfigurableResult(
    val isSnapshotOnly: Boolean,
    val isReleaseOnly: Boolean,
    val isPublishConfigurable: Boolean = when {
        isSnapshotOnly -> isSnapshot()
        isReleaseOnly -> !isSnapshot()
        else -> true
    },
)


fun checkPublishConfigurable(): PublishConfigurableResult {
    val isSnapshotOnly =
        (System.getProperty("snapshotOnly") ?: System.getenv(Env.SNAPSHOT_ONLY))?.equals("true", true) == true
    val isReleaseOnly =
        (System.getProperty("releaseOnly") ?: System.getenv(Env.RELEASES_ONLY))?.equals("true", true) == true

    return PublishConfigurableResult(isSnapshotOnly, isReleaseOnly)
}

inline fun checkPublishConfigurable(block: PublishConfigurableResult.() -> Unit) {
    val v = checkPublishConfigurable()
    if (v.isPublishConfigurable) {
        v.block()
    }
}
