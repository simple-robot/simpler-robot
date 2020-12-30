/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  simple-robot
 *  * File     MiraiAvatar.kt
 *  *
 *  * You can contact the author through the following channels:
 *  * github https://github.com/ForteScarlet
 *  * gitee  https://gitee.com/ForteScarlet
 *  * email  ForteScarlet@163.com
 *  * QQ     1149159218
 *
 */

@file:JvmName("SimbotApps")

package love.forte.simbot.core

import love.forte.common.configuration.Configuration
import love.forte.common.configuration.ConfigurationManagerRegistry
import love.forte.common.configuration.ConfigurationParserManager
import love.forte.common.configuration.impl.LinkedMapConfiguration
import love.forte.common.configuration.impl.MergedConfiguration
import love.forte.common.exception.ResourceException
import love.forte.common.ifOr
import love.forte.common.ioc.DependBeanFactory
import love.forte.common.ioc.DependCenter
import love.forte.common.ioc.InstanceBeanDepend
import love.forte.common.ioc.annotation.Beans
import love.forte.common.listAs
import love.forte.common.utils.ResourceUtil
import love.forte.common.utils.annotation.AnnotationUtil
import love.forte.common.utils.scanner.HutoolClassesScanner
import love.forte.common.utils.scanner.ResourcesScanner
import love.forte.common.utils.scanner.Scanner
import love.forte.simbot.*
import love.forte.simbot.annotation.SimbotApplication
import love.forte.simbot.bot.BotManager
import love.forte.simbot.constant.PriorityConstant
import love.forte.simbot.listener.MsgGetProcessor
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.*
import java.net.HttpURLConnection
import java.net.URL


/**
 * 如果在 [SimbotApp.run] 的时候填入的参数为此接口实现，则会在必要的流程中调用它们。
 */
public interface SimbotProcess {
    fun pre(config: Configuration)
    fun post(context: SimbotContext)
}

/**
 * 默认的无内容流程接口实现。
 */
internal object NothingProcess : SimbotProcess {
    override fun pre(config: Configuration) {}
    override fun post(context: SimbotContext) {}
}


/**
 * simbot 启动完成后的部分数据信息。
 */
public class SimbotContext
internal constructor(
    private val dependBeanFactory: DependBeanFactory,
    val botManager: BotManager,
    val environment: SimbotEnvironment,
    val msgProcessor: MsgGetProcessor,
    val configuration: Configuration,
    private val doClosed: Closeable = Closeable {},
) : DependBeanFactory by dependBeanFactory,
    Closeable by doClosed


internal val simbotAppLogger: Logger = LoggerFactory.getLogger(SimbotApp::class.java)


/**
 * simbot app 启动类。
 *
 * 启动时，最优先的事就是构建依赖中心并确定扫描所有的默认资源；
 *
 * 其次进行配置文件读取，并构建配置信息。
 *
 * 其次进行扫描并构建依赖中心；
 *
 * ... //
 *
 */
public open class SimbotApp
protected constructor(
    /** 当不存在 scanPackage 配置项的时候，使用此扫描路径。不可为空。 */
    defaultScanPackage: String,
    private val loader: ClassLoader,
    private val parentDependBeanFactory: DependBeanFactory?,
    resourceData: List<SimbotResourceData>,
    private val process: SimbotProcess,
    // 可以提供一个基础的额外配置信息。
    private val defaultConfiguration: Configuration?,
    args: List<String>,
    internal val logger: Logger = simbotAppLogger,
) {


    var showLogo: Boolean = runCatching {
        defaultConfiguration?.getConfig(Logo.ENABLE_KEY)?.boolean
    }.getOrNull() ?: true
    var showTips: Boolean = runCatching {
        defaultConfiguration?.getConfig(Tips.ENABLE_KEY)?.boolean
    }.getOrNull() ?: true

    protected open val defaultScanPackageArray: Array<String> = arrayOf(defaultScanPackage)

    /** 资源环境，即启动的时候使用的配置文件环境，会在 [initDependCenterWithRunData] 阶段注入依赖。 */
    protected open val simbotResourceEnvironment: SimbotResourceEnvironment =
        SimbotResourceEnvironmentImpl(resourceData)

    /** 启动参数环境，会在 [initDependCenterWithRunData] 阶段注入依赖。 */
    protected open val simbotArgsEnvironment: SimbotArgsEnvironment = SimbotArgsEnvironmentImpl(args.toTypedArray())

    /**
     * 获取一个配置类解析管理器。
     */
    protected open val configurationManager: ConfigurationParserManager = ConfigurationManagerRegistry.defaultManager()

    // /**
    //  * 流程配置。
    //  */
    // protected open val appConfiguration = SimbotAppConfiguration()

    /**
     * 包扫描器。 默认为 [HutoolClassesScanner]。
     * 每次获取都是一个新的实例。
     */
    protected open val scanner: Scanner<String, Class<*>> get() = HutoolClassesScanner()

    /**
     * 依赖注入中心。
     */
    private lateinit var dependCenter: DependCenter


    /**
     * 启动执行。
     */
    @Synchronized
    internal fun run(): SimbotContext {
        // show logo.
        if (showLogo) {
            Logo.show()
        }

        runCatching {
            defaultConfiguration?.getConfig(Tips.RESOURCE_CONF_KEY)?.getObject(TipOnline::class.java)?.let {
                Tips.TIP_ONLINE_PATH = it
            }
        }

        if (showTips) {
            Tips.show()
        }


        // load configs.
        val config: Configuration = loadResourcesToConfiguration().let {
            if (defaultConfiguration != null) MergedConfiguration.merged(defaultConfiguration, it)
            else it
        }
        // process pre config.
        process.pre(config)

        // load all auto config.
        val autoConfigures = initDependCenterWithAutoConfigures(config)
        // init with run data.
        initDependCenterWithRunData()

        // val hi = config.getConfig("simbot.core.init")?.string ?: "hello!!!!!!!!!!!!!!!~"
        // println(hi)

        // merge depend center config.
        // dependCenter.mergeConfig { c -> MergedConfiguration.merged(c, config) }

        // scan and inject.
        scanPackagesAndInject(dependCenter.configuration, autoConfigures)

        // init depend.
        initDependCenter()

        // return.
        return createSimbotContext(config).also {
            // post
            process.post(it)
        }
    }


    /**
     * 获取自动装配信息并加载所有auto config类。
     */
    private fun initDependCenterWithAutoConfigures(config: Configuration): Set<Class<*>> {
        // 首先扫描并加载所有默认配置信息。
        val autoConfigures = autoConfigures(loader, logger)

        dependCenter = DependCenter(parent = parentDependBeanFactory, configuration = config)

        // 加载所有的自动配置类
        autoConfigures.forEach {
            dependCenter.register(it)
        }

        return autoConfigures
    }

    /**
     * 将部分初始化、启动资源进行初始化。
     */
    private fun initDependCenterWithRunData() {
        // depend center 注册自己
        dependCenter.registerInstance("dependCenter", dependCenter)
        // 注册部分环境:
        // simbotResourceEnvironment: SimbotResourceEnvironment
        // simbotArgsEnvironment: SimbotArgsEnvironment
        dependCenter.registerInstance("simbotResourceEnvironment", simbotResourceEnvironment)
        dependCenter.registerInstance("simbotArgsEnvironment", simbotArgsEnvironment)
    }

    /**
     * 读取所有的配置文件信息并整合为一个 [Configuration] 实例。
     */
    private fun loadResourcesToConfiguration(): Configuration {
        val confReaderManager = configurationManager

        val args = simbotArgsEnvironment.args

        // 加载所有的配置类信息
        return simbotResourceEnvironment.resourceDataList.filter {
            val commands = it.commands
            commands.isEmpty() || commands.all { c -> args.contains(c) }
        }.mapNotNull { resourceData ->
            val resourceName = resourceData.resource

            // get reader.
            val resourceReader: Reader? = runCatching {
                ResourceUtil.getResourceUtf8Reader(resourceName)
            }.getOrElse { e ->
                if (resourceData.orIgnore) {
                    null
                } else {
                    throw ResourceException("Unable to read resource: $resourceName", e)
                }
            }


            // parse to configuration.
            resourceReader?.let {
                logger.debugf("resource [{}] loaded.", resourceName)
                val type: String = resourceData.type
                confReaderManager.parse(type, it)
            }
        }.reduceOrNull { c1, c2 ->
            MergedConfiguration.merged(c1, c2)
        } ?: LinkedMapConfiguration()
    }


    /**
     * 扫描并注入。
     */
    private fun scanPackagesAndInject(config: Configuration, ignored: Set<Class<*>>) {
        // scanPackage.
        val scanPackages = config.getConfig(SCAN_PACKAGES_KEY)?.getObject(Array<String>::class.java)
            ?: defaultScanPackageArray

        val scanner = this.scanner

        scanPackages.forEach {
            scanner.scan(it) { c ->
                c !in ignored &&
                        AnnotationUtil.containsAnnotation(c, Beans::class.java)
            }
            logger.debug("package scan: {}", it)
        }

        val collection = scanner.collection
        //     .toMutableSet().apply {
        //          // remove all ignored.
        //          removeAll(ignored)
        //      }

        // inject classes.
        dependCenter.inject(types = collection.toTypedArray())
        // register simbotPackageScanEnvironment.
        dependCenter.registerInstance(
            "simbotPackageScanEnvironment",
            SimbotPackageScanEnvironmentImpl(scanPackages.copyOf())
        )


    }


    /**
     * 初始化 depend center.
     */
    private fun initDependCenter() {
        dependCenter.init()
    }


    /**
     * 构建一个 [SimbotContext] 实例并返回。
     */
    private fun createSimbotContext(configuration: Configuration): SimbotContext {
        // 获取 botManager.
        val botManager = dependCenter[BotManager::class.java]
        val environment = dependCenter[SimbotEnvironment::class.java]
        val msgGetProcessor = dependCenter[MsgGetProcessor::class.java]

        return SimbotContext(dependCenter, botManager, environment, msgGetProcessor, configuration)
    }


    /**
     * companion for static run.
     */
    companion object Run {

        internal const val SCAN_PACKAGES_KEY = "simbot.core.scan-package"

        /**
         * 启动，使用一个class启动。
         */
        @JvmStatic
        @JvmOverloads
        public fun run(
            appType: Class<*>,
            loader: ClassLoader = Thread.currentThread().contextClassLoader ?: ClassLoader.getSystemClassLoader(),
            parentDependBeanFactory: DependBeanFactory? = null,
            defaultConfiguration: Configuration? = null,
            vararg args: String,
        ): SimbotContext {

            // 资源配置数据，获取appType的注解。
            @Suppress("UNCHECKED_CAST")
            val resourceData: List<SimbotResourceData> =
                AnnotationUtil.getAnnotation(appType, SimbotApplication::class.java)?.value
                    ?.map {
                        it.toData()
                    } ?: throw IllegalArgumentException("There is no resource data info.")

            // 流程接口实例。
            val process: SimbotProcess = if (SimbotProcess::class.java.isAssignableFrom(appType)) {
                kotlin.runCatching { appType.newInstance() as SimbotProcess }.getOrElse { e ->
                    throw IllegalStateException("$appType cannot be SimbotProcess instance: ${e.localizedMessage}", e)
                }
            } else NothingProcess

            // 默认扫描使用的启动器实例。
            val defPackage: String = appType.`package`.name

            // run and return.
            return SimbotApp(
                defPackage,
                loader,
                parentDependBeanFactory,
                resourceData,
                process,
                defaultConfiguration,
                args.asList(),
                LoggerFactory.getLogger(appType)
            ).run()
        }


        /**
         * 启动，使用一个类实例启动。
         */
        @JvmStatic
        @JvmOverloads
        public fun run(
            app: Any,
            loader: ClassLoader = Thread.currentThread().contextClassLoader ?: ClassLoader.getSystemClassLoader(),
            parentDependBeanFactory: DependBeanFactory? = null,
            defaultConfiguration: Configuration? = null,
            vararg args: String,
        ): SimbotContext {

            // 资源配置数据，允许app直接作为注解数据填入。
            @Suppress("UNCHECKED_CAST")
            val resourceData: List<SimbotResourceData> = when {
                /** 如果是class，执行另一个方法。 */
                app is Class<*> -> return run(app, loader, parentDependBeanFactory, defaultConfiguration, *args)
                app is SimbotResourceData -> listOf(app)
                app is List<*> && listAs<SimbotResourceData, Any?>(app) != null -> app as List<SimbotResourceData>
                else -> {
                    AnnotationUtil.getAnnotation(app::class.java, SimbotApplication::class.java)?.value
                        ?.map {
                            it.toData()
                        } ?: throw IllegalArgumentException("There is no resource data info.")
                }
            }

            // 流程接口实例。
            val process: SimbotProcess = ifOr<SimbotProcess>(app) { NothingProcess }

            val defPackage: String = app::class.java.`package`.name

            // run and return.
            return SimbotApp(
                defPackage,
                loader,
                parentDependBeanFactory,
                resourceData,
                process,
                defaultConfiguration,
                args.asList(),
                LoggerFactory.getLogger(app::class.java)
            ).run()
        }


    }
}


/**
 * 注册一个实例单例。
 */
internal fun <T> DependCenter.registerInstance(name: String, instance: T) {
    this.register(InstanceBeanDepend(name, PriorityConstant.CORE_TENTH, instance = instance))
}


// logo. logo? logo!
private object Logo {
    private const val DEF_LOGO = """
     _           _           _   
    (_)         | |         | |  
 ___ _ _ __ ___ | |__   ___ | |_ 
/ __| | '_ ` _ \| '_ \ / _ \| __|
\__ \ | | | | | | |_) | (_) | |_ 
|___/_|_| |_| |_|_.__/ \___/ \__|
                    @ForteScarlet
    """
    internal const val ENABLE_KEY = "simbot.core.logo.enable"
    private val LOGO_PATH: String =
        "META-INF" + File.separator + "simbot" + File.separator + "logo"
    val logo: String by lazy(LazyThreadSafetyMode.PUBLICATION) {
        runCatching {
            ResourcesScanner().scan(LOGO_PATH) { it.toASCIIString().endsWith("simbLogo") }
                .collection.randomOrNull()
                ?.toURL()?.readText(Charsets.UTF_8)
                ?: return@runCatching DEF_LOGO
        }.getOrDefault(DEF_LOGO)
    }

}

private fun Logo.show(print: PrintStream = System.out) {
    print.println(logo)
    print.println()
}

private object DisableTips : NullPointerException("Disable online tips.")


// tips! Do you know?
private object Tips {

    private val logger: Logger = LoggerFactory.getLogger("love.forte.simbot.tips")

    internal const val RESOURCE_CONF_KEY = "simbot.core.tips.resource"
    internal const val ENABLE_KEY = "simbot.core.tips.enable"

    private val TIP_PATH: String =
        "META-INF" + File.separator + "simbot" + File.separator + "simbTip.tips"

    internal var TIP_ONLINE_PATH: TipOnline? = null
        get() {
            if (field != null) {
                return field
            }
            return when (val resource = System.getProperty(RESOURCE_CONF_KEY)) {
                "gitee", "GITEE", null -> TipOnline.GITEE
                "github", "GITHUB" -> TipOnline.GITHUB
                else -> {
                    logger.warn("Unknown tips resource: {}, used Gitee resource.", resource)
                    TipOnline.GITEE
                }
            }
        }


    val randomTip: String? by lazy(LazyThreadSafetyMode.PUBLICATION) {
        kotlin.runCatching {
            val url = TIP_ONLINE_PATH?.url ?: throw DisableTips

            logger.trace("Tips online resource {}, url: {}", TIP_ONLINE_PATH, url)

            (URL(url).openConnection() as HttpURLConnection).run {
                readTimeout = 5000
                connectTimeout = 5000
                connect()
                takeIf { responseCode < 300 }
                    ?: throw IOException("Online tips connection failed. ${errorStream.reader().use { it.readText() }}")
            }.inputStream.reader()
        }.getOrElse { e ->
            if (e != DisableTips) {
                logger.debugEf("Read online tips failed: {}", e, e.localizedMessage)
            }
            runCatching {
                ResourceUtil.getResourceUtf8Reader(TIP_PATH)
            }.getOrNull()
        }?.useLines {
            it.filter { s -> s.isNotBlank() }.toList().randomOrNull()
        }
    }



}


internal enum class TipOnline(val url: String) {
    GITHUB("https://raw.githubusercontent.com/ForteScarlet/simpler-robot/dev/tips/tips.tips"),
    GITEE("https://gitee.com/ForteScarlet/simpler-robot/raw/dev/tips/tips.tips"),
}


private fun Tips.show(print: PrintStream = System.out) {
    randomTip?.run {
        print.println("Tips: $this")
        print.println()
    }
}








