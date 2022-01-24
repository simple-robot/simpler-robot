/*
 *  Copyright (c) 2021-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  根据 GNU LESSER GENERAL PUBLIC LICENSE 3 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.gnu.org/licenses/lgpl-3.0-standalone.html
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

includePro(":apis:simbot-api")
// includePro(":simbot-annotation")
includePro(":cores:simbot-core")

includePro(":boots:simboot-api")
includePro(":boots:simboot-core-annotation")
includePro(":boots:simboot-core")
includePro(":boots:simboot-core-springboot-starter")



fun includePro(path: String, dir: String? = null, name: String? = null): String {
    include(path)
    if (dir != null) {
        if (File(rootDir, "$dir/build.gradle.kts").exists()) {
            project(path).projectDir = file(dir)
        } else {
            println("$rootDir/$dir/build.gradle.kts 不存在")
        }
    }
    if (name != null) {
        project(path).name = name
        return name
    }
    return path
}

rootProject.children.forEach {
    println(it)
}