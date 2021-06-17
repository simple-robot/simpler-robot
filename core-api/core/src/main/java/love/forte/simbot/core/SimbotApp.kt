/*
 *
 *  * Copyright (c) 2021. ForteScarlet All rights reserved.
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

import cn.hutool.core.io.FileUtil
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
import kotlin.concurrent.thread


private const val RESOURCE_FILE = "file:"
private const val RESOURCE_CLASSPATH = "classpath:"
private const val RESOURCE_HTTP = "http"


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
    private val closeHandleList: List<SimbotContextClosedHandle> = emptyList(),
) : DependBeanFactory by dependBeanFactory, Closeable {
    private companion object : TypedCompLogger(SimbotContext::class.java)

    override fun close() {
        // run doClosed list
        closeHandleList.forEach { handle ->
            kotlin.runCatching {
                logger.debug("Execute handle ${handle.handleName}")
                handle.simbotClose(this)
                logger.debug("Execute handle ${handle.handleName} finish.")
            }.getOrElse { e ->
                val handleLogger = if (handle is LogAble) handle.log else LoggerFactory.getLogger(handle::class.java)
                handleLogger.error("SimbotContext close handle '${handle.handleName}' execute failed!", e)
            }
        }
    }
}


/**
 * [SimbotContext] 被 [SimbotContext.close] 的时候所使用的处理器。
 *
 * 尽可能不要在此逻辑中使用带有阻塞的逻辑。
 *
 * 所有异常均会被捕获并输出为错误日志。
 *
 * 所有的 [SimbotContextClosedHandle] 都会在 [SimbotContext] 构建的时候被初始化完毕.
 *
 */
public interface SimbotContextClosedHandle {
    /**
     * 一个名称，可重写并用于日志提示。
     */
    // @JvmDefault
    val handleName: String get() = "SimbotContextClosedHandle-Default"

    /**
     * 执行close操作。
     *
     * @throws Exception 可能出现任何异常, 异常均会被捕获。
     */
    @Throws(Exception::class)
    fun simbotClose(context: SimbotContext)
}


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
    defaultScanPackage: Array<String>,
    private val loader: ClassLoader,
    private val parentDependBeanFactory: DependBeanFactory?,
    resourceData: List<SimbotResourceData>,
    private val process: SimbotProcess,
    // 可以提供一个基础的额外配置信息。
    private val defaultConfiguration: Configuration?,
    args: List<String>,
    internal val logger: Logger = simbotAppLogger,
) {


    private var showLogo: Boolean = runCatching {
        defaultConfiguration?.getConfig(Logo.ENABLE_KEY)?.boolean
    }.getOrNull() ?: true
    private var showTips: Boolean = runCatching {
        defaultConfiguration?.getConfig(Tips.ENABLE_KEY)?.boolean
    }.getOrNull() ?: true

    protected open val defaultScanPackageArray: Array<String> = defaultScanPackage

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
            try {
                process.post(it)
            } catch (e: Exception) {
                logger.error("SimbotProcess.post failed.", e)
            }
        }
    }


    /**
     * 获取自动装配信息并加载所有auto config类, 以及扫描的包路径。
     */
    private fun initDependCenterWithAutoConfigures(config: Configuration): AutoConfiguresData {
        // 首先扫描并加载所有默认配置信息。
        val autoConfigures = autoConfigures(loader, logger)

        dependCenter = DependCenter(parent = parentDependBeanFactory, configuration = config)

        // register shutdown hook
        Runtime.getRuntime().addShutdownHook(thread(start = false) {
            dependCenter.close()
        })

        // 加载所有的自动配置类
        autoConfigures.classes.forEach {
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

        val activeResources = simbotResourceEnvironment.resourceDataList.filter {
            val commands = it.commands
            commands.isEmpty() || commands.all { c -> simbotArgsEnvironment.contains(c) }
        }

        logger.info("Active resources: ${activeResources.map { it.resource }}")

        // 加载所有的配置类信息
        return activeResources.mapNotNull { resourceData ->
            val resourceName = resourceData.resource

            // get reader.
            val resourceReader: Reader? = runCatching {

                when {
                    resourceName.startsWith(RESOURCE_FILE) -> {
                        // starts with 'file', try get Reader by file
                        FileUtil.getUtf8Reader(resourceName.substring(RESOURCE_FILE.length))
                    }
                    resourceName.startsWith(RESOURCE_CLASSPATH) -> {
                        ResourceUtil.getResourceUtf8Reader(resourceName.substring(RESOURCE_CLASSPATH.length))
                    }
                    resourceName.startsWith(RESOURCE_HTTP) -> {
                        URL(resourceName).connection { "Online resource connection failed. $it" }
                    }
                    else -> {
                        // try file first.
                        val file = File(resourceName)
                        if (file.exists()) {
                            // file exist
                            FileUtil.getUtf8Reader(file)
                        } else {
                            ResourceUtil.getResourceUtf8Reader(resourceName)
                        }
                    }
                }

            }.getOrElse { e ->
                if (resourceData.orIgnore) {
                    null
                } else {
                    throw ResourceException("Unable to read resource: $resourceName", e)
                }
            }

            // parse to configuration.
            resourceReader?.use { reader ->
                logger.debugf("resource [{}] loaded.", resourceName)
                val type: String = resourceData.type
                confReaderManager.parse(type, reader)
            }
        }.reduceOrNull { c1, c2 ->
            MergedConfiguration.merged(c1, c2)
        } ?: LinkedMapConfiguration()
    }


    /**
     * 扫描并注入。
     */
    private fun scanPackagesAndInject(config: Configuration, autoConfigure: AutoConfiguresData) {
        val ignored: Set<Class<*>> = autoConfigure.classes

        // scanPackage.
        val scanPackages = config.getConfig(SCAN_PACKAGES_KEY)?.getObject(Array<String>::class.java)?.asList()
            ?: (defaultScanPackageArray + autoConfigure.packages).distinct()

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
            SimbotPackageScanEnvironmentImpl(scanPackages.toTypedArray())
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

        // 获取所有的异常处理器。
        val handles: List<SimbotContextClosedHandle> = dependCenter.getListByType(SimbotContextClosedHandle::class.java).toList()

        return SimbotContext(dependCenter, botManager, environment, msgGetProcessor, configuration, handles)
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
                    }
                    ?: throw IllegalArgumentException("There is no resource data info or SimbotApplication annotation.")

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
                arrayOf(defPackage),
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
                arrayOf(defPackage),
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
                 @ForteScarlet  D
    """
    internal const val ENABLE_KEY = "simbot.core.logo.enable"
    private val LOGO_PATH: String =
        "META-INF" + File.separator + "simbot" + File.separator + "logo"
    val logo: String = runCatching {
        ResourcesScanner().scan(LOGO_PATH) { it.toASCIIString().endsWith("simbLogo") }
            .collection.randomOrNull()
            ?.toURL()?.readText(Charsets.UTF_8)
            ?: return@runCatching DEF_LOGO
    }.getOrDefault(DEF_LOGO)


}

private fun Logo.show(print: PrintStream = System.out) {
    print.println(logo)
    print.println()
}

private class DisableTips : NullPointerException("Disable online tips.")


// tips! Do you know?
private object Tips {

    private val logger: Logger = LoggerFactory.getLogger("love.forte.simbot.tips")

    internal const val RESOURCE_CONF_KEY = "simbot.core.tips.resource"
    internal const val ENABLE_KEY = "simbot.core.tips.enable"

    private val TIP_PATH: String =
        "META-INF" + File.separator + "simbot" + File.separator + "simbTip.tips"

    internal var TIP_ONLINE_PATH: TipOnline? = null
        get() {
            // if (field != null) {
            //     return field
            // }
            return field ?: when (val resource = System.getProperty(RESOURCE_CONF_KEY)) {
                "gitee", "GITEE", null -> TipOnline.GITEE
                "github", "GITHUB" -> TipOnline.GITHUB
                else -> {
                    logger.warn("Unknown tips resource: {}, used Gitee resource.", resource)
                    TipOnline.GITEE
                }
            }
        }


    val randomTip: String? = runCatching {
        val url = TIP_ONLINE_PATH?.url ?: throw DisableTips()
        logger.trace("Tips online resource {}, url: {}", TIP_ONLINE_PATH, url)
        URL(url).connection { "Online tips connection failed. $it" }
    }.getOrElse { e ->
        if (e !is DisableTips) {
            logger.debugEf("Read online tips failed: {}", e, e.localizedMessage)
        }
        runCatching {
            ResourceUtil.getResourceUtf8Reader(TIP_PATH)
        }.getOrNull()
    }?.useLines {
        it.filter { s -> s.isNotBlank() }.toList().randomOrNull()
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


private inline fun URL.connection(
    readTimeout: Int = 5000, connectTimeout: Int = 5000,
    onError: (errorStreamReaderText: String) -> String,
): Reader {
    // 网络请求
    return (this.openConnection() as HttpURLConnection).run {
        this.readTimeout = readTimeout
        this.connectTimeout = connectTimeout
        connect()
        takeIf { responseCode < 300 }
            ?: throw IOException(onError(errorStream.reader().use { it.readText() }))
    }.inputStream.reader()
}





