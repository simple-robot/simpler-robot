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

@file:Suppress("unused")

import love.forte.gradle.common.core.project.ProjectDetail
import love.forte.gradle.common.core.property.systemProp
import org.gradle.api.Project

@Suppress("ObjectPropertyName")
private val _isSnapshot: Boolean by lazy {
    val snapProp = System.getProperty("isSnapshot")?.toBoolean() ?: false
    val snapEnv = System.getenv(Env.IS_SNAPSHOT)?.toBoolean() ?: false

    logger.info("IsSnapshot from system.property: {}", snapProp)
    logger.info("IsSnapshot from system.env:      {}", snapEnv)

    snapProp || snapEnv
}

fun isSnapshot(): Boolean = _isSnapshot


/**
 * Project versions.
 */
@Suppress("MemberVisibilityCanBePrivate")
sealed class P(override val group: String) : ProjectDetail() {
    companion object {
        const val VERSION = "4.8.0"
        const val NEXT_VERSION = "4.8.1"
        const val SNAPSHOT_VERSION = "$VERSION-SNAPSHOT"
        const val NEXT_SNAPSHOT_VERSION = "$NEXT_VERSION-SNAPSHOT"

        const val GROUP = "love.forte.simbot"
        const val GROUP_COMMON = "love.forte.simbot.common"
        const val GROUP_LOGGER = "love.forte.simbot.logger"
        const val GROUP_GRADLE = "love.forte.simbot.gradle"
        const val GROUP_QUANTCAT = "love.forte.simbot.quantcat"
        const val GROUP_EXTENSION = "love.forte.simbot.extension"
        const val GROUP_BENCHMARK = "love.forte.simbot.benchmark"
        const val GROUP_PROCESSOR = "love.forte.simbot.processor"

        // const val COMPONENT_GROUP = "love.forte.simbot.component"
        const val DESCRIPTION = "Simple Robot，一个通用的bot风格事件调度框架，以灵活的统一标准来编写bot应用。"
        const val HOMEPAGE = "https://github.com/simple-robot/simpler-robot"

        fun findProjectDetailByGroup(group: String): ProjectDetail? {
            val groupProject =
                P::class.sealedSubclasses.mapNotNull { it.objectInstance }.associateBy { obj -> obj.group }
            return groupProject[group]
        }

    }

    override val homepage: String get() = HOMEPAGE

    object Simbot : P(GROUP)
    object SimbotCommon : P(GROUP_COMMON)
    object SimbotLogger : P(GROUP_LOGGER)
    object SimbotGradle : P(GROUP_GRADLE)
    object SimbotQuantcat : P(GROUP_QUANTCAT)
    object SimbotExtension : P(GROUP_EXTENSION)
    object SimbotBenchmark : P(GROUP_BENCHMARK)

    final override val version: String = if (isSnapshot()) {
        NEXT_SNAPSHOT_VERSION
    } else {
        VERSION
    }

    override val description: String get() = DESCRIPTION
    override val developers: List<Developer> = developers {
        developer {
            id = "forte"
            name = "ForteScarlet"
            email = "ForteScarlet@163.com"
            url = "https://github.com/ForteScarlet"
        }
        developer {
            id = "forliy"
            name = "ForliyScarlet"
            email = "ForliyScarlet@163.com"
            url = "https://github.com/ForliyScarlet"
        }
    }
    override val licenses: List<License> = licenses {
        license {
            name = "GNU GENERAL PUBLIC LICENSE, Version 3"
            url = "https://www.gnu.org/licenses/gpl-3.0-standalone.html"
        }
        license {
            name = "GNU LESSER GENERAL PUBLIC LICENSE, Version 3"
            url = "https://www.gnu.org/licenses/lgpl-3.0-standalone.html"
        }
    }
    override val scm: Scm = scm {
        url = HOMEPAGE
        connection = "scm:git:$HOMEPAGE.git"
        developerConnection = "scm:git:ssh://git@github.com/simple-robot/simpler-robot.git"
    }


}

fun isSimbotLocal(): Boolean = systemProp("SIMBOT_LOCAL").toBoolean()

fun Project.setupGroup(pro: ProjectDetail) {
    group = pro.group
}
