package test

import love.forte.simbot.*
import love.forte.simbot.core.event.*


class TestComponent(
    val name: String,
    val age: Int
) : Component {
    override val id: ID = ID.ID

    override fun toString(): String {
        return "TestComponent(name=$name, age=$age)"
    }

    companion object Registrar : ComponentRegistrar<TestComponent, TestComponentConfiguration> {
        const val ID = "simbot.test.1"

        override val key: Attribute<TestComponent> = attribute(ID)

        override fun register(block: TestComponentConfiguration.() -> Unit): TestComponent {
            val configuration = TestComponentConfiguration().also(block)

            return TestComponent(configuration.name, configuration.age)
        }
    }


}


class TestComponentConfiguration {
    var name: String = ""
    var age: Int = 5
}

@OptIn(ExperimentalSimbotApi::class)
fun main() {
    val manager = coreListenerManager {
        install(TestComponent) {
            age = 114
            name = "forte"
        }
    }


    println(manager.getComponent(TestComponent.ID))

}