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

package love.forte.simboot.core.internal

import love.forte.annotationtool.core.KAnnotationTool
import love.forte.di.BeanContainer
import love.forte.di.allInstance
import love.forte.di.core.internal.AnnotationGetter
import love.forte.simboot.Configuration
import love.forte.simboot.SimbootEntranceContext
import love.forte.simboot.SimbotPropertyResources
import love.forte.simboot.core.CoreBootEntranceContext
import love.forte.simboot.core.SimbootApplication
import love.forte.simboot.factory.BeanContainerFactory
import love.forte.simboot.factory.ConfigurationFactory
import love.forte.simboot.factory.EventListenerManagerFactory
import love.forte.simbot.core.event.coreListenerManager
import love.forte.simbot.event.EventListenerInterceptor
import love.forte.simbot.event.EventListenerManager
import love.forte.simbot.event.EventProcessingInterceptor
import org.slf4j.Logger
import java.net.URL
import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes
import java.util.*
import kotlin.io.path.Path
import kotlin.io.path.bufferedReader
import kotlin.io.path.extension
import kotlin.io.path.isRegularFile
import kotlin.reflect.KAnnotatedElement
import kotlin.reflect.KClass
import kotlin.reflect.KVisibility
import kotlin.reflect.cast

/**
 * 扫描并加载所有在 `META-INF/simbot.factories` 中 key为 `simbot.includes` 的元素。
 *
 *
 */
internal const val INCLUDES_KEY = "simbot.includes"

/**
 *
 * 通过 class 解析得到的 [CoreBootEntranceContext]
 *
 * @author ForteScarlet
 */
internal class CoreBootEntranceContextImpl(
    private val simbootApplicationAnnotationInstance: SimbootApplication,
    private val applicationClass: KClass<*>,
    private val context: SimbootEntranceContext
) : CoreBootEntranceContext {
    companion object {
        internal val allowExtensions = setOf("bot", "properties")

    }

    override val topFunctionScanPackages: Set<String> =
        simbootApplicationAnnotationInstance.topListenerScanPackages.toSet()

    /**
     * bean container factory
     */
    override fun getBeanContainerFactory(): BeanContainerFactory {
        val packages =
            simbootApplicationAnnotationInstance.scanPackages.ifEmpty {
                arrayOf(applicationClass.java.`package`?.name ?: "")
            }

        val includes = SimbotPropertyResources.findKey(INCLUDES_KEY).values.toSet()

        return CoreForteDIBeanContainerFactory(
            AnnotationToolGetter(KAnnotationTool()),
            packagesToClassesGetter(*packages),
            includes
        )
    }

    override fun getConfigurationFactory(): ConfigurationFactory {
        return ConfigurationFactory { EmptyConfiguration } // TODO
    }


    override fun getListenerManager(beanContainer: BeanContainer): EventListenerManager {

        return beanContainer.getOrNull(EventListenerManagerFactory::class)
            ?.getEventListenerManager()
            ?: coreListenerManager {

                // 所有的拦截器
                val allListenerInterceptor = beanContainer.allInstance<EventListenerInterceptor>()
                val allProcessingInterceptor = beanContainer.allInstance<EventProcessingInterceptor>()

                interceptors {
                    if (allListenerInterceptor.isNotEmpty()) {
                        addListenerInterceptors(allListenerInterceptor)
                    }
                    if (allProcessingInterceptor.isNotEmpty()) {
                        addProcessingInterceptors(allProcessingInterceptor)
                    }
                }

            }
    }

    override fun getAllBotInfos(
        configuration: Configuration,
        beanContainer: BeanContainer
    ): Map<String, List<Map<String, String>>> {
        val configGlob =
            (configuration.getString("simbot.bot.configPath") ?: configuration.getString("simbot.bot.config-path"))
                ?.removePrefix("classpath:")
                ?.removePrefix("/")
                ?.let { s -> if (s.endsWith(".bot")) s else "$s.bot" }
                ?: "simbot-bots/**.bot"

        // from file
        // matcher
        val pathMatcher = FileSystems.getDefault().getPathMatcher(configGlob)

        // find file first
        val rootPath = configGlob.substringBefore("*")
        val path = Path(rootPath)
        val list = mutableSetOf<Path>()
        Files.walkFileTree(path, SimpleVisiter(allowExtensions, pathMatcher, list))


        // from resources
        return ResourcesScanner<Map<String, String>>(CoreBootEntranceContextImpl::class.java.classLoader).use { scanner ->
            scanner.scan("").glob(configGlob)
                .visitPath { (p, _) ->
                    if (p.isRegularFile()) {
                        if (p.extension == "bot") {
                            return@visitPath sequenceOf(p.readProperties().stringMap)
                        }
                    }
                    emptySequence()
                }.visitJarEntry { _, url ->
                    sequenceOf(url.readProperties().stringMap)
                }
        }.collectSequence(false).groupBy {
            it["component"] ?: ""
        }

    }

    override val args: Array<String>
        get() = context.args

    override val logger: Logger get() = context.logger
}


// fun fromResource(): List<Properties> {
//     return runCatching {
//         val loader = Thread.currentThread().contextClassLoader
//         ResourcesScanner(loader).scan(BotVerifyInfoConfiguration.PATH_DIR) { uri ->
//             uri.toASCIIString().endsWith("bot")
//         }.collection.map { uri ->
//             LOGGER.debug("Bot verify info by {}", uri.toASCIIString())
//             Properties().apply {
//                 val asciiString = uri.toASCIIString()
//                 loader.getResource(asciiString)?.newInputStream()?.reader(Charsets.UTF_8)?.use(::load)
//                     ?: kotlin.runCatching {
//                         LOGGER.debug("Uri stream null: {}, try use url", asciiString)
//                         uri.toURL().useJarBufferedReader(Charsets.UTF_8, ::load)
//                         // uri.toURL().openStream().reader(Charsets.UTF_8).use(::load)
//                     }.getOrElse { e1 ->
//                         throw IllegalStateException("Uri stream read failed: $uri", e1)
//                     }
//
//
//             }
//         }
//     }.getOrElse { e ->
//         LOGGER.debug("Cannot read bots configure by resource, skip. info : {}", e.localizedMessage)
//         LOGGER.debug("Details: $e", e)
//         emptyList()
//     }
// }


private fun packagesToClassesGetter(vararg scannerPackages: String): () -> Collection<KClass<*>> {
    if (scannerPackages.isEmpty()) return { emptyList() }

    // scanner.

    return {
        val pathReplace = Regex("[/\\\\]")
        ResourcesScanner<KClass<*>>().use { scanner ->
            scanner.scan("")
                .also {
                    for (scanPkg in scannerPackages) {
                        it.glob(scanPkg.replace(".", "/") + "**.class")
                    }
                }
                .visitJarEntry { entry, _ ->
                    val classname = entry.name.replace(pathReplace, ".").substringBefore(".class")
                    sequenceOf(scanner.classLoader.loadClass(classname).kotlin)
                }
                .visitPath { (_, r) ->
                    // '/Xxx.class'
                    val classname = r.replace(pathReplace, ".").substringBefore(".class").let {
                        if (it.startsWith(".")) it.substring(1) else it
                    }
                    sequenceOf(scanner.classLoader.loadClass(classname).kotlin)
                }
                .collectSequence(true)
                /* Packages and file facades are not yet supported in Kotlin reflection. Meanwhile please use Java reflection to inspect this class: class ResourceGetTestKt */
                .filter { k -> runCatching { k.visibility == KVisibility.PUBLIC }.getOrDefault(false)  }
                .toList()
        }
    }
}

private class SimpleVisiter(
    private val exSet: Set<String>,
    private val matcher: PathMatcher,
    private val list: MutableCollection<Path>
) :
    SimpleFileVisitor<Path>() {
    override fun visitFile(file: Path, attrs: BasicFileAttributes?): FileVisitResult {
        if (matcher.matches(file) && file.extension in exSet) {
            list.add(file)
        }

        return FileVisitResult.CONTINUE
    }
}


private class AnnotationToolGetter(private val tool: KAnnotationTool) : AnnotationGetter {
    override fun <T : Annotation> containsAnnotation(element: KAnnotatedElement, annotationType: KClass<T>): Boolean {
        return tool.getAnnotation(element, annotationType) != null
    }

    override fun <R : Any> getAnnotationProperty(
        element: KAnnotatedElement,
        annotationType: KClass<out Annotation>,
        name: String,
        propertyType: KClass<R>
    ): R? {
        val annotation = tool.getAnnotation(element, annotationType) ?: return null
        val values = tool.getAnnotationValues(annotation)
        return values[name]?.let { propertyType.cast(it) }
    }

    override fun <R : Any> getAnnotationsProperties(
        element: KAnnotatedElement,
        annotationType: KClass<out Annotation>,
        name: String,
        propertyType: KClass<R>
    ): List<R> {
        return tool.getAnnotations(element, annotationType).mapNotNull {
            tool.getAnnotationValues(it)[name]?.let { v -> propertyType.cast(v) }
        }
    }
}


private fun Path.readProperties(): Properties {
    return Properties().also { p ->
        bufferedReader().use(p::load)
    }
}

private fun URL.readProperties(): Properties {
    return Properties().also { p ->
        openStream().bufferedReader().use(p::load)
    }
}

private val Properties.stringMap: Map<String, String>
    get() {
        return mutableMapOf<String, String>().also { map ->
            stringPropertyNames().forEach { name -> map[name] = getProperty(name) }
        }
    }


private object EmptyConfiguration : Configuration {
    override fun getString(key: String): String? {
        return null
    }
}