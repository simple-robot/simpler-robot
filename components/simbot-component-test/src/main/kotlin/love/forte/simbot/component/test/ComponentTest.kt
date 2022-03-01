/*
 *  Copyright (c) 2022 ForteScarlet <https://github.com/ForteScarlet>
 *
 *  本文件是 simply-robot (或称 simple-robot 3.x、simbot 3.x、simbot3) 的一部分。
 *
 *  simply-robot 是自由软件：你可以再分发之和/或依照由自由软件基金会发布的 GNU 通用公共许可证修改之，无论是版本 3 许可证，还是（按你的决定）任何以后版都可以。
 *
 *  发布 simply-robot 是希望它能有用，但是并无保障;甚至连可销售和符合某个特定的目的都不保证。请参看 GNU 通用公共许可证，了解详情。
 *
 *  你应该随程序获得一份 GNU 通用公共许可证的复本。如果没有，请看:
 *  https://www.gnu.org/licenses
 *  https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *  https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 */

package love.forte.simbot.component.test

import love.forte.simbot.*

/**
 * test组件的 [组件][Component] 实例信息。
 */
public object ComponentTest {
    /**
     * test组件唯一标识的字符串常量。
     */
    public const val COMPONENT_ID: String = "simbot.test"

    /**
     * 一个默认的头像图片地址。
     */
    public const val DEFAULT_AVATAR: String = "https://p.qlogo.cn/gh/782930037/782930037/640"

    private lateinit var realComponent: Component

    /**
     * test 组件的 [组件][Component] 实例。
     */
    @JvmStatic
    public val component: Component
        get() {
            if (::realComponent.isInitialized) {
                return realComponent
            }
            return Components[COMPONENT_ID]
        }


    internal object TestComponentInformation : ComponentInformation {
        override val id: ID = COMPONENT_ID.ID
        override val name: String = "simbot.test"

        override fun configAttributes(attributes: MutableAttributeMap) {
        }

        override fun setComponent(component: Component) {
            realComponent = component
        }
    }
}


/**
 * test组件中的信息注册器。
 */
public class TestComponentInformationRegistrar : ComponentInformationRegistrar {
    override fun informations(): ComponentInformationRegistrar.Result {
        return ComponentInformationRegistrar.Result.ok(listOf(ComponentTest.TestComponentInformation))
    }
}

