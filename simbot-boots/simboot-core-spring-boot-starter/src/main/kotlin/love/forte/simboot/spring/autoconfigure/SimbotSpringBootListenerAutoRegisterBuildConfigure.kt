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

package love.forte.simboot.spring.autoconfigure

import love.forte.annotationtool.core.KAnnotationTool
import love.forte.annotationtool.core.getAnnotation
import love.forte.di.Api4J
import love.forte.di.BeanContainer
import love.forte.di.all
import love.forte.di.allInstance
import love.forte.simboot.annotation.Binder
import love.forte.simboot.annotation.Listener
import love.forte.simboot.annotation.scopeIfDefault
import love.forte.simboot.core.binder.BinderManager
import love.forte.simboot.core.binder.CoreBinderManager
import love.forte.simboot.core.listener.FunctionalListenerProcessContext
import love.forte.simboot.core.listener.KFunctionListenerProcessor
import love.forte.simboot.core.utils.isTopClass
import love.forte.simboot.core.utils.sign
import love.forte.simboot.listener.ParameterBinderFactory
import love.forte.simboot.spring.autoconfigure.application.SpringBootApplicationBuilder
import love.forte.simboot.spring.autoconfigure.application.SpringBootApplicationConfiguration
import love.forte.simboot.spring.autoconfigure.utils.Quadruple
import love.forte.simbot.InternalSimbotApi
import love.forte.simbot.LoggerFactory
import love.forte.simbot.SimbotIllegalStateException
import love.forte.simbot.core.application.listeners
import love.forte.simbot.event.EventListener
import love.forte.simbot.event.EventListenerBuilder
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.core.io.support.PathMatchingResourcePatternResolver
import org.springframework.core.type.AnnotationMetadata
import org.springframework.core.type.classreading.SimpleMetadataReaderFactory
import javax.inject.Named
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.KVisibility
import kotlin.reflect.full.*
import kotlin.reflect.jvm.kotlinFunction


/**
 *
 * 自动扫描、解析、注册当前SpringBoot依赖环境中存在的所有 [EventListener] 的配置器。
 *
 * 这其中包括了标记 [Listener] 注解的函数以及直接对 [EventListener] 进行实现的实现类。
 *
 *
 * @author ForteScarlet
 */
public open class SimbotSpringBootListenerAutoRegisterBuildConfigure : SimbotSpringBootApplicationBuildConfigure,
    ApplicationContextAware {
    private lateinit var beanContainer: BeanContainer
    
    
    override fun setApplicationContext(applicationContext: ApplicationContext) {
        // _applicationContext = applicationContext
        beanContainer = SpringBeanContainer(applicationContext)
    }
    
    override fun SpringBootApplicationBuilder.config(configuration: SpringBootApplicationConfiguration) {
        val tool = KAnnotationTool()
        val listenerProcessor = KFunctionListenerProcessor(tool)
        logger.debug("Resolving binders...")
        val binderManager = resolveBinderManager(tool, configuration)
        logger.debug(
            "The size of resolved binders is {} (normal: {}, global: {})",
            binderManager.normalBinderFactorySize + binderManager.globalBinderFactorySize,
            binderManager.normalBinderFactorySize,
            binderManager.globalBinderFactorySize
        )
        
        logger.debug("Resolving listeners...")
        val listeners = resolveListeners(tool, listenerProcessor, binderManager, configuration)
        logger.debug("The size of resolved listeners is {}", listeners.size)
        
        listeners {
            listeners.forEach { listener ->
                logger.debug("Registing normal listener {}", listener)
                listener(listener)
            }
        }
        logger.debug("All {} listeners registered.", listeners.size)
    }
    
    
    @OptIn(Api4J::class)
    private fun resolveBinderManager(
        tool: KAnnotationTool,
        configuration: SpringBootApplicationConfiguration,
    ): BinderManager {
        // val applicationContext = _applicationContext
        
        val globalBinderFactories: MutableList<ParameterBinderFactory> = mutableListOf()
        val idBinderFactories: MutableMap<String, ParameterBinderFactory> = mutableMapOf()
        
        val binderManager = CoreBinderManager(globalBinderFactories, idBinderFactories)
        
        // region instance binders
        val binderInstanceNames = beanContainer.all<ParameterBinderFactory>()
        
        
        binderInstanceNames.forEach { name ->
            // annotation
            val type = kotlin.runCatching { beanContainer.getTypeOrNull(name) }.getOrNull() ?: return@forEach
            val binderAnnotation = tool.getAnnotation<Binder>(type)
            val id = binderAnnotation?.value?.firstOrNull() ?: name
            
            fun global() {
                val globalBinderFactory = beanContainer[name, ParameterBinderFactory::class.java]
                globalBinderFactories.add(globalBinderFactory)
            }
            
            fun specify() {
                val specifyBinderFactory = beanContainer[name, ParameterBinderFactory::class.java]
                idBinderFactories.merge(id, specifyBinderFactory) { old, curr ->
                    throw SimbotIllegalStateException("Duplicate binder factory id [$id]: old [$old] vs current [$curr]")
                }
            }
            
            if (binderAnnotation == null) {
                global()
            } else {
                val scope = binderAnnotation.scopeIfDefault { Binder.Scope.SPECIFY }
                if (scope == Binder.Scope.CURRENT) {
                    // 通过类直接实现的BinderFactory的scope不能为 CURRENT
                    throw SimbotIllegalStateException("The scope of the BinderFactory directly implemented by the class cannot be CURRENT, but $binderAnnotation of type [$type] named [$name]")
                }
                
                if (scope == Binder.Scope.GLOBAL) {
                    global()
                } else {
                    specify()
                }
            }
        }
        // endregion
        
        // region functional binders
        beanContainer.all.asSequence().flatMap { name ->
            val kClass =
                kotlin.runCatching { beanContainer.getTypeOrNull(name) }.getOrNull() ?: return@flatMap emptySequence()
            
            kClass.allFunctions.asSequence().mapNotNull { function ->
                val binderAnnotation = tool.getAnnotation<Binder>(function) ?: return@mapNotNull null
                // skip if scope == CURRENT.
                if (binderAnnotation.scope == Binder.Scope.CURRENT) {
                    return@mapNotNull null
                }
                
                Quadruple(name, kClass, function, binderAnnotation)
            }
        }.forEach { (name, _, function, binder) ->
            // not current
            val scope = binder.scope
            val id = binder.value.firstOrNull()
            
            
            fun global() {
                globalBinderFactories.add(binderManager.resolveFunctionToBinderFactory(function) {
                    beanContainer[name]
                })
            }
            
            fun specify(id: String) {
                val binderFactory = binderManager.resolveFunctionToBinderFactory(function) {
                    beanContainer[name]
                }
                idBinderFactories.merge(id, binderFactory) { old, curr ->
                    throw SimbotIllegalStateException("Duplicate binder factory id [$id]: old [$old] vs current [$curr]")
                }
            }
            
            
            when (scope) {
                Binder.Scope.DEFAULT -> {
                    if (id != null) {
                        specify(id)
                    } else {
                        global()
                    }
                }
                Binder.Scope.SPECIFY -> {
                    if (id == null) {
                        throw SimbotIllegalStateException("The scope of binder [$binder] on function [$function] is SPECIFY, but the required property id (Binder.value) is null.")
                    }
                    specify(id)
                }
                else -> {
                    // is global
                    global()
                }
            }
        }
        // endregion
        
        val topBinderPackages = configuration.topLevelBinderScanPackage
        if (topBinderPackages.isNotEmpty()) {
            logger.debug("Resolving top-level function binder in {}", topBinderPackages)
            resolveTopLevelManagerTo(
                tool,
                configuration.classLoader,
                configuration.topLevelBinderScanPackage,
                globalBinderFactories,
                idBinderFactories,
                binderManager
            )
        } else {
            logger.debug("Top-level binder package scan target is empty.")
        }
        
        
        return binderManager
    }
    
    
    private fun resolveTopLevelManagerTo(
        tool: KAnnotationTool,
        classLoader: ClassLoader,
        packages: List<String>,
        globalBinderFactories: MutableList<ParameterBinderFactory>,
        idBinderFactories: MutableMap<String, ParameterBinderFactory>,
        binderManager: BinderManager,
    ) {
        val globs = packages.mapTo(mutableSetOf()) { it.replace(".", "/") + "/**/*.class" }
        logger.debug("Scanning top-level binders in {}", globs)
        
        topFunctions(classLoader, globs).mapNotNull { (metadata, kc, func) ->
            val binderAnnotation = tool.getAnnotation<Binder>(func) ?: return@mapNotNull null
            
            
            Quadruple(metadata, kc, func, binderAnnotation)
        }.forEach { (_, _, func, annotation) ->
            val scope = annotation.scope
            if (scope == Binder.Scope.CURRENT) {
                throw SimbotIllegalStateException("The binder scope of top-level binder function cannot be CURRENT, but $annotation")
            }
            val id = annotation.value.firstOrNull()
            
            val binderFactory = binderManager.resolveFunctionToBinderFactory(func) { null }
            when (scope) {
                Binder.Scope.SPECIFY -> {
                    if (id == null) {
                        throw SimbotIllegalStateException("")
                    }
                    idBinderFactories.merge(id, binderFactory) { old, curr ->
                        throw SimbotIllegalStateException("Duplicate binder ID [$id]: old [$old] vs current [$curr]")
                    }
                }
                else -> {
                    // global
                    globalBinderFactories.add(binderFactory)
                }
            }
            
            
        }
    }
    
    
    private fun resolveListeners(
        tool: KAnnotationTool,
        listenerProcessor: KFunctionListenerProcessor,
        binderManager: BinderManager,
        configuration: SpringBootApplicationConfiguration,
    ): List<EventListener> {
        
        // find instances for init
        val instances = beanContainer.allInstance<EventListener>()
        val listeners = instances.toMutableList()
        
        // find EventBuilders
        val builders = beanContainer.allInstance<EventListenerBuilder>()
        listeners.addAll(builders.map { it.build() })
        
        // scan functions
        beanContainer.all.asSequence().mapNotNull { name ->
            val kClass = kotlin.runCatching { beanContainer.getTypeOrNull(name) }.getOrElse {
                logger.debug("Cannot resolve bean type named {} to kotlin, skip it.", name)
                return@mapNotNull null
            }
            
            name to kClass
        }.flatMap { (name, type) ->
            type?.allDeclaredFunctions?.asSequence()?.map { function -> Triple(name, type, function) }
                ?: emptySequence()
        }.mapNotNullTo(listeners) { (name, _, function) ->
            // check @Listener
            val listenerAnnotation = tool.getAnnotation<Listener>(function) ?: return@mapNotNullTo null
            
            if (function.visibility != KVisibility.PUBLIC) {
                logger.warn(
                    "Function [{}] is annotated with @Listener, so visibility of it must be PUBLIC, but is {}",
                    function,
                    function.visibility
                )
                return@mapNotNullTo null
            }
            
            if ((function.returnType.classifier as? KClass<*>?)?.isSubclassOf(EventListenerBuilder::class) == true) {
                logger.trace("Function [{}]'s return type is subclass of EventListenerBuilder, skip.", function)
                return@mapNotNullTo null
            }
            
            if ((function.returnType.classifier as? KClass<*>?)?.isSubclassOf(EventListener::class) == true) {
                logger.trace("Function [{}]'s return type is subclass of EventListener, skip.", function)
                return@mapNotNullTo null
            }
            
            val functionSign = function.sign()
            val listenerId = listenerAnnotation.id.ifEmpty { "$name#$functionSign" }
            
            
            val resolvedListener = listenerProcessor.process(
                FunctionalListenerProcessContext(
                    id = listenerId,
                    function = function,
                    priority = listenerAnnotation.priority,
                    isAsync = listenerAnnotation.async,
                    binderManager = binderManager,
                    beanContainer = beanContainer,
                )
            )
            
            logger.debug("Resolved listener [{}] by processor [{}]", resolvedListener, listenerProcessor)
            
            resolvedListener
        }
        
        val topLevelPackages = configuration.topLevelListenerScanPackage
        if (topLevelPackages.isNotEmpty()) {
            resolveTopLevelListenersTo(
                tool,
                listenerProcessor,
                binderManager,
                configuration.classLoader,
                configuration.topLevelListenerScanPackage,
                listeners
            )
        } else {
            logger.debug("Top-level listener function scan target is empty.")
        }
        
        
        return listeners
    }
    
    
    private fun resolveTopLevelListenersTo(
        tool: KAnnotationTool,
        listenerProcessor: KFunctionListenerProcessor,
        binderManager: BinderManager,
        classLoader: ClassLoader,
        packages: List<String>,
        listeners: MutableList<EventListener>,
    ) {
        val globs = packages.mapTo(mutableSetOf()) { it.replace(".", "/") + "/**/*.class" }
        logger.debug("Scanning top-level listeners in {}", globs)
        
        topFunctions(classLoader, globs).mapNotNull { (metadata, kc, func) ->
            val listenerAnnotation = tool.getAnnotation<Listener>(func) ?: return@mapNotNull null
            
            Quadruple(metadata, kc, func, listenerAnnotation)
        }.mapNotNullTo(listeners) { (_, _, func, annotation) ->
            kotlin.runCatching {
                val returnType = func.returnType.classifier as? KClass<*>?
                val resolvedListener: EventListener? = when {
                    returnType?.isSubclassOf(EventListener::class) == true || returnType?.isSubclassOf(
                        EventListenerBuilder::class
                    ) == true -> {
                        val callParameters = func.parameters.associateWith { beanContainer.getByKParameter(it) }
                        when (val result = func.callBy(callParameters)) {
                            is EventListenerBuilder -> {
                                if (result.id.isEmpty()) {
                                    // 生成id
                                    result.id = annotation.id.ifEmpty { func.sign() }
                                }
                                result.build()
                            }
                            is EventListener -> result
                            else -> null // print log?
                        }
                    }
                    else -> {
                        val id = annotation.id.ifEmpty { "\$TOP#${func.sign()}" }
                        val processedListener = listenerProcessor.process(
                            FunctionalListenerProcessContext(
                                id = id,
                                function = func,
                                priority = annotation.priority,
                                isAsync = annotation.async,
                                binderManager = binderManager,
                                beanContainer = beanContainer,
                            )
                        )
                        
                        logger.debug(
                            "Resolved top-level listener: [{}] by processor [{}]", processedListener, listenerProcessor
                        )
                        processedListener
                    }
                }
                
                resolvedListener
            }.getOrNull()
        }
    }
    
    
    private companion object {
        private val logger = LoggerFactory.getLogger(SimbotSpringBootListenerAutoRegisterBuildConfigure::class)
    }
}


private inline val KClass<*>.allFunctions: List<KFunction<*>>
    get() = kotlin.runCatching {
        memberFunctions + memberExtensionFunctions
    }.getOrDefault(emptyList())

private inline val KClass<*>.allDeclaredFunctions: List<KFunction<*>>
    get() = kotlin.runCatching {
        declaredMemberFunctions + declaredMemberExtensionFunctions
    }.getOrDefault(emptyList())


@OptIn(InternalSimbotApi::class)
private fun topFunctions(
    classLoader: ClassLoader,
    globs: Collection<String>,
): Sequence<Triple<AnnotationMetadata, Class<*>, KFunction<*>>> {
    val scanner = PathMatchingResourcePatternResolver(classLoader)
    val readerFactory = SimpleMetadataReaderFactory(classLoader)
    
    return globs.asSequence().flatMap { glob ->
        scanner.getResources(glob).asSequence()
    }.mapNotNull { r ->
        runCatching {
            val metadata = readerFactory.getMetadataReader(r).annotationMetadata
            if (!metadata.hasAnnotation("kotlin.Metadata")) {
                return@mapNotNull null
            }
            if (!metadata.isFinal) {
                return@mapNotNull null
            }
            if (metadata.isAbstract) {
                return@mapNotNull null
            }
            if (!metadata.hasAnnotatedMethods(Listener::class.java.name)) {
                return@mapNotNull null
            }
            val c = classLoader.loadClass(metadata.className)
            metadata to c
        }.getOrNull()
    }.filter { (_, c) ->
        runCatching { c.isTopClass() }.getOrElse { false }
    }.flatMap { (metadata, c) ->
        // metadata, class, function, annotation
        runCatching {
            c.methods.asSequence().mapNotNull { it.kotlinFunction?.let { f -> c to f } }.mapNotNull { (c, func) ->
                if (func.visibility != KVisibility.PUBLIC) {
                    println("func.visibility != KVisibility.PUBLIC")
                    return@mapNotNull null
                }
                
                if (func.instanceParameter != null) {
                    return@mapNotNull null
                }
                
                Triple(metadata, c, func)
            }
        }.getOrElse { emptySequence() }
    }
}


@Suppress("DuplicatedCode")
private fun BeanContainer.getByKParameter(parameter: KParameter): Any {
    val name = parameter.findAnnotation<Named>()?.value?.let { n ->
        n.ifEmpty {
            kotlin.runCatching { parameter.name }.getOrNull()
        }
    }
    val type = parameter.type.classifier as? KClass<*>?
    val value = when {
        name == null && type == null -> {
            throw IllegalStateException("The name and type of parameter [$parameter] are both null")
        }
        name == null && type != null -> {
            // only type
            get(type)
        }
        type == null && name != null -> {
            // only name
            get(name)
        }
        else -> {
            // both not null
            get(name!!, type!!)
        }
    }
    
    return value
}