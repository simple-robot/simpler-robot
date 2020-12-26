/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  component-onebot
 *  * File     BaseTask.java
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
 * 基础 {@link Task} 实现，提供基本信息建设。
 * @author ForteScarlet
 */
public abstract class BaseTask implements Task {

    private final String id;
    private final String name;
    private final String cycle;
    private final CycleType cycleType;
    private final long repeat;

    protected BaseTask(String id, String name, String cycle, CycleType cycleType, long repeat) {
        this.id = id;
        this.name = name;
        this.cycle = cycle;
        this.cycleType = cycleType;
        this.repeat = repeat;
    }

    protected BaseTask(String id, String name, String cycle, CycleType cycleType) {
        this.id = id;
        this.name = name;
        this.cycle = cycle;
        this.cycleType = cycleType;
        this.repeat = -1;
    }


    @Override
    public String id() {
        return id;
    }
    @Override
    public String name() {
        return name;
    }
    @Override
    public String cycle() {
        return cycle;
    }
    @Override
    public CycleType cycleType() {
        return cycleType;
    }
    @Override
    public long repeat() {
        return repeat;
    }
}
