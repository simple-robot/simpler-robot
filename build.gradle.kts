/*
 *  Copyright (c) 2021-2021 ForteScarlet <https://github.com/ForteScarlet>
 *
 *  根据 Apache License 2.0 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */


plugins {
    kotlin("jvm") version "1.6.0" apply false
    kotlin("multiplatform") version "1.6.0" apply false
    kotlin("plugin.serialization") version "1.6.0" apply false
    id("org.jetbrains.dokka") version "1.5.30" apply false
}



group = P.Simbot.GROUP
version = P.Simbot.VERSION

repositories {
    mavenCentral()
}

subprojects {
    group = P.Simbot.GROUP
    version = P.Simbot.VERSION

    repositories {
        mavenCentral()
    }

    plugins.findPlugin("org.jetbrains.dokka")?.let {
        configDokka()
    }


}



/**
 * config dokka output.
 */
inline fun Project.configDokka() {
    tasks.named<org.jetbrains.dokka.gradle.DokkaTask>("dokkaHtml") {
        val root = rootProject.rootDir
        outputDirectory.set(File(root, "dokkaOutput/${project.name}"))
    }
}




