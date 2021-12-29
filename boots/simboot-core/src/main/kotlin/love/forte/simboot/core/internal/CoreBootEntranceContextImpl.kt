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
import love.forte.di.all
import love.forte.di.core.internal.AnnotationGetter
import love.forte.simboot.Configuration
import love.forte.simboot.SimbootEntranceContext
import love.forte.simboot.SimbotPropertyResources
import love.forte.simboot.core.CoreBootEntranceContext
import love.forte.simboot.core.SimbootApplication
import love.forte.simboot.core.configuration.CoreEventListenerManagerContextFactory
import love.forte.simboot.factory.BeanContainerFactory
import love.forte.simboot.factory.ConfigurationFactory
import love.forte.simboot.factory.EventListenerManagerFactory
import love.forte.simboot.interceptor.AnnotatedEventListenerInterceptor
import love.forte.simbot.BotVerifyInfo
import love.forte.simbot.ID
import love.forte.simbot.SimbotIllegalStateException
import love.forte.simbot.asBotVerifyInfo
import love.forte.simbot.core.event.coreListenerManager
import love.forte.simbot.event.EventListenerInterceptor
import love.forte.simbot.event.EventListenerManager
import love.forte.simbot.event.EventProcessingInterceptor
import org.slf4j.Logger
import java.net.URL
import java.nio.file.Path
import java.util.*
import kotlin.io.path.bufferedReader
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
    }

    override val topFunctionScanPackages: Set<String> =
        simbootApplicationAnnotationInstance.topListenerScanPackages.toSet().ifEmpty {
            simbootApplicationAnnotationInstance.scanPackages.ifEmpty {
                arrayOf(applicationClass.java.`package`?.name ?: "")
            }.toSet()
        }

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

                val allListenerInterceptor: Map<ID, EventListenerInterceptor> =
                    beanContainer.all<EventListenerInterceptor>()
                        .associate { it.ID to beanContainer[it, EventListenerInterceptor::class] }

                val allProcessingInterceptor: Map<ID, EventProcessingInterceptor> =
                    beanContainer.all<EventProcessingInterceptor>()
                        .associate { it.ID to beanContainer[it, EventProcessingInterceptor::class] }


                // val allListenerInterceptor = beanContainer.allInstance<EventListenerInterceptor>()
                // val allProcessingInterceptor = beanContainer.allInstance<EventProcessingInterceptor>()
                // CoreEventListenerManagerContextFactory
                val context =
                    beanContainer.getOrNull(CoreEventListenerManagerContextFactory::class)?.managerCoroutineContext

                interceptors {
                    if (allListenerInterceptor.isNotEmpty()) {
                        addListenerInterceptors(allListenerInterceptor.filterValues { it !is AnnotatedEventListenerInterceptor }) // 不追加注解拦截器
                    }
                    if (allProcessingInterceptor.isNotEmpty()) {
                        addProcessingInterceptors(allProcessingInterceptor)
                    }
                    if (context != null) {
                        coroutineContext = context
                    }
                }

            }
    }

    override fun getAllBotInfos(
        configuration: Configuration,
        beanContainer: BeanContainer
    ): List<BotVerifyInfo> {

        val baseResource: String = configuration.getString("simbot.core.bots.resource") ?: "simbot-bots"
        // val botResourceGlob = configuration.getString("simbot.core.bots.path") ?: "simbot-bots/**.bot"

        val glob = if (baseResource.endsWith("/")) "$baseResource**.bot" else "$baseResource/**.bot"

        logger.debug("Scan bots base resource path: {}, glob: {}", baseResource, glob)

        // all bots verify info
        return ResourcesScanner<BotVerifyInfo>()
            .scan(baseResource)
            .glob(glob)
            .visitJarEntry { _, url ->
                sequenceOf(
                    url.asBotVerifyInfo()
                )
            }
            .visitPath { (path, _) ->
                sequenceOf(
                    path.asBotVerifyInfo()
                )
            }
            .toList(false)


    }

    override val args: Array<String>
        get() = context.args

    override val logger: Logger get() = context.logger
}


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
                    val classname = entry.name.replace(pathReplace, ".").substringBeforeLast(".class")
                    val loadClass = runCatching {
                        scanner.classLoader.loadClass(classname)
                    }.getOrElse { e -> throw SimbotIllegalStateException("Class load filed: $classname", e) }
                    sequenceOf(loadClass.kotlin)
                }
                .visitPath { (_, r) ->
                    // '/Xxx.class'
                    val classname = r.replace(pathReplace, ".").substringBeforeLast(".class").let {
                        if (it.startsWith(".")) it.substring(1) else it
                    }
                    val loadClass = runCatching {
                        scanner.classLoader.loadClass(classname)
                    }.getOrElse { e -> throw SimbotIllegalStateException("Class load filed: $classname", e) }
                    sequenceOf(loadClass.kotlin)
                }
                .collectSequence(true)
                /* Packages and file facades are not yet supported in Kotlin reflection. Meanwhile please use Java reflection to inspect this class: class ResourceGetTestKt */
                .filter { k -> runCatching { k.visibility == KVisibility.PUBLIC }.getOrDefault(false) }
                .toList()
        }
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