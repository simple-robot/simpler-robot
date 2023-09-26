
/*
 * Copyright (c) 2023 ForteScarlet.
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
    kotlin("jvm") // version "1.9.20-Beta2"

}


tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

dependencies {
    implementation(project(":simbot-util-suspend-transformer"))
    testImplementation(kotlin("test-junit5"))
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        javaParameters = true
        jvmTarget = "21"
        freeCompilerArgs = freeCompilerArgs + listOf("-Xjvm-default=all")
    }
}

kotlin {
    explicitApi()
    this.sourceSets.configureEach {
        languageSettings {
            optIn("kotlin.RequiresOptIn")
        }
    }
}


tasks.withType<JavaCompile> {
    sourceCompatibility = "21"
    targetCompatibility = "21"
    options.encoding = "UTF-8"
}
