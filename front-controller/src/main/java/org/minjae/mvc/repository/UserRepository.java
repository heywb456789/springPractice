package org.minjae.mvc.repository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.minjae.mvc.model.User;

/**
 * @author : MinjaeKim
 * @packageName : org.minjae.mvc.repository
 * @fileName : UserRepository
 * @date : 2025-02-06
 * @description : ===========================================================
 * @DATE @AUTHOR       @NOTE ----------------------------------------------------------- 2025-02-06
 * MinjaeKim       최초 생성
 */
public class UserRepository {

    private static Map<String, User> users = new HashMap<>();

    public static void save(User user) {
        users.put(user.getUserId(), user);
    }
    public static Collection<User> findAll() {
        return users.values();
    }

}
