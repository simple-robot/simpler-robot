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

package love.forte.simboot

import love.forte.simbot.LoggerFactory
import love.forte.simbot.SimbotException
import love.forte.simbot.SimbotIllegalStateException
import org.slf4j.Logger
import java.util.*

/**
 * Simbot boot 入口处，通过 `Java SPI` 加载 [SimbootEntrance] 实例并进行启动。
 *
 * 假若当前运行环境中通过 `Java SPI` 加载得到多个 [SimbootEntrance]，
 * [SimbootApp] 会通过异常展示所有可选内容。你可以通过设置系统参数(System properties)：
 *
 * ```
 * simboot.entrance=xxx.xxx.xxx.XxxEntrance
 * ```
 *
 * 来指定一个你所需要的最终目标。你可以在main函数起始处或者 通过`-D` 启动参数来完成这一操作：
 *
 * ```
 * java -jar -Dsimboot.entrance=xxx.xxx.XxxEntrance YourFile.jar
 * ```
 *
 * 当然，这并不是说这个参数只有在出现冲突的时候才起作用。事实上，只要这个参数存在，则会直接使用此参数指定的目标，
 * 除非指定的目标并不存在或者无法通过无参构造实例化，这样便会得到一个异常。
 *
 *
 *
 *
 */
public object SimbootApp {

    /**
     * 指定 [SimbootEntrance] 的系统参数Key。
     */
    public const val SPECIFY_KEY: String = "simboot.entrance"

    /**
     * A boot logger.
     */
    private val bootLogger = LoggerFactory.getLogger(SimbootApp::class)


    /**
     * 通过加载当前 classpath 中的 [SimbootEntrance] 实例来启动一个BOOT。
     *
     *
     * @throws SimbootApplicationException 可能会出现各种异常，例如通过指定参数加载时的类加载与实例化相关异常，或者通过 [ServiceLoader] 的相关异常，
     * 以及服务加载存在多个指定目标的 [SimbotIllegalStateException] 异常。
     */
    @Throws(SimbootApplicationException::class)
    @JvmStatic
    public fun run(application: Any?, vararg args: String): SimbootContext {
        return runCatching {
            loadEntranceInstance().run(Context(application, Array(args.size) { args[it] }, bootLogger))
        }.getOrElse { failure ->
            throw SimbootApplicationException(failure)
        }
    }


    private class Context(
        override val application: Any?,
        override val args: Array<String>,
        override val logger: Logger
    ) : SimbootEntranceContext
}


private fun loadEntranceInstance(): SimbootEntrance {
    val specified = System.getProperty(SimbootApp.SPECIFY_KEY)
    return if (specified != null) {
        loadEntranceInstanceSpecified(specified)
    } else {
        loadEntranceInstanceByServiceLoader()
    }
}

/**
 * 加载指定的入口类实例。
 *
 * @throws ClassNotFoundException 类路径不存在. via [ClassLoader.loadClass]
 * @throws NoSuchMethodException 没有public的无参构造. via [java.lang.Class.getConstructor]
 * @throws SecurityException 安全异常. via [java.lang.Class.getConstructor]
 * @throws InstantiationException 构造函数获取实例对象错误. via [java.lang.reflect.Constructor.newInstance]
 * @throws IllegalAccessException 构造函数获取实例对象错误. via [java.lang.reflect.Constructor.newInstance]
 * @throws IllegalArgumentException 构造函数获取实例对象错误. via [java.lang.reflect.Constructor.newInstance]
 * @throws java.lang.reflect.InvocationTargetException 构造函数获取实例对象错误. via [java.lang.reflect.Constructor.newInstance]
 */
private fun loadEntranceInstanceSpecified(specifiedPath: String): SimbootEntrance {
    val loader = Thread.currentThread().contextClassLoader
        ?: ClassLoader.getSystemClassLoader()

    val loadedClass = loader.loadClass(specifiedPath)

    return loadedClass.getConstructor().newInstance() as SimbootEntrance
}


private fun loadEntranceInstanceByServiceLoader(): SimbootEntrance {
    val serviceLoader = ServiceLoader.load(SimbootEntrance::class.java)
    val list = serviceLoader.toList()
    if (list.isEmpty()) {
        val message = """
            No service was found.
            
            Please check if there is at least one META-INF/services/love.forte.simboot.SimBootEntrance file in classPath,
            Or use the -D${SimbootApp.SPECIFY_KEY} to specify a specific simboot entrance service.
        
        """.trimIndent()
        throw SimbotIllegalStateException(message)
    }

    if (list.size > 1) {
        val message = buildString {
            append("There is more than one entrance service instance\n\n")
            append("These services are: \n")
            for (instance in list) {
                append("\t-\t").append(instance::class.java.name).append("\n")
            }
            append("Please use -D${SimbootApp.SPECIFY_KEY} to specify a simboot entrance service")
        }

        throw SimbotIllegalStateException(message)
    }

    return list.first()
}


public open class SimbootApplicationException : SimbotException {
    public constructor() : super()
    public constructor(message: String?) : super(message)
    public constructor(message: String?, cause: Throwable?) : super(message, cause)
    public constructor(cause: Throwable?) : super(cause)
}

