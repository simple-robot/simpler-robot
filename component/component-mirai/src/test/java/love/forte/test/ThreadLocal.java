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

package love.forte.test;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * 简易的弱引用ThreadLocal.
 * 构建实例的 `id` 参数用于区分不同的参数。
 * @author ForteScarlet
 */
public final class ThreadLocal<T> {

    private static final WeakHashMap<Thread, Map<Integer, Object>> weakMap = new WeakHashMap<>();

    private final int id;

    private static Map<Integer, Object> createMap() {
        return new LinkedHashMap<>();
    }

    public ThreadLocal(int id) {
        this.id = id;
    }

    public ThreadLocal(int id, T init) {
        this.id = id;
        set(init);
    }

    public static int weakMapSize() {
        return weakMap.size();
    }

    private Map<Integer, Object> getMap() {
        Thread t = Thread.currentThread();
        Map<Integer, Object> map = weakMap.get(t);
        if (map == null) {
            synchronized (weakMap) {
                map = weakMap.computeIfAbsent(t, k -> createMap());
            }
        }
        return map;
    }

    public void set(T value) {
        getMap().put(id, value);
    }


    @SuppressWarnings("unchecked")
    public T get() {
        return (T) getMap().get(id);
    }


    public void remove() {
        Thread t = Thread.currentThread();
        weakMap.remove(t);
    }


}
