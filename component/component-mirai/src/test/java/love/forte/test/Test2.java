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

import java.util.ArrayList;
import java.util.List;

/**
 * @author ForteScarlet
 */
public class Test2 {

    private static final java.lang.ThreadLocal<String> localName = new java.lang.ThreadLocal<>();


    public static void main(String[] args) throws InterruptedException {

        List<Thread> ts = new ArrayList<>(100);

        for (int i = 0; i < 100; i++) {
            Thread t = new Thread(() -> {
                setValue(Thread.currentThread().getName());
                System.out.println(Thread.currentThread().getName() + "\t-\t" + getValue());
            });
            ts.add(t);
            t.setDaemon(true);
            t.start();
        }


        System.out.println(1);
        for (int i = 0; i < 6; i++) {
            System.err.println(ThreadLocal.weakMapSize());
            Thread.sleep(1000);
            System.gc();
        }

        ts.clear();

        System.out.println(2);
        for (int i = 0; i < 6; i++) {
            System.err.println(ThreadLocal.weakMapSize());
            Thread.sleep(1000);
            System.gc();
        }

        localName.remove();


    }

    private static void setValue(String name) {
        localName.set(name);
    }

    private static String getValue() {
        return localName.get();
    }

}
