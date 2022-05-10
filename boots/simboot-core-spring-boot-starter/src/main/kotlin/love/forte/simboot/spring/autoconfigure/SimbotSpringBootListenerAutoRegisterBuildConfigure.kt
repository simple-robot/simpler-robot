package love.forte.simboot.spring.autoconfigure

import love.forte.annotationtool.core.KAnnotationTool
import love.forte.annotationtool.core.getAnnotation
import love.forte.simboot.annotation.Binder
import love.forte.simboot.annotation.Listener
import love.forte.simboot.annotation.scopeIfDefault
import love.forte.simboot.core.binder.BinderManager
import love.forte.simboot.core.binder.CoreBinderManager
import love.forte.simboot.listener.ParameterBinderFactory
import love.forte.simboot.spring.autoconfigure.application.SpringBootApplicationBuilder
import love.forte.simboot.spring.autoconfigure.application.SpringBootApplicationConfiguration
import love.forte.simbot.LoggerFactory
import love.forte.simbot.SimbotIllegalStateException
import love.forte.simbot.core.application.listeners
import love.forte.simbot.event.EventListener
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.memberExtensionFunctions
import kotlin.reflect.full.memberFunctions


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
    private lateinit var listeners: List<EventListener>
    
    
    override fun setApplicationContext(applicationContext: ApplicationContext) {
        resolveListeners(applicationContext)
    }
    
    override fun SpringBootApplicationBuilder.config(configuration: SpringBootApplicationConfiguration) {
        logger.debug("Registing all listeners... The size of listeners are {}", listeners.size)
        listeners {
            listeners.forEach { listener ->
                // TODO log
                listener(listener)
                // TODO log
            }
        }
        logger.debug("All {} listeners registered.", listeners.size)
    }
    
    
    private fun resolveBinderManager(applicationContext: ApplicationContext): BinderManager {
        val tool = KAnnotationTool()
        val globalBinderFactories: MutableList<ParameterBinderFactory> = mutableListOf()
        val idBinderFactories: MutableMap<String, ParameterBinderFactory> = mutableMapOf()
    
        val binderManager = CoreBinderManager(globalBinderFactories, idBinderFactories)
        
        // region instance binders
        val binderInstanceNames = applicationContext.getBeanNamesForType(ParameterBinderFactory::class.java)
        
        binderInstanceNames.forEach { name ->
            // annotation
            val type = kotlin.runCatching { applicationContext.getType(name)?.kotlin }.getOrNull() ?: return@forEach
            val binderAnnotation = tool.getAnnotation<Binder>(type)
            val id = binderAnnotation?.value?.firstOrNull() ?: name
            
            fun global() {
                val globalBinderFactory = applicationContext.getBean(name, ParameterBinderFactory::class.java)
                globalBinderFactories.add(globalBinderFactory)
            }
            
            fun specify() {
                val specifyBinderFactory = applicationContext.getBean(name, ParameterBinderFactory::class.java)
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
        
        
        // functional binders
        applicationContext.beanDefinitionNames.asSequence()
            .flatMap { name ->
                val kClass = kotlin.runCatching { applicationContext.getType(name)?.kotlin }.getOrNull()
                    ?: return@flatMap emptySequence()
                
                kClass.allFunctions.asSequence().mapNotNull { function ->
                    val binderAnnotation = tool.getAnnotation<Binder>(function) ?: return@mapNotNull null
                    // skip if scope == CURRENT.
                    if (binderAnnotation.scope == Binder.Scope.CURRENT) {
                        return@mapNotNull null
                    }
                    
                    Quadruple(name, kClass, function, binderAnnotation)
                }
            }.forEach { (name, type, function, binder) ->
                // not current
                val scope = binder.scope
                val id = binder.value.firstOrNull()
                
                when(scope) {
                    Binder.Scope.DEFAULT -> TODO()
                    Binder.Scope.SPECIFY -> TODO()
                    else -> {
                        // TODO global
                        
                    }
                }
                
                val binder = binderManager.resolveFunctionToBinderFactory(function) {
                
                }
                
                
                TODO("解析加载binder")
                
            
            }
        
        
        // TODO find functions
        
        return binderManager
    }
    
    
    private fun resolveListeners(applicationContext: ApplicationContext) {
        // binders.
        val binderManager = resolveBinderManager(applicationContext)
        
        
        // find instances for init
        val instances = applicationContext.getBeansOfType(EventListener::class.java).values
        val listeners = instances.toMutableList()
        
        // scan functions
        applicationContext.beanDefinitionNames.asSequence()
            .map { name -> name to applicationContext.getType(name) }
            .flatMap { (name, type) ->
                val kClass = kotlin.runCatching { type.kotlin }.getOrElse {
                    logger.debug("Cannot resolve type {} to kotlin, skip it.", type)
                    return@flatMap emptySequence()
                }
                kClass.allFunctions.asSequence().map { function -> Triple(name, kClass, function) }
            }
            .mapNotNullTo(listeners) { (name, type, function) ->
                
                // TODO map function to listener and register
                
                
                null
            }
        
        
    }
    
    
    private inline val KClass<*>.allFunctions: List<KFunction<*>>
        get() = kotlin.runCatching {
            memberFunctions + memberExtensionFunctions
        }.getOrDefault(emptyList())
    
    private companion object {
        private val logger = LoggerFactory.getLogger(SimbotSpringBootListenerAutoRegisterBuildConfigure::class)
    }
}


private data class Quadruple<out A, out B, out C, out D>(val a: A, val b: B, val c: C, val d: D)