/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  simple-robot-core
 * File     Iterators.java
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 *
 */

package love.forte.common.utils;

import java.util.Enumeration;
import java.util.Iterator;

/**
 * 迭代器工具类
 *
 * @author <a href="https://github.com/ForteScarlet"> ForteScarlet </a>
 */
public class IteratorUtil {

    /**
     * 将 {@link Enumeration} 转化为迭代器对象
     * @return Iterator
     */
    public static <T> Iterator<T> enumerationIter(Enumeration<T> enumeration){
        return new EnumerateIterator<>(enumeration);
    }





    /**
     * {@link Enumeration} 的迭代器实现。
     *
     */
    static final class EnumerateIterator<T> implements Iterator<T>, Enumeration<T> {
        private final Enumeration<T> enumeration;
        EnumerateIterator(Enumeration<T> enumeration){
            this.enumeration = enumeration;
        }
        @Override
        public boolean hasNext() {
            return enumeration.hasMoreElements();
        }

        @Override
        public T next() {
            return enumeration.nextElement();
        }

        @Override
        public boolean hasMoreElements() {
            return enumeration.hasMoreElements();
        }

        @Override
        public T nextElement() {
            return enumeration.nextElement();
        }

        @Override
        public String toString() {
            return super.toString() + "<by>("+ enumeration.toString() +")";
        }
    }


}
