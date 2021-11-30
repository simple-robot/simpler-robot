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

pluginManagement {
    repositories {
        mavenCentral()
        maven { setUrl("https://plugins.gradle.org/m2/") }
    }
}
rootProject.name = "simply-robot"

includePro(":apis:api")
includePro(":annotation")
includePro(":cores:core")
includePro(":cores:boot")
// includePro(":components:component-tencent-guild")
// if (File(rootProject.projectDir, "components/tencent-guild-api/build.gradle.kts").exists()) {
//     includePro(":components:tencent-guild-api")
// }

fun includePro(proName: String, dir: String? = null) {
    include(proName)
    if (dir != null) {
        project(proName).projectDir = file(dir)
    }
}
