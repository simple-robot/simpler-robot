/*
 *     Copyright (c) 2025. ForteScarlet.
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

import com.vanniktech.maven.publish.SonatypeHost
import gradle.kotlin.dsl.accessors._962842af322993b246b9354775ec3900.mavenPublishing
import love.forte.gradle.common.core.Gpg
import love.forte.gradle.common.core.property.ofIf
import love.forte.gradle.common.publication.configure.configPublishMaven
import love.forte.gradle.common.publication.configure.publishingExtension
import love.forte.gradle.common.publication.configure.setupPom
import org.jetbrains.kotlin.com.intellij.openapi.util.text.HtmlChunk.p
import utils.checkPublishConfigurable

plugins {
    signing
    id("com.vanniktech.maven.publish")
    id("org.jetbrains.dokka")
}

val p = project

// checkPublishConfigurable {
    mavenPublishing {
        publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
        if (!isSimbotLocal()) {
            signAllPublications()
        }
        coordinates(groupId = p.group.toString(), artifactId = p.name, version = p.version.toString())

        pom {
            name = p.name
            description = p.description
            url = P.HOMEPAGE
            licenses {
                P.Simbot.licenses.forEach { license ->
                    license {
                        name ofIf license.name
                        url ofIf license.url
                        distribution ofIf license.distribution
                        comments ofIf license.comments
                    }
                }
            }

            val scm = P.Simbot.scm
            scm {
                url ofIf scm.url
                connection ofIf scm.connection
                developerConnection ofIf scm.developerConnection
                tag ofIf scm.tag
            }

            developers {
                P.Simbot.developers.forEach { developer ->
                    developer {
                        id ofIf developer.id
                        name ofIf developer.name
                        email ofIf developer.email
                        url ofIf developer.url
                        organization ofIf developer.organization
                        organizationUrl ofIf developer.organizationUrl
                        timezone ofIf developer.timezone
                        roles.addAll(developer.roles)
                        properties.putAll(developer.properties)
                    }
                }
            }

            issueManagement {
                system.set("GitHub Issues")
                url.set("https://github.com/simple-robot/simpler-robot/issues")
            }
        }
    }
// }
