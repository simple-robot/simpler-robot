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

// includePro(":tencent-guild:api", "components/tencent-guild/api", "tencent-guild-api")
// includePro(":tencent-guild:core", "components/tencent-guild/core", "tencent-guild-core")
// includePro(":tencent-guild:component", "components/tencent-guild/component", "component-tencent-guild")

fun includePro(proName: String, dir: String? = null, name: String? = null): String {
    include(proName)
    if (dir != null) {
        if (File(rootDir, "$dir/build.gradle.kts").exists()) {
            project(proName).projectDir = file(dir)
        } else {
            println("$rootDir/$dir/build.gradle.kts 不存在")
        }
    }
    if (name != null) {
        project(proName).name = name
        return name
    }
    return proName
}

rootProject.children.forEach {
    println(it)
}