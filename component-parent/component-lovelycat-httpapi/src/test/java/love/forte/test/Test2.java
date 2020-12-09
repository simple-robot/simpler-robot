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

package love.forte.test;

import love.forte.simbot.core.listener.ListenResultImpl;

import java.util.concurrent.atomic.AtomicReference;


/**
 * @author ForteScarlet
 */
public class Test2 {
    public static void main(String[] args) throws InterruptedException {

        ListenResultImpl.success();

        AtomicReference<Integer> ref = new AtomicReference<>(null);

        for (int i = 0; i < 500; i++) {
            int index = i;
            new Thread(() -> {
                Integer get;
                if (index == 100) {
                    get = ref.updateAndGet(old -> null);
                } else {
                    get = ref.updateAndGet(old -> {
                        if (old == null) {
                            System.out.println("compute.");
                            return index;
                        } else {
                            return old;
                        }
                    });
                }
                System.out.println("index: " + index + ", get: " + get);
            }).start();
        }

        // LazyTimeLimitCache<Integer> cache = new LazyTimeLimitCache<>(1000);
        //
        // for (int i = 0; i < 4; i++) {
        //     int index = i;
        //     new Thread(() -> {
        //         cache.compute(() -> {
        //             // 如果被计算了则会输出
        //             System.out.println("compute time: " + System.currentTimeMillis());
        //             System.out.println("compute by:   " + index);
        //             return index;
        //         });
        //     }).start();
        // }

    }
}
