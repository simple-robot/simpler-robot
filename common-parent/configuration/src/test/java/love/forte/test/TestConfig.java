package love.forte.test;

import love.forte.common.configuration.annotation.AsConfig;
import love.forte.common.configuration.annotation.ConfigInject;

/**
 * @author <a href="https://github.com/ForteScarlet"> ForteScarlet </a>
 */
@AsConfig(prefix = "user", allField = true)
public class TestConfig extends TestParent {

    @ConfigInject("name")
    private String name;

    @ConfigInject(value = "user.age2", ignorePrefix = true)
    private Integer age;


    public int getAge() {
        return age;
    }

    @ConfigInject(value = "user.age", ignorePrefix = true)
    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "TestConfig{" +
                "name='" + name + '\'' +
                ", age=" + age +
                "} " + super.toString();
    }
}
