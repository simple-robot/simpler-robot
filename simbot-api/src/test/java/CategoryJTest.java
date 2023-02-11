import love.forte.simbot.Identifies;
import love.forte.simbot.definition.Category;
import love.forte.simbot.definition.SimpleCategory;
import org.junit.jupiter.api.Test;

/**
 * @author ForteScarlet
 */
public class CategoryJTest {

    @Test
    public void equalsTest() {
        final SimpleCategory c1 = Category.of("123");
        final SimpleCategory c2 = Category.of(Identifies.ID(123), "123");
        final SimpleCategory c3 = Category.of(Identifies.ID(123));

        assert c1.equals(c2);
        assert c1.equals(c3);
        assert c2.equals(c3);
    }

}
