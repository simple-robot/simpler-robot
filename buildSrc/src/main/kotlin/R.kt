/*
 * Copyright (c) 2022-2023 ForteScarlet.
 *
 * This file is part of Simple Robot.
 *
 * Simple Robot is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Simple Robot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Simple Robot. If not, see <https://www.gnu.org/licenses/>.
 */

import love.forte.gradle.common.core.property.systemProp
import love.forte.gradle.common.core.repository.Repositories
import love.forte.gradle.common.core.repository.SimpleCredentials
import org.slf4j.LoggerFactory


val logger = LoggerFactory.getLogger("Sonatype Userinfo")

private val sonatypeUserInfo by lazy {
    val userInfo = love.forte.gradle.common.publication.sonatypeUserInfoOrNull
    
    if (userInfo == null) {
        logger.warn("sonatype.username or sonatype.password is null, cannot config nexus publishing.")
    }
    
    userInfo
}

private val sonatypeUsername: String? get() = sonatypeUserInfo?.username
private val sonatypePassword: String? get() = sonatypeUserInfo?.password


val ReleaseRepository by lazy {
    
    Repositories.Central.Default.copy(SimpleCredentials(sonatypeUsername, sonatypePassword))
}
val SnapshotRepository by lazy {
    Repositories.Snapshot.Default.copy(SimpleCredentials(sonatypeUsername, sonatypePassword))
}
