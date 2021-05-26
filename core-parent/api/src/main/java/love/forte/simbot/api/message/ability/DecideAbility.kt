/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  component-onebot
 *  * File     DecideAbility.kt
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

package love.forte.simbot.api.message.ability


/**
 * '接受'决策的能力。
 */
public interface AcceptDecideAbility<ACCEPT> : Ability {
    /**
     * 进行决策：接受。例如接受一个请求。[accept] 为一个在接受的时候所需要的参数。
     */
    fun accept(accept: ACCEPT): Boolean
}

/**
 * '拒绝'决策的能力。
 */
public interface RejectDecideAbility<REJECT> : Ability {
    /**
     * 进行决策；拒绝。例如拒绝一个请求。[reject] 为一个在拒绝的时候所需要的参数。
     */
    fun reject(reject: REJECT): Boolean
}

/**
 * '纯粹接受'决策的能力。
 */
public interface PureAcceptDecideAbility<ACCEPT> : AcceptDecideAbility<ACCEPT?> {

    /**
     * 一个不需要参数的'接受'决策。默认情况下，其等同于当决策参数为null的时候。
     */
    // @JvmDefault
    fun accept(): Boolean = accept(null)


    /**
     * 进行决策：接受。例如接受一个请求。[accept] 为一个在接受的时候所需要的参数。
     */
    override fun accept(accept: ACCEPT?): Boolean
}

/**
 * '纯粹拒绝'决策的能力。
 */
public interface PureRejectDecideAbility<REJECT> : RejectDecideAbility<REJECT?> {

    /**
     * 一个不需要参数的'拒绝'决策。默认情况下，其等同于当决策参数为null的时候。
     */
    // @JvmDefault
    fun reject(): Boolean = reject(null)

    /**
     * 进行决策；拒绝。例如拒绝一个请求。[reject] 为一个在拒绝的时候所需要的参数。
     */
    override fun reject(reject: REJECT?): Boolean
}


/**
 * 可进行 **决策** 的接口。
 * 此接口存在一个任意泛型，来规定做出决策的时候是否需要什么。
 */
public interface DecideAbility<ACCEPT, REJECT> :
    AcceptDecideAbility<ACCEPT>,
    RejectDecideAbility<REJECT> {
    /**
     * 进行决策：接受。例如接受一个请求。[accept] 为一个在接受的时候所需要的参数。
     */
    override fun accept(accept: ACCEPT): Boolean

    /**
     * 进行决策；拒绝。例如拒绝一个请求。[reject] 为一个在拒绝的时候所需要的参数。
     */
    override fun reject(reject: REJECT): Boolean
}


/**
 * 一个仅需要 **纯粹** 的进行 **决策** 的接口。
 * 所谓纯粹，即进行决策的时候不需要参数，或者有一个最终不需要参数的重载。
 */
public interface PureDecideAbility<ACCEPT, REJECT> :
    DecideAbility<ACCEPT?, REJECT?>,
    PureAcceptDecideAbility<ACCEPT>, PureRejectDecideAbility<REJECT>
