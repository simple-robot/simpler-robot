rootProject.name = "simply-robot"


// enableFeaturePreview("VERSION_CATALOGS")
//
// dependencyResolutionManagement {
//     @Suppress("UnstableApiUsage")
//     versionCatalogs {
//         create("libs") {
//             from(files(File(rootProject.projectDir, "libs.versions.toml")))
//         }
//     }
// }

include("simbot-utils:simbot-util-stage-loop2")

// include(":simbot-logger")
// include(":simbot-logger-slf4j-impl")
//
// include(
//     api("api"),
//     // api("logger")
// )
//
// include(core("core"))
//
// include(
//     boot("api"),
//     boot("core-annotation"),
//     boot("core"),
//     boot("core-spring-boot-starter"),
// )
//
// // project test
// include(
//     projectTest("boot"),
//     projectTest("jmh-duration"),
// )
//
// include(
//     componentHttpServer("api")
// )
//
//
// @Suppress("NOTHING_TO_INLINE")
// inline fun api(moduleName: String): String = ":simbot-apis:simbot-$moduleName"
//
// @Suppress("NOTHING_TO_INLINE")
// inline fun core(moduleName: String): String = ":simbot-cores:simbot-$moduleName"
//
// @Suppress("NOTHING_TO_INLINE")
// inline fun boot(moduleName: String): String = ":simbot-boots:simboot-$moduleName"
//
// @Suppress("NOTHING_TO_INLINE")
// inline fun projectTest(moduleName: String): String = ":simbot-project-tests:simbot-project-test-$moduleName"
//
// @Suppress("NOTHING_TO_INLINE")
// inline fun componentHttpServer(moduleName: String): String = ":simbot-components:http-server:simbot-component-http-server-$moduleName"