package love.forte.test

import love.forte.common.configuration.annotation.AsConfig
import love.forte.common.configuration.annotation.ConfigIgnore
import love.forte.common.configuration.annotation.ConfigInject

@AsConfig(prefix = "user", allField = true)
data class TestKTConf(
    var name: String = "",
    @ConfigInject("user.age", ignorePrefix = true)
    var age: Int = -1
)

@AsConfig(prefix = "user", allField = true)
public class TestKTConf2 {

    lateinit var name: String

    @ConfigInject("user.age2", ignorePrefix = true)
    var age: Int = -1

    // @field:ConfigIgnore
    // var size: Int = 20

    override fun toString(): String {
        return "TestKTConf2(name='$name', age=$age)"
    }

}