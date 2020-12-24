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

package love.forte.simbot.timer;

/**
 * 时间周期表现为 {@code cron} 的定时任务。
 *
 * @author ForteScarlet
 */
public interface CronTask extends Task {

    /**
     * 时间周期为一个 {@code cron} 表达式字符串。
     * @return cron 表达式字符串。
     */
    String cron();


    /**
     * 任务的周期信息默认为 {@link #cron()} 的值。
     * @return {@link #cron()}
     */
    @Override
    default String cycle(){
        return cron();
    }

}
