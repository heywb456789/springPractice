import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.minjae.annotaion.Controller;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *  @Controller Annotaion이 있는 모든 클래스를 출력한다.
 */

public class ReflectionTest {

    private static final Logger logger = LoggerFactory.getLogger(ReflectionTest.class);

    @Test
    void ControllerScan(){
        Reflections reflection = new Reflections("org.minjae");

        Set<Class<?>> beans = new HashSet<>();

        beans.addAll(reflection.getTypesAnnotatedWith(Controller.class));

        logger.debug("beans [{}]", beans);
    }
}
