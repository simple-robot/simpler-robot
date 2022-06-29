/*
 *  Copyright (c) 2022-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  本文件是 simply-robot (或称 simple-robot 3.x 、simbot 3.x ) 的一部分。
 *
 *  simply-robot 是自由软件：你可以再分发之和/或依照由自由软件基金会发布的 GNU 通用公共许可证修改之，无论是版本 3 许可证，还是（按你的决定）任何以后版都可以。
 *
 *  发布 simply-robot 是希望它能有用，但是并无保障;甚至连可销售和符合某个特定的目的都不保证。请参看 GNU 通用公共许可证，了解详情。
 *
 *  你应该随程序获得一份 GNU 通用公共许可证的复本。如果没有，请看:
 *  https://www.gnu.org/licenses
 *  https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *  https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 */


tasks.create("createChangelog") {
    group = "build"
    doFirst {
        val realVersion = rootProject.version
        val version = "v$realVersion"
        println("Generate change log for $version ...")
        // configurations.runtimeClasspath
        val changelogDir = rootProject.file(".changelog").also {
            it.mkdirs()
        }
        val file = File(changelogDir, "$version.md")
        if (!file.exists()) {
            file.createNewFile()
            val autoGenerateText = """
                

                ## 组件更新
                相关组件会在后续三日内跟进更新
                - [mirai组件](https://github.com/simple-robot/simbot-component-mirai/releases)
                - [腾讯频道组件](https://github.com/simple-robot/simbot-component-tencent-guild/releases)
                - [开黑啦组件](https://github.com/simple-robot/simbot-component-kaiheila/releases)

                ## 仓库参考
                
                - [simbot-api: $version](https://repo1.maven.org/maven2/love/forte/simbot/simbot-api/$realVersion)
                - [simbot-core: $version](https://repo1.maven.org/maven2/love/forte/simbot/simbot-core/$realVersion)
                - [simbot-logger: $version](https://repo1.maven.org/maven2/love/forte/simbot/simbot-logger/$realVersion)
                - [simboot-api: $version](https://repo1.maven.org/maven2/love/forte/simbot/boot/simboot-api/$realVersion)
                - [simboot-core: $version](https://repo1.maven.org/maven2/love/forte/simbot/boot/simboot-core/$realVersion)
                - [simboot-core-annotation: $version](https://repo1.maven.org/maven2/love/forte/simbot/boot/simboot-core-annotation/$realVersion)
                - [simboot-core-spring-boot-starter: $version](https://repo1.maven.org/maven2/love/forte/simbot/boot/simboot-core-spring-boot-starter/$realVersion)

                
            """.trimIndent()
            
            
            file.writeText(autoGenerateText)
        }
        
        
    }
}