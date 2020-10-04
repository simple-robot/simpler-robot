// /*
//  * Copyright (c) 2020. ForteScarlet All rights reserved.
//  * Project  parent
//  * File     Listener.kt
//  *
//  * You can contact the author through the following channels:
//  * github https://github.com/ForteScarlet
//  * gitee  https://gitee.com/ForteScarlet
//  * email  ForteScarlet@163.com
//  * QQ     1149159218
//  */
//
// package love.forte.simbot.core.api.listener
//
//
// /**
//  * 定义一个**监听函数**。
//  * @author ForteScarlet -> https://github.com/ForteScarlet
//  */
// interface Listener : Comparable<Listener> {
//
//     /**
//      * 代表了当前listener的唯一ID。
//      */
//     val id: String
//
//     /**
//      * 此监听函数的名称。
//      */
//     val name: String
//
//
//     /**
//      * 执行监听函数，并得到一个执行结果
//      */
//     fun invoke(): ListenResult<*>
//
//
// }