/*
 *     Copyright (c) 2021-2026. ForteScarlet.
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
rootProject.name = "simple-robot"

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        // mavenLocal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
    id("com.gradle.develocity") version "4.3"
}

// https://docs.gradle.com/develocity/intellij-plugin/current/#troubleshooting
develocity {
    // configuration
}

// benchmarks
// include(":benchmarks:v4-v3-core-benchmark")

// internals
include(":internal-processors:interface-uml-processor")
include(":internal-processors:onebot-event-type-resolver-processor")
include(":internal-processors:onebot-include-component-message-elements-processor")
include(":internal-processors:kook-api-reader")
include(":internal-processors:kook-message-element-processor")

// gradle
// include(":simbot-gradles:simbot-gradle-plugin")
include(":simbot-gradles:simbot-gradle-suspendtransforms")

// processors
include("simbot-processors:simbot-processor-message-element-polymorphic-include")
include("simbot-processors:class-builder:simbot-processor-class-builder")
include("simbot-processors:class-builder:simbot-processor-class-builder-annotation")
include("simbot-processors:class-builder:simbot-processor-class-builder-test")

include(":simbot-commons:simbot-common-annotations")
include(":simbot-commons:simbot-common-collection")
include(":simbot-commons:simbot-common-streamable")
include(":simbot-commons:simbot-common-atomic")
include(":simbot-commons:simbot-common-apidefinition")
include(":simbot-commons:simbot-common-core")
include(":simbot-commons:simbot-common-ktor-inputfile")
include(":simbot-commons:simbot-common-suspend-runner")
include(":simbot-commons:simbot-common-stage-loop")
include(":simbot-api")
include(":simbot-test")
include(":simbot-cores:simbot-core")

include(":simbot-logger")
include(":simbot-logger-slf4j2-impl")

// include(":simbot-quantcat:simbot-quantcat-annotations")
include(":simbot-quantcat:simbot-quantcat-common")

include(":simbot-cores:simbot-core-spring-boot-starter-common")
include(":simbot-cores:simbot-core-spring-boot-starter") // v3
include(":simbot-cores:simbot-core-spring-boot-starter-v2")

// extensions
include(":simbot-extensions:simbot-extension-continuous-session")

// components
// onebot
include(":simbot-components:onebot:simbot-component-onebot-common")
include(":simbot-components:onebot:simbot-component-onebot-v11:simbot-component-onebot-v11-common")
include(":simbot-components:onebot:simbot-component-onebot-v11:simbot-component-onebot-v11-core")
include(":simbot-components:onebot:simbot-component-onebot-v11:simbot-component-onebot-v11-event")
include(":simbot-components:onebot:simbot-component-onebot-v11:simbot-component-onebot-v11-message")

// kook
include(":simbot-components:kook:simbot-component-kook-api")
include(":simbot-components:kook:simbot-component-kook-stdlib")
include(":simbot-components:kook:simbot-component-kook-core")

// local tests
include(":tests:spring-boot-starter-test")

// local
// if (!(System.getProperty("IS_CI") ?: System.getenv("IS_CI")).toBoolean()) {
//     include(":tests:native-impl-test")
// }
