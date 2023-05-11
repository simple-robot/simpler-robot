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

val springVersion = "5.3.13"
val springBootVersion: String = libs.versions.spring.boot.get()

dependencies {
    api(libs.javax.inject)
    api(libs.slf4j.api)
    
    compileOnly(project(":simbot-util-annotation-tool"))
    
    compileOnly(libs.javax.annotation.api)
    compileOnly("org.springframework:spring-context:5.3.25")
    compileOnly("org.springframework:spring-core:6.0.9")
    compileOnly("org.springframework.boot:spring-boot:$springBootVersion")
    compileOnly("org.springframework.boot:spring-boot-autoconfigure:$springBootVersion")
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
