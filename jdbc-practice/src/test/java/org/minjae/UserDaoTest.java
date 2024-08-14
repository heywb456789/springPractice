package org.minjae;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * packageName       : org.minjae
 * fileName         : UserDaoTest
 * author           : MinjaeKim
 * date             : 2024-08-14
 * description      :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-08-14        MinjaeKim       최초 생성
 */
public class UserDaoTest {

    /**
     * 테스트 수행 전 실행하는 코드
     *
     */
    @BeforeEach
    void setUp() {
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(new ClassPathResource("db_schema.sql"));

        DatabasePopulatorUtils.execute(populator, ConnectionManager.getDataSource());
    }

    @Test
    void createTest() throws SQLException {
        UserDao userDao = new UserDao();
        userDao.create(new User("wizard", "password", "name", "email"));

        User user = userDao.findByUserId("wizard");

        assertThat(user).isEqualTo(new User("wizard", "password", "name", "email"));
    }
}
