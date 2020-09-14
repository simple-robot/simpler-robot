package love.forte.test

import love.forte.common.configuration.annotation.AsConfig
import love.forte.common.configuration.annotation.ConfigInject

@AsConfig(prefix = "user", allField = true)
data class TestKTConf(
    var name: String = "",
    @ConfigInject("user.age", ignorePrefix = true)
    var age: Int = -1
)

@AsConfig(prefix = "user", allField = true)
class TestKTConf2 {

    lateinit var name: String

    @ConfigInject("user.age", ignorePrefix = true)
    var age: Int = -1


    override fun toString(): String {
        return "TestKTConf2(name='$name', age=$age)"
    }

}