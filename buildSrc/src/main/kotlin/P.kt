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
        const val VERSION = "3.0.0.preview.1.1"
    }

    sealed class ForteDI(id: String) : Dep(GROUP, "di-$id", VERSION) {
        companion object {
            const val GROUP = "love.forte.di"
            const val VERSION = "0.0.1"
        }
        object Api : ForteDI("api")
        object Core : ForteDI("core")
        object Spring : ForteDI("spring")

    }

    // "love.forte.annotation-tool:api:0.6.1"
    sealed class AnnotationTool(id: String) : Dep(GROUP, id, VERSION) {
        companion object {
            const val GROUP = "love.forte.annotation-tool"
            const val VERSION = "0.6.1"
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



