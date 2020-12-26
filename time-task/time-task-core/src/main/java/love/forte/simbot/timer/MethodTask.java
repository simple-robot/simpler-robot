/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  component-onebot
 *  * File     MethodTask.java
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

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.function.Supplier;

/**
 * 基于某个 JavaMethod 实例的Task调用。
 * <p>
 * Method不允许存在参数，且必须是 <code>PUBLIC</code> 的。
 *
 * @author ForteScarlet
 */
public abstract class MethodTask extends BaseTask implements Task {

    /**
     * 方法实例。
     */
    protected final Method method;
    protected final Supplier<Object> supplier;

    protected MethodTask(
            String id, String name, String cycle, CycleType cycleType, long repeat, long delay,
            Method method, Supplier<Object> instanceSupplier
    ) {
        super(id, name, cycle, cycleType, repeat, delay);
        checkMethod(method);
        this.method = method;
        this.supplier = instanceSupplier;
    }

    protected MethodTask(
            String id, String name, String cycle, CycleType cycleType, long repeat,
            Method method, Supplier<Object> instanceSupplier
    ) {
        super(id, name, cycle, cycleType, repeat);
        checkMethod(method);
        this.method = method;
        this.supplier = instanceSupplier;
    }

    protected MethodTask(
            String id, String name, String cycle, CycleType cycleType,
            Method method, Supplier<Object> instanceSupplier
    ) {
        super(id, name, cycle, cycleType);
        checkMethod(method);
        this.method = method;
        this.supplier = instanceSupplier;
    }

    static void checkMethod(Method method) {
        if (method == null) {
            throw new NullPointerException("Method is null.");
        }
        String name = method.toGenericString();
        int modifiers = method.getModifiers();
        if (!Modifier.isPublic(modifiers)) {
            throw new IllegalStateException("Method '"+ name +"' is not public.");
        }
        if (Modifier.isAbstract(modifiers)) {
            throw new IllegalStateException("Method '"+ name +"' is abstract.");
        }
        int parameterCount = method.getParameterCount();
        if (parameterCount > 0) {
            throw new IllegalStateException("Method cannot have parameters, but there are " + parameterCount);
        }
    }


    /**
     * 执行一个任务。这个任务没有返回值，也没有参数。
     *
     * @throws Exception 可能会存在任何异常。
     */
    @Override
    public void execute() throws Exception {
        Object instance = supplier.get();
        method.invoke(instance);
    }
}
