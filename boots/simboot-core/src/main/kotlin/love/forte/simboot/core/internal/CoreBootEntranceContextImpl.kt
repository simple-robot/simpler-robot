/*
 *  Copyright (c) 2021-2022 ForteScarlet <https://github.com/ForteScarlet>
 *
 *  本文件是 simply-robot (或称 simple-robot 3.x、simbot 3.x、simbot3) 的一部分。
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

package love.forte.simboot.core.internal

import love.forte.annotationtool.core.*
import love.forte.di.*
import love.forte.di.core.internal.*
import love.forte.simboot.*
import love.forte.simboot.core.*
import love.forte.simboot.core.configuration.*
import love.forte.simboot.factory.*
import love.forte.simboot.interceptor.*
import love.forte.simbot.*
import love.forte.simbot.core.event.*
import love.forte.simbot.event.*
import love.forte.simbot.utils.*
import org.slf4j.*
import java.net.*
import java.nio.file.*
import java.util.*
import kotlin.collections.Collection
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.Set
import kotlin.collections.associate
import kotlin.collections.emptyList
import kotlin.collections.filterValues
import kotlin.collections.forEach
import kotlin.collections.ifEmpty
import kotlin.collections.isEmpty
import kotlin.collections.isNotEmpty
import kotlin.collections.mapNotNull
import kotlin.collections.mutableMapOf
import kotlin.collections.set
import kotlin.collections.toSet
import kotlin.io.path.*
import kotlin.reflect.*

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
    private val applicationClass: KClass<*>?,
    private val context: SimbootEntranceContext
) : CoreBootEntranceContext {
    companion object {
    }

    override val topFunctionScanPackages: Set<String> =
        simbootApplicationAnnotationInstance.topListenerScanPackages.toSet().ifEmpty {
            simbootApplicationAnnotationInstance.scanPackages.ifEmpty {
                if (applicationClass == null) {
                    logger.warn("Application class instance is null, and param 'topListenerScanPackages' of annotation @SimbootApplication is empty.")

                    arrayOf("")
                } else {
                    arrayOf(applicationClass.java.`package`?.name ?: "")
                }
            }.toSet()
        }

    /**
     * bean container factory
     */
    override fun getBeanContainerFactory(): BeanContainerFactory {
        val packages: Array<String> =
            simbootApplicationAnnotationInstance.scanPackages.ifEmpty {
                if (applicationClass == null) {
                    logger.warn("Application class instance is null, and param 'scanPackages' of annotation @SimbootApplication is empty.")
                    //
                    val mainClass = systemProperties("sun.java.command")
                    if (mainClass == null) {
                        logger.warn("Cannot get main class package from system property 'sun.java.command', [scanPackages] will be empty.")
                        arrayOf("")
                    } else {
                        val mainPackage = mainClass.substringBeforeLast('.')
                        logger.warn("Main class package from system property 'sun.java.command' is '$mainPackage' [scanPackages] will use it.")
                        arrayOf(mainPackage)
                    }
                } else {
                    arrayOf(applicationClass.java.`package`?.name ?: "")
                }
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

                logger.info("Size of all global processing interceptors: {}", allProcessingInterceptor.size)
                if (logger.isDebugEnabled) {
                    allListenerInterceptor.forEach {
                        logger.debug("Global processing interceptors(id={}): {}", it.key, it.value)
                    }
                }
                logger.info("Size of all global listener interceptors: {}", allListenerInterceptor.size)
                if (logger.isDebugEnabled) {
                    allListenerInterceptor.forEach {
                        logger.debug("Global listener interceptors(id={}): {}", it.key, it.value)
                    }
                }

                val context =
                    beanContainer.getOrNull(CoreEventListenerManagerContextFactory::class)?.managerCoroutineContext

                interceptors {

                    if (allProcessingInterceptor.isNotEmpty()) {
                        addProcessingInterceptors(allProcessingInterceptor)
                    }


                    if (allListenerInterceptor.isNotEmpty()) {
                        addListenerInterceptors(allListenerInterceptor.filterValues { it !is AnnotatedEventListenerInterceptor }) // 不追加注解拦截器
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
        return ResourcesScanner<BotVerifyInfo>().use {
            it.scan(baseResource)
                .glob(glob)
                .visitJarEntry { entry, _ ->
                    if (!entry.isDirectory) {
                        it.classLoader.getResource(entry.toString())?.asBotVerifyInfo()?.let { url -> sequenceOf(url) }
                            ?: emptySequence()
                    } else emptySequence()
                }
                .visitPath { (path, _) ->
                    sequenceOf(
                        path.asBotVerifyInfo()
                    )
                }
                .toList(false)
        }


    }

    override val args: Array<String>
        get() = context.args

    override val logger: Logger get() = context.logger
}


private fun CoreBootEntranceContext.packagesToClassesGetter(vararg scannerPackages: String): () -> Collection<KClass<*>> {
    if (scannerPackages.isEmpty()) return { emptyList() }

    // scanner.

    return {
        val pathReplace = Regex("[/\\\\]")
        ResourcesScanner<KClass<*>>().use { scanner ->
            for (scanPkg in scannerPackages) {
                val scanPath = scanPkg.replace(".", "/")
                scanner.scan(scanPath)
                scanner.glob("$scanPath**.class")
            }
            scanner.visitJarEntry { entry, _ ->
                val classname = entry.name.replace(pathReplace, ".").substringBeforeLast(".class")
                val loadClass = runCatching {
                    scanner.classLoader.loadClass(classname)
                }.getOrElse { e ->
                    logger.warn("Class [{}] failed to load and will be skipped.", classname)
                    if (logger.isDebugEnabled) {
                        logger.debug("Reason for failure: $e", e)
                    }
                    // just warn
                    // throw SimbotIllegalStateException("Class load failed: $classname", e)
                    null
                }
                if (loadClass != null) {
                    sequenceOf(loadClass.kotlin)
                } else {
                    emptySequence()
                }
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