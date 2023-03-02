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

plugins {
    id("simbot.base-module-conventions")
    id("simbot.util-module-conventions")
    id("simbot.dokka-module-configuration")
    `simbot-jvm-maven-publish`
}


dependencies {
    api(project(":simbot-util-di-api"))
    api(kotlin("reflect"))
    compileOnly("org.springframework:spring-context:6.0.6") // component
    compileOnly("org.springframework:spring-core:5.3.25") // aliasFor
    compileOnly("org.springframework.boot:spring-boot:${libs.versions.spring.boot.get()}") // ConfigurationProperties
}


// suppress all
tasks.withType<org.jetbrains.dokka.gradle.DokkaTaskPartial>().configureEach {
    dokkaSourceSets.configureEach {
        suppress.set(true)
        perPackageOption {
            suppress.set(true)
        }
    }
}
