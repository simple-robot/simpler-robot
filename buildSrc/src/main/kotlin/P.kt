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

@file:Suppress("unused")
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
*/


abstract class SimbotProject {

    abstract val group: String
    abstract val version: String
}


/**
 * Project versions.
 */
sealed class P : SimbotProject() {
    @Suppress("MemberVisibilityCanBePrivate")
    object Simbot {
        init {
            println("System.getProperty(\"isSnapshot\"): ${System.getProperty("isSnapshot")}")
        }
        const val GROUP = "love.forte.simbot"
        const val BOOT_GROUP = "love.forte.simbot.boot"

        val version = Version(
            "3", 0, 0,
            status = preview(12, 0),
            isSnapshot = System.getProperty("isSnapshot")?.equals("true", true) ?: false
        )
 
        val isSnapshot get() = version.isSnapshot

        val VERSION = version.fullVersion(true) // = if (SNAPSHOT) "$REAL_VERSION-SNAPSHOT" else REAL_VERSION

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


}




/**
 * **P**roject **V**ersion。
 */
@Suppress("SpellCheckingInspection")
data class Version(
    /**
     * 主版号
     */
    val major: String,
    /**
     * 次版号
     */
    val minor: Int,
    /**
     * 修订号
     */
    val patch: Int,

    /**
     * 状态号。状态号会追加在 [major].[minor].[patch] 之后，由 `.` 拼接，
     * 变为 [major].[minor].[patch].[PVS.status].[PVS.minor].[PVS.patch].
     *
     * 例如：
     * ```
     * 3.0.0.preview.0.1
     * ```
     *
     */
    val status: PVS? = null,

    /**
     * 是否快照。如果是，将会在版本号结尾处拼接 `-SNAPSHOT`。
     */
    val isSnapshot: Boolean = false
) {
    companion object {
        const val SNAPSHOT_SUFFIX = "-SNAPSHOT"
    }

    /**
     * 没有任何后缀的版本号。
     */
    val standardVersion: String = "$major.$minor.$patch"


    /**
     * 完整的版本号。
     */
    fun fullVersion(checkSnapshot: Boolean): String {
        return buildString {
            append(major).append('.').append(minor).append('.').append(patch)
            if (status != null) {
                append('.').append(status.status).append('.').append(status.minor).append('.').append(status.patch)
            }
            if (checkSnapshot && isSnapshot) {
                append(SNAPSHOT_SUFFIX)
            }
        }
    }

}

/**
 * **P**roject **V**ersion **S**tatus.
 */
@Suppress("SpellCheckingInspection")
data class PVS(
    val status: String,
    /**
     * 次版号
     */
    val minor: Int,
    /**
     * 修订号
     */
    val patch: Int,
) {
    companion object {
        const val PREVIEW_STATUS = "preview"
        const val BETA_STATUS = "beta"
    }
}


internal fun preview(minor: Int, patch: Int) = PVS(PVS.PREVIEW_STATUS, minor, patch)


