
/*
 * Copyright (c) 2021-2023 ForteScarlet.
 *
 * This file is part of Simple Robot.
 *
 * Simple Robot is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Simple Robot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Simple Robot. If not, see <https://www.gnu.org/licenses/>.
 */
rootProject.name = "simply-robot"

include(":simbot-logger")
include(":simbot-logger-slf4j-impl")

include(":simbot-annotations")
include(":simbot-api")
include(":simbot-core")

include(
    ":simboot-api",
    ":simboot-core-annotation",
    ":simboot-core",
    ":simboot-core-spring-boot-starter",
)


include(
    ":simbot-util-api-requestor-core",
    ":simbot-util-api-requestor-ktor",
    ":simbot-util-stage-loop",
)

include(":simbot-util-annotation-tool")

include(
    ":simbot-util-di-api",
    ":simbot-util-di-core",
)

include(":simbot-util-suspend-transformer")

// project test
// if not in CI workflows
if (!System.getenv("IS_CI").toBoolean()) {
//    include(
//        projectTest("boot"),
//        projectTest("spring-boot-starter"),
//        projectTest("jmh"),
//    )

    include(":simbot-component-http-server-api")
}



@Suppress("NOTHING_TO_INLINE")
inline fun projectTest(moduleName: String): String = ":simbot-project-tests:simbot-project-test-$moduleName"
