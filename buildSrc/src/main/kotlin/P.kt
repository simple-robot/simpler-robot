/*
 *  Copyright (c) 2021-2022 ForteScarlet <ForteScarlet@163.com>
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
 *
 */


abstract class SimbotProject {

    abstract val group: String
    abstract val version: String
}


/**
 * Project versions.
 */
sealed class P : SimbotProject() {
    object Simbot {
        const val GROUP = "love.forte.simbot"
        const val BOOT_GROUP = "love.forte.simbot.boot"
        const val VERSION = "3.0.0.preview.2.0"
    }

    sealed class ForteDI(id: String) : Dep(GROUP, "di-$id", VERSION) {
        companion object {
            const val GROUP = "love.forte.di"
            const val VERSION = "0.0.3"
        }
        object Api : ForteDI("api")
        object Core : ForteDI("core")
        object Spring : ForteDI("spring")

    }

    // "love.forte.annotation-tool:api:0.6.1"
    sealed class AnnotationTool(id: String) : Dep(GROUP, id, VERSION) {
        companion object {
            const val GROUP = "love.forte.annotation-tool"
            const val VERSION = "0.6.3"
        }
        object Api : AnnotationTool("api")
        object Core : AnnotationTool("core")
        object KCore : AnnotationTool("kcore")

    }

    // object TencentGuild {
    //     const val GROUP = Simbot.GROUP
    //     const val VERSION = "0.0.2"
    //
    //     const val apiPath = ":components:tencent-guild:tencent-guild-api"
    //     const val corePath = ":components:tencent-guild:tencent-guild-core"
    //     const val componentPath = ":components:tencent-guild:component-tencent-guild"
    // }


}



