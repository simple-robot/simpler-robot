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
@file:JvmName("CoreFilterManagers")

package love.forte.simbot.core.filter

import love.forte.simbot.annotation.Filters
import love.forte.simbot.api.message.events.MsgGet
import love.forte.simbot.core.strict.StrictManager
import love.forte.simbot.filter.*
import love.forte.simbot.mark.ThreadUnsafe
import love.forte.simbot.read
import love.forte.simbot.write
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.locks.ReadWriteLock
import java.util.concurrent.locks.ReentrantReadWriteLock


// /**
//  * 恒定返回false的 [love.forte.simbot.filter.AtDetection] 实例。
//  */
// internal val ConstantFalseAtDetection: AtDetection = AtDetection { false }


/**
 *
 * [过滤器管理器][FilterManager] 实现。
 *
 * @author ForteScarlet -> https://github.com/ForteScarlet
 */
public class CoreFilterManager(
    private val filterTargetManager: FilterTargetManager,
    private val strictManager: StrictManager
) : FilterManager {


    /**
     * 全部的自定义过滤器列表。
     */
    private val _filters: MutableMap<String, ListenerFilter> = ConcurrentHashMap()

    /** [AtDetectionFactory] 相关读写锁 */
    private val atDetectionUpdateLock: ReadWriteLock = ReentrantReadWriteLock()

    /**
     * 全部的 [AtDetectionFactory] 构建工厂。
     */
    private val atDetectionFactories: Deque<AtDetectionFactory> = LinkedList()

    /**
     * 用于构建 [CompoundAtDetection] 的, 可随机访问的列表元素。
     */
    private var _atDetectionFactoryRandomAccessList: List<AtDetectionFactory> = emptyList()

    /**
     * 获取所有的监听过滤器。
     * 获取的均为自定义过滤器。
     */
    override val filters: List<ListenerFilter>
        get() = _filters.values.toMutableList()

    /**
     * 根据一个名称获取一个对应的过滤器。
     */
    override fun getFilter(name: String): ListenerFilter? = _filters[name]


    /**
     * 通过注解构建一个 [过滤器][ListenerFilter]
     */
    override fun getFilter(filters: Filters): ListenerFilter {
        return AnnotationFiltersListenerFilterImpl(
            filters,
            this,
            filterTargetManager,
            strictManager.coreStrict()
        )
    }


    /**
     * 根据一个msg实例构建一个 [AtDetection] 函数。
     * 如果存在很多 [AtDetection] 实例，则会将他们构建为一个 [AtDetection]，并且会尝试寻找返回true的一个实例。
     */
    override fun getAtDetection(msg: MsgGet): AtDetection {
        // lock for read
        atDetectionUpdateLock.read {
            return _atDetectionFactoryRandomAccessList.let { list ->
                when (list.size) {
                    0 -> AlwaysRefuseAtDetection
                    1 -> list.first().getAtDetection(msg)
                    else -> CompoundAtDetection(msg, list)
                }
            }
        }
    }

    /**
     * 注册一个 [AtDetection] 构建函数。
     * 在尾部追加。
     */
    @Suppress("UNCHECKED_CAST")
    override fun registryAtDetection(atDetectionFactory: AtDetectionFactory) {
        // lock for write
        atDetectionUpdateLock.write {
            atDetectionFactories.addLast(atDetectionFactory)
            if (_atDetectionFactoryRandomAccessList is Deque<*>) {
                // add to last
                (_atDetectionFactoryRandomAccessList as Deque<AtDetectionFactory>).addLast(atDetectionFactory)
            } else {
                // not deque
                _atDetectionFactoryRandomAccessList = atDetectionFactories.toList()
            }
        }
    }

    /**
     * 注册一个 [AtDetection] 构建函数。
     * 在头部追加。
     */
    @Suppress("UNCHECKED_CAST")
    override fun registryAtDetectionFirst(atDetectionFactory: AtDetectionFactory) {
        // lock for write
        atDetectionUpdateLock.write {
            atDetectionFactories.addFirst(atDetectionFactory)
            if (_atDetectionFactoryRandomAccessList is Deque<*>) {
                // add to first
                (_atDetectionFactoryRandomAccessList as Deque<AtDetectionFactory>).addFirst(atDetectionFactory)
            } else {
                // not deque, reset
                _atDetectionFactoryRandomAccessList = atDetectionFactories.toList()
            }
        }
    }

    /**
     * 注册一个 [过滤器][ListenerFilter] 实例。
     *
     * @throws FilterAlreadyExistsException 如果filter已经存在则可能抛出此异常。
     */
    override fun registerFilter(name: String, filter: ListenerFilter) {
        _filters.merge(name, filter) { oldValue, newValue ->
            throw FilterAlreadyExistsException("Duplicate custom filter name: $name, Conflicting filter：'$oldValue' VS '$newValue'")
        }
    }
}

internal object NotInit : AtDetection {
    override fun atBot(): Nothing = error("NotInit")
    override fun atAll(): Nothing = error("NotInit")
    override fun atAny(): Nothing = error("NotInit")
    override fun at(codes: Array<String>): Nothing = error("NotInit")
}


/**
 * 组合式的 [AtDetection] 实现, 其内部记录多个 [AtDetection] 实例并逐一判断。
 */
public class CompoundAtDetection(private val msg: MsgGet, private var detections: List<AtDetectionFactory>) :
    AtDetection {

    // init for array
    private val detectionsArray: Array<AtDetection> = Array(detections.size) { NotInit }

    private val detectionsIterable = DetectionsIterable()

    private inner class DetectionsIterable : Iterable<AtDetection> {
        override fun iterator(): Iterator<AtDetection> = DetectionsIterator()
    }

    @ThreadUnsafe
    private inner class DetectionsIterator : Iterator<AtDetection> {
        private var index: Int = 0
        override fun hasNext(): Boolean = index <= detectionsArray.lastIndex
        override fun next(): AtDetection {
            var next = detectionsArray[index]
            // if not init
            if (next === NotInit) {
                next = detections[index].getAtDetection(msg).also {
                    detectionsArray[index] = it
                }
            }
            index++
            // 如果没有下一个了, 说明都遍历完了, 清除引用
            if (!hasNext()) {
                detections = emptyList()
            }

            return next
        }
    }


    override fun atBot(): Boolean = detectionsIterable.any { it.atBot() }
    override fun atAll(): Boolean = detectionsIterable.any { it.atAll() }
    override fun atAny(): Boolean = detectionsIterable.any { it.atAny() }
    override fun at(codes: Array<String>): Boolean = detectionsIterable.any { it.at(codes) }
}


/**
 * [AtDetectionFactory].invoke(MsgGet)。
 */
internal operator fun AtDetectionFactory.invoke(msg: MsgGet): AtDetection = this.getAtDetection(msg)


/**
 * [FilterManagerBuilder] 默认实现。
 */
public class CoreFilterManagerBuilder(
    private val filterTargetManager: FilterTargetManager,
    private val strictManager: StrictManager
    ) : FilterManagerBuilder {

    data class Filter(val name: String, val filter: ListenerFilter)

    private var filterNameSet = mutableSetOf<String>()
    private var filters = mutableListOf<Filter>()

    /**
     * 注册一个或多个过滤器。
     */
    override fun register(name: String, filter: ListenerFilter): FilterManagerBuilder {
        if (name in filterNameSet) {
            throw IllegalArgumentException("Filter named $name already exists.")
        } else {
            filterNameSet.add(name)
        }
        this.filters.add(Filter(name, filter))
        return this
    }

    /**
     * 构建一个manager
     */
    override fun build(): FilterManager {
        val filterManager = CoreFilterManager(filterTargetManager, strictManager)
        val filters = this.filters
        this.filters = mutableListOf()
        this.filterNameSet = mutableSetOf()

        filters.forEach { (name, filter) ->
            filterManager.registerFilter(name, filter)
        }

        return filterManager
    }
}









