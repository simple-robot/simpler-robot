/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  component-onebot
 *  * File     TaskRunner.java
 *  *
 *  * You can contact the author through the following channels:
 *  * github https://github.com/ForteScarlet
 *  * gitee  https://gitee.com/ForteScarlet
 *  * email  ForteScarlet@163.com
 *  * QQ     1149159218
 *  *
 *  *
 *
 */

package love.forte.simbot.timer;

/**
 * 一个任务执行的runner。类似于 {@link Runnable}
 * @author ForteScarlet
 */
@FunctionalInterface
public interface TaskRunner {

    /**
     * 执行一个任务。
     * @throws Exception 可能存在任何异常。
     */
    void execute() throws Exception;

}
