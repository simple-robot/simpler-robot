package love.forte.test;

import love.forte.common.configuration.annotation.ConfigIgnore;
import love.forte.common.configuration.annotation.ConfigInject;

import java.util.List;

/**
 * @author <a href="https://github.com/ForteScarlet"> ForteScarlet </a>
 */
public class TestParent {
    @ConfigInject(value = "user.password", orNull = true)
    private String password;

    @ConfigIgnore
    private List<Integer> list;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Integer> getList() {
        return list;
    }
    @ConfigInject("values")
    public void setList(List<Integer> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        return "TestParent{" +
                "password='" + password + '\'' +
                ", list=" + list +
                '}';
    }
}
