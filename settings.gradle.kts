
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
rootProject.name = "simply-robot"

enableFeaturePreview("VERSION_CATALOGS")

dependencyResolutionManagement {
    @Suppress("UnstableApiUsage")
    versionCatalogs {
        create("libs") {
            from(files(File(rootProject.projectDir, "libs.versions.toml")))
        }
    }
}

include(
    ":simbot-util-api-requestor-core",
    ":simbot-util-api-requestor-ktor",
    ":simbot-util-stage-loop",
)

include(":simbot-logger")
include(":simbot-logger-slf4j-impl")

include(":simbot-api")
include(":simbot-core")

include(
    ":simboot-api",
    ":simboot-core-annotation",
    ":simboot-core",
    ":simboot-core-spring-boot-starter",
)

// project test
// if not in CI workflows
if (!System.getenv("IS_CI").toBoolean()) {
    include(
        projectTest("boot"),
        projectTest("spring-boot-starter"),
        projectTest("jmh-duration"),
    )

    include(":simbot-component-http-server-api")
}





@Suppress("NOTHING_TO_INLINE")
inline fun api(moduleName: String): String = ":simbot-apis:simbot-$moduleName"

@Suppress("NOTHING_TO_INLINE")
inline fun core(moduleName: String): String = ":simbot-cores:simbot-$moduleName"

@Suppress("NOTHING_TO_INLINE")
inline fun boot(moduleName: String): String = ":simbot-boots:simboot-$moduleName"

@Suppress("NOTHING_TO_INLINE")
inline fun projectTest(moduleName: String): String = ":simbot-project-tests:simbot-project-test-$moduleName"

@Suppress("NOTHING_TO_INLINE")
inline fun componentHttpServer(moduleName: String): String = ":simbot-components:http-server:simbot-component-http-server-$moduleName"

@Suppress("NOTHING_TO_INLINE")
inline fun util(moduleName: String): String = ":simbot-utils:simbot-util-$moduleName"

@Suppress("NOTHING_TO_INLINE")
inline fun utilApiReq(moduleName: String): String = ":simbot-utils:api-requestor:simbot-util-api-requestor-$moduleName"
