package love.forte.simboot.core


/**
 *
 * Simbot boot starter.
 *
 * 是simbot 下， boot模块中的核心启动类。
 *
 * 启动类分为
 *
 *
 *
 * @author ForteScarlet
 */
public class SimbotApp {
    /**
     * 启动参数。
     */
    public var args: MutableList<String> = mutableListOf()

    public fun run() {}
}


/**
 * 启动流程.
 */
public interface BootProcess


/**
 * [SimbotApp] 中整个流程下的各个阶段.
 */
public interface BootProcessStage {


}