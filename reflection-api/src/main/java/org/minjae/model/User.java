package org.minjae.model;

import java.util.Objects;

/**
 * @author : MinjaeKim
 * @packageName : org.minjae.model
 * @fileName : User
 * @date : 2024-11-13
 * @description : ===========================================================
 * @DATE @AUTHOR       @NOTE ----------------------------------------------------------- 2024-11-13
 * MinjaeKim       최초 생성
 */
public class User {

    private String userId;
    private String name;

    public User(String userId, String name) {
        this.userId = userId;
        this.name = name;
    }

    public boolean equalsUser(User user) {
        return this.equals(user);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        User user = (User) o;
        return Objects.equals(userId, user.userId) && Objects.equals(name,
            user.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, name);
    }
}
