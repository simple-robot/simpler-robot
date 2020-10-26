/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     SimbotEnvironment.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */
@file:JvmName("SimbotEnvironments")
package love.forte.simbot

import love.forte.simbot.annotation.SimbotResource


/**
 * 代表了标注注解的启动类。
 */
public class MainApplicationObject(val mainApp: Any)


/**
 * [SimbotResource] 数据类。
 * @see SimbotResource
 */
public data class SimbotResourceData(
    val resource: String,
    val type: String,
    val orIgnore: Boolean,
    val commands: List<String>
)


/**
 * [SimbotResource] 转化为 [SimbotResourceData] 数据类。
 */
public fun SimbotResource.toData(): SimbotResourceData {
    val v: String = this.value
    val t: String = with(this.type) {
        if (isBlank()) {
            // blank, try to get from v.
            v.split(".").let {
                if (it.size <= 1) {
                    // 截取结果只有一个，说明没有扩展名。
                    throw IllegalStateException("Unable to confirm the type (extension) of resource [$v]")
                } else {
                    it.last()
                }
            }

        } else this
    }

    return SimbotResourceData(v, t, this.orIgnore, this.command.asList())
}


/**
 *  SimbotResourceEnvironment. 配置文件信息。
 */
public interface SimbotResourceEnvironment {
    val resourceDataList: List<SimbotResourceData>
}

/**
 * [SimbotResourceEnvironment] 默认实现。
 */
public data class SimbotResourceEnvironmentImpl(override val resourceDataList: List<SimbotResourceData>) :
    SimbotResourceEnvironment


/**
 *  SimbotArgsEnvironment. 启动参数内容。
 */
public interface SimbotArgsEnvironment {
    val args: Array<String>
}

/**
 * [SimbotArgsEnvironment] 默认实现。
 */
public data class SimbotArgsEnvironmentImpl(override val args: Array<String>) :
    SimbotArgsEnvironment {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SimbotArgsEnvironmentImpl

        if (!args.contentEquals(other.args)) return false

        return true
    }

    override fun hashCode(): Int {
        return args.contentHashCode()
    }
}


/**
 * 包扫描信息。
 */
public interface SimbotPackageScanEnvironment {
    val scanPackages: Array<String>
}

/**
 * [SimbotPackageScanEnvironment] 实现。
 */
public data class SimbotPackageScanEnvironmentImpl(override val scanPackages: Array<String>) :
    SimbotPackageScanEnvironment {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SimbotPackageScanEnvironmentImpl

        if (!scanPackages.contentEquals(other.scanPackages)) return false

        return true
    }

    override fun hashCode(): Int {
        return scanPackages.contentHashCode()
    }
}


/**
 * simbot 环境参数内容。
 */
public interface SimbotEnvironment {
    val resourceEnvironment: SimbotResourceEnvironment
    val argsEnvironment: SimbotArgsEnvironment
    val packageScanEnvironment: SimbotPackageScanEnvironment
}


/**
 * [SimbotEnvironment] 默认实现。
 */
public data class CoreSimbotEnvironment(
    override val resourceEnvironment: SimbotResourceEnvironment,
    override val argsEnvironment: SimbotArgsEnvironment,
    override val packageScanEnvironment: SimbotPackageScanEnvironment
) : SimbotEnvironment
