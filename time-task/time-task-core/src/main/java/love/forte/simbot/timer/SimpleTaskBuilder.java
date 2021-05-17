/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  component-onebot
 *  * File     TaskBuilder.java
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

import cn.hutool.core.util.IdUtil;
import love.forte.simbot.mark.ThreadUnsafe;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 基础的 {@link Task} 构建器。
 *
 * @author ForteScarlet
 * @see SimpleFixedTask
 * @see SimpleCronTask
 */
@SuppressWarnings({"UnusedReturnValue", "unused"})
@ThreadUnsafe
public class SimpleTaskBuilder {
    private String id = null;
    private String name = null;
    private CycleType cycleType = null;
    private String cycle = null;
    private long repeat = -1;
    private long delay = -1;
    private TimeUnit delayTimeUnit = TimeUnit.MILLISECONDS;
    private TaskRunner taskRunner;

    private void checkNameById() {
        if (this.name == null) {
            this.name = "task-" + this.id;
        }
    }

    /**
     * task id.
     *
     * @param id id
     * @return self.
     */
    public SimpleTaskBuilder id(String id) {
        Objects.requireNonNull(id, "Id must be non-null.");
        this.id = id;
        checkNameById();
        return this;
    }

    /**
     * get task id by {@link IdUtil#fastSimpleUUID()}.
     *
     * @return self.
     */
    public SimpleTaskBuilder randomId() {
        return id(IdUtil.fastSimpleUUID());
    }

    /**
     * task name.
     *
     * @param name name
     * @return self.
     */
    public SimpleTaskBuilder name(String name) {
        Objects.requireNonNull(id, "Name must be non-null.");
        this.name = name;
        return this;
    }


    /**
     * 时间周期值。一般可能是一个cron表达式或者时间好秒值。
     *
     * @param cycle 时间周期
     * @return self.
     */
    public SimpleTaskBuilder cycle(String cycle) {
        Objects.requireNonNull(cycle, "Cycle must be non-null.");
        this.cycle = cycle;
        return this;
    }

    public SimpleTaskBuilder cycleType(CycleType cycleType) {
        switch (cycleType) {
            case CRON:
                return cronCycle();
            case FIXED:
                return fixedCycle();
            default:
                throw new IllegalArgumentException("Unknown cycleType " + cycleType);
        }
    }


    public Cron cronCycle() {
        this.cycleType = CycleType.CRON;
        return new Cron();
    }

    public Fixed fixedCycle() {
        this.cycleType = CycleType.FIXED;
        return new Fixed();
    }


    /**
     * task repeat.
     *
     * @param repeat 重复次数。-1: 永远重复; 0: 不重复;
     * @return self.
     */
    public SimpleTaskBuilder repeat(long repeat) {
        this.repeat = repeat;
        return this;
    }

    /**
     * task repeat forever.
     *
     * @return self
     */
    public SimpleTaskBuilder repeatForever() {
        this.repeat = -1;
        return this;
    }

    /**
     * 首次执行的延迟执行时间。
     *
     * @param delay         延迟时间。0：立即执行。-1：不首次执行。
     * @param delayTimeUnit 时间类型
     * @return self.
     */
    public SimpleTaskBuilder delay(long delay, TimeUnit delayTimeUnit) {
        this.delay = delay;
        Objects.requireNonNull(delayTimeUnit, "Delay time unit must be non-null.");
        this.delayTimeUnit = delayTimeUnit;
        return this;
    }

    /**
     * 首次执行的延迟执行时间（毫秒）。
     *
     * @param delay 延迟时间。0：立即执行。-1：不首次执行。
     * @return self.
     */
    public SimpleTaskBuilder delay(long delay) {
        return delay(delay, TimeUnit.MILLISECONDS);
    }

    /**
     * 任务执行逻辑。
     *
     * @param taskRunner runner.
     * @return self.
     */
    public SimpleTaskBuilder taskRunner(TaskRunner taskRunner) {
        Objects.requireNonNull(taskRunner, "Task runner must be non-null");
        this.taskRunner = taskRunner;
        return this;
    }


    /**
     * 构建一个 {@link Task} 实例。
     *
     * @return task instance.
     */
    public Task build() {
        throw new IllegalStateException("No task cycle type selected. use cycleType(CycleType) or [cronCycle() / fixedCycle()] first.");
    }


    /**
     * builder for cron.
     */
    private class Cron extends SimpleTaskBuilder {
        private Cron() {
        }

        /**
         * task id.
         *
         * @param id id
         * @return self.
         */
        @Override
        public Cron id(String id) {
            SimpleTaskBuilder.this.id(id);
            return this;
        }

        /**
         * get task id by {@link IdUtil#fastSimpleUUID()}.
         *
         * @return self.
         */
        @Override
        public Cron randomId() {
            SimpleTaskBuilder.this.randomId();
            return this;
        }

        /**
         * task name.
         *
         * @param name name
         * @return self.
         */
        @Override
        public Cron name(String name) {
            SimpleTaskBuilder.this.name(name);
            return this;
        }

        /**
         * task repeat.
         *
         * @param repeat 重复次数。-1: 永远重复; 0: 不重复;
         * @return self.
         */
        @Override
        public Cron repeat(long repeat) {
            SimpleTaskBuilder.this.repeat(repeat);
            return this;
        }

        /**
         * 时间周期值。一般可能是一个cron表达式或者时间好秒值。
         *
         * @param cycle 时间周期
         * @return self.
         */
        @Override
        public SimpleTaskBuilder cycle(String cycle) {
            return super.cycle(cycle);
        }

        @Override
        public SimpleTaskBuilder cycleType(CycleType cycleType) {
            return SimpleTaskBuilder.this.cycleType(cycleType);
        }

        @Override
        public Cron cronCycle() {
            return this;
        }

        @Override
        public Fixed fixedCycle() {
            return SimpleTaskBuilder.this.fixedCycle();
        }

        /**
         * task repeat forever.
         *
         * @return self
         */
        @Override
        public Cron repeatForever() {
            SimpleTaskBuilder.this.repeatForever();
            return this;
        }

        /**
         * 首次执行的延迟执行时间。
         *
         * @param delay         延迟时间。0：立即执行。-1：不首次执行。
         * @param delayTimeUnit 时间类型
         * @return self.
         */
        @Override
        public Cron delay(long delay, TimeUnit delayTimeUnit) {
            SimpleTaskBuilder.this.delay(delay, delayTimeUnit);
            return this;
        }

        /**
         * 首次执行的延迟执行时间（毫秒）。
         *
         * @param delay 延迟时间。0：立即执行。-1：不首次执行。
         * @return self.
         */
        @Override
        public Cron delay(long delay) {
            SimpleTaskBuilder.this.delay(delay);
            return this;
        }

        /**
         * 任务执行逻辑。
         *
         * @param taskRunner runner.
         * @return self.
         */
        @Override
        public Cron taskRunner(TaskRunner taskRunner) {
            SimpleTaskBuilder.this.taskRunner(taskRunner);
            return this;
        }


        /**
         * build task.
         *
         * @return {@link Task} instance.
         */
        @Override
        public Task build() {
            return SimpleCronTask.newInstance(id, name, cycle, repeat, delayTimeUnit.toMillis(delay), taskRunner);
        }
    }

    /**
     * builder for fixed.
     */
    public class Fixed extends SimpleTaskBuilder {
        private Fixed() {
        }

        private long duration = -1;
        private TimeUnit timeUnit = TimeUnit.MILLISECONDS;

        /**
         * task id.
         *
         * @param id id
         * @return self.
         */
        @Override
        public Fixed id(String id) {
            SimpleTaskBuilder.this.id(id);
            return this;
        }

        /**
         * get task id by {@link IdUtil#fastSimpleUUID()}.
         *
         * @return self.
         */
        @Override
        public Fixed randomId() {
            SimpleTaskBuilder.this.randomId();
            return this;
        }

        /**
         * task name.
         *
         * @param name name
         * @return self.
         */
        @Override
        public Fixed name(String name) {
            SimpleTaskBuilder.this.name(name);
            return this;
        }

        /**
         * task repeat.
         *
         * @param repeat 重复次数。-1: 永远重复; 0: 不重复;
         * @return self.
         */
        @Override
        public Fixed repeat(long repeat) {
            SimpleTaskBuilder.this.repeat(repeat);
            return this;
        }

        /**
         * 时间周期值。一般可能是一个cron表达式或者时间好秒值。
         *
         * @param cycle 时间周期
         * @return self.
         */
        @Override
        public Fixed cycle(String cycle) {
            long du = Long.parseLong(cycle);
            this.duration = du;
            SimpleTaskBuilder.this.cycle(String.valueOf(du));
            return this;
        }

        public Fixed duration(long duration, TimeUnit timeUnit) {
            this.duration = duration;
            this.timeUnit = timeUnit;
            SimpleTaskBuilder.this.cycle(String.valueOf(timeUnit.toMillis(duration)));
            return this;
        }

        public Fixed duration(long duration) {
            return duration(duration, TimeUnit.MILLISECONDS);
        }

        @Override
        public SimpleTaskBuilder cycleType(CycleType cycleType) {
            return SimpleTaskBuilder.this.cycleType(cycleType);
        }

        @Override
        public Cron cronCycle() {
            return SimpleTaskBuilder.this.cronCycle();
        }

        @Override
        public Fixed fixedCycle() {
            return this;
        }

        /**
         * task repeat forever.
         *
         * @return self
         */
        @Override
        public Fixed repeatForever() {
            SimpleTaskBuilder.this.repeatForever();
            return this;
        }

        /**
         * 首次执行的延迟执行时间。
         *
         * @param delay         延迟时间。0：立即执行。-1：不首次执行。
         * @param delayTimeUnit 时间类型
         * @return self.
         */
        @Override
        public Fixed delay(long delay, TimeUnit delayTimeUnit) {
            SimpleTaskBuilder.this.delay(delay, delayTimeUnit);
            return this;
        }

        /**
         * 首次执行的延迟执行时间（毫秒）。
         *
         * @param delay 延迟时间。0：立即执行。-1：不首次执行。
         * @return self.
         */
        @Override
        public Fixed delay(long delay) {
            SimpleTaskBuilder.this.delay(delay);
            return this;
        }

        /**
         * 任务执行逻辑。
         *
         * @param taskRunner runner.
         * @return self.
         */
        @Override
        public Fixed taskRunner(TaskRunner taskRunner) {
            SimpleTaskBuilder.this.taskRunner(taskRunner);
            return this;
        }


        /**
         * build task.
         *
         * @return {@link Task} instance.
         */
        @Override
        public Task build() {
            return SimpleFixedTask.newInstance(id, name, duration, timeUnit, repeat, delayTimeUnit.toMillis(delay), taskRunner);
        }
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public CycleType getCycleType() {
        return cycleType;
    }

    public String getCycle() {
        return cycle;
    }

    public long getRepeat() {
        return repeat;
    }

    public long getDelay() {
        return delay;
    }

    public TimeUnit getDelayTimeUnit() {
        return delayTimeUnit;
    }

    public TaskRunner getTaskRunner() {
        return taskRunner;
    }
}


