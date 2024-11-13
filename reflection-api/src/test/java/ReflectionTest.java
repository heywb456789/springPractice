import static org.assertj.core.api.Assertions.assertThat;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.minjae.annotaion.Controller;
import org.minjae.annotaion.Service;
import org.minjae.model.User;
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
        Set<Class<?>> beans = getTypesAnnotatedWith(List.of(Controller.class, Service.class));

        logger.debug("beans [{}]", beans);
    }

    private static Set<Class<?>> getTypesAnnotatedWith(List<Class<? extends Annotation>> annotations) {
        Reflections reflection = new Reflections("org.minjae");

        Set<Class<?>> beans = new HashSet<>();
        annotations.forEach(annotation -> {
            beans.addAll(reflection.getTypesAnnotatedWith(annotation));
        });
//        beans.addAll(reflection.getTypesAnnotatedWith(Controller.class));
//        beans.addAll(reflection.getTypesAnnotatedWith(Service.class));
        return beans;
    }

    @Test
    void showClass() {
        Class<User> clazz = User.class;
        logger.debug("class [{}]", clazz.getName());

        logger.debug(" User All Declared fields  [{}]", Arrays.stream(clazz.getDeclaredFields()).collect(
            Collectors.toList()));

        logger.debug(" User All Declared contruct  [{}]", Arrays.stream(clazz.getDeclaredConstructors()).collect(
            Collectors.toList()));

        logger.debug(" User All Declared methods  [{}]", Arrays.stream(clazz.getDeclaredMethods()).collect(
            Collectors.toList()));
    }

    //힙영역에서 불러오는 방법 1.
    @Test
    void load() throws ClassNotFoundException {
        //1.
        Class<User> clazz = User.class;

        //2.
        User user = new User("John", "Doe");
        Class<? extends User> clazz2 = User.class;

        //3.
        Class<?> clazz3 = Class.forName("org.minjae.model.User");

        logger.debug("class [{}]", clazz.getName());
        logger.debug("class2 [{}]", clazz2.getName());
        logger.debug("class3 [{}]", clazz3.getName());

        assertThat(clazz==clazz2).isTrue();
        assertThat(clazz==clazz3).isTrue();
        assertThat(clazz2==clazz3).isTrue();

    }





}
