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

@file:Suppress("unused")

import org.gradle.api.artifacts.DependencyConstraint
import kotlin.reflect.KClass


val publishNeed = setOf("annotation", "core", "api")



abstract class Dep(val group: String?, val id: String, val version: String?) {
    abstract val isAbsolute: Boolean
    override fun toString(): String = "Dep($notation)"
    open fun constraints(constraints: DependencyConstraint): DependencyConstraint {
        return constraints
    }
}

val Dep.notation
    get() = buildString {
        if (group != null) append(group).append(':')
        append(id)
        if (version != null) append(':').append(version)
    }


sealed class V(group: String?, id: String, version: String?) : Dep(group, id, version) {
    override val isAbsolute: Boolean get() = true

    companion object {
        @Suppress("ObjectPropertyName")
        val dependencies: Set<Dep> by lazy {
            V::class.all().toSet()
        }
    }

    sealed class Simbot(group: String = P.Simbot.GROUP, id: String, version: String = VERSION) : V(group, id, version) {
        companion object {
            const val VERSION = P.Simbot.VERSION
        }

        object Api : Simbot(id = "api")
        object Core : Simbot(id = "core")

    }

    // org.jetbrains:annotations:23.0.0
    sealed class Jetbrains(group: String = "org.jetbrains", id: String, version: String): V(group, id, version) {
        object Annotations: Jetbrains(id = "annotations", version = "23.0.0")

    }


    /**
     * Kotlin相关依赖项
     */
    sealed class Kotlin(id: String) :
        V("org.jetbrains.kotlin", "kotlin-$id", VERSION) {
        companion object {
            const val VERSION = "1.6.0"
        }

        sealed class Stdlib(id: String) : Kotlin(id = "stdlib-$id") {
            object Common : Stdlib("common")
        }
        object GradlePlugin : Kotlin("gradle-plugin")
        object CompilerEmbeddable : Kotlin("compiler-embeddable")
        object Reflect : Kotlin("reflect")
        sealed class Test(id: String) : Kotlin("test-$id") {
            object Common : Test("common")
            object Junit : Test("junit")
            object Junit5 : Test("junit5")
            object Js : Test("js")
            object AnnotationsCommon : Test("annotations-common")
        }
    }

    /**
     * Kotlinx 相关依赖项
     */
    sealed class Kotlinx(id: String, version: String?, override val isAbsolute: Boolean) :
        V("org.jetbrains.kotlinx", "kotlinx-$id", version) {


        // https://github.com/Kotlin/kotlinx.coroutines
        sealed class Coroutines(id: String) : Kotlinx(id = "coroutines-$id", VERSION, true) {
            companion object {
                const val VERSION = "1.5.2-native-mt"
            }

            object J8 : Coroutines("jdk8")

            // https://github.com/Kotlin/kotlinx.coroutines/blob/master/kotlinx-coroutines-core/README.md
            object Core : Coroutines("core") {
                object Jvm : Coroutines("core-jvm")
                object Js : Coroutines("core-js")
            }
            object Debug : Coroutines("debug")
            object Test : Coroutines("test")
            // =======
            //   https://github.com/Kotlin/kotlinx.coroutines/blob/master/reactive/README.md
            object Reactive : Coroutines("reactive")
            object Reactor : Coroutines("reactor")
            object Rx2 : Coroutines("rx2")
            object Rx3 : Coroutines("rx3")
            // =======

            // https://github.com/Kotlin/kotlinx.coroutines/blob/master/ui/README.md
            sealed class UI(id: String) : Coroutines(id) {
                // kotlinx-coroutines-android -- Dispatchers.Main context for Android applications.
                // kotlinx-coroutines-javafx -- Dispatchers.JavaFx context for JavaFX UI applications.
                // kotlinx-coroutines-swing -- Dispatchers.Swing context for Swing UI applications.

                object Android : UI("android")
                object Javafx : UI("javafx")
                object Swing : UI("swing")
            }

            // https://github.com/Kotlin/kotlinx.coroutines/blob/master/integration/README.md
            sealed class Integration(id: String) : Coroutines(id) {
                // kotlinx-coroutines-jdk8 -- integration with JDK8 CompletableFuture (Android API level 24).
                // kotlinx-coroutines-guava -- integration with Guava ListenableFuture.
                // kotlinx-coroutines-slf4j -- integration with SLF4J MDC.
                // kotlinx-coroutines-play-services -- integration with Google Play Services Tasks API.
                object Jdk8 : Integration("jdk8")
                object Guava : Integration("guava")
                object Slf4j : Integration("slf4j")
                object PlayServices : Integration("play-services")
            }



        }

        // https://github.com/Kotlin/kotlinx.serialization
        sealed class Serialization(id: String) : Kotlinx(id = "serialization-$id", VERSION, true) {
            companion object {
                const val VERSION = "1.3.1"
            }

            object Core : Serialization("core")
            object Json : Serialization("json")
            object Hocon : Serialization("hocon")
            object Protobuf : Serialization("protobuf")
            object Cbor : Serialization("cbor")
            object Properties : Serialization("properties")
            object Yaml : V("com.charleskorn.kaml", "kaml", "0.37.0")

        }

    }

    // Ktor相关
    sealed class Ktor(id: String) : V(group = "io.ktor", id = "ktor-$id", VERSION) {
        companion object {
            // NoSuchMethodError: java.nio.ByteBuffer.limit(I)Ljava/nio/ByteBuffer;
            // https://youtrack.jetbrains.com/issue/KTOR-3358
            const val VERSION = "1.6.4" // 1.6.6
        }

        // server
        sealed class Server(id: String) : Ktor(id = "server-$id") {
            object Core : Server("core")
            object Netty : Server("netty")
            object Jetty : Server("jetty")
            object Tomcat : Server("tomcat")
            object CIO : Server("cio")
        }

        // client
        sealed class Client(id: String): Ktor(id = "client-$id") {
            object Serialization : Client("serialization")
            object Auth : Client("auth")
            object Websockets : Client("websockets")
            sealed class Jvm(id: String): Client(id) {
                object Core : Jvm("core")
                object Apache : Jvm("apache")
                object Java : Jvm("java")
                object Jetty : Jvm("jetty")
                object CIO : Jvm("cio")
                object OkHttp : Jvm("okhttp")
            }
        }


    }

    /**
     * Slf4j 相关
     */
    sealed class Slf4j(id: String) : V("org.slf4j", id = "slf4j-$id", version = VERSION) {
        override val isAbsolute: Boolean get() = true

        companion object {
            const val VERSION = "1.7.9"
        }

        object Api : Slf4j("api")
    }

    sealed class Log4j(id: String) : V("org.apache.logging.log4j", id = "log4j-$id", version = VERSION) {
        companion object {
            const val VERSION = "2.14.1"
        }
        object Api : Log4j("api")
        object Core : Log4j("core")
        object Slf4jImpl : Log4j("slf4j-impl")
    }

    /**
     * Okio https://square.github.io/okio/#releases
     */
    object Okio : V("com.squareup.okio", "okio", "3.0.0")





}

fun <T : Any> KClass<T>.all(): Sequence<T> {
    if (!this.isSealed) return this.objectInstance?.let { sequenceOf(it) } ?: emptySequence()
    return this.sealedSubclasses.asSequence().flatMap { t -> t.all() }

}
