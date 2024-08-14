package org.minjae;

import java.util.Objects;

/**
 * packageName       : org.minjae
 * fileName         : User
 * author           : MinjaeKim
 * date             : 2024-08-14
 * description      :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-08-14        MinjaeKim       최초 생성
 */
public class User {

    private final String email;
    private final String userId;
    private final String password;
    private final String name;

    public User(String userId, String password, String name, String email) {
        this.userId = userId;
        this.password = password;
        this.name = name;
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public String getUserID() {
        return userId;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(email, user.email) && Objects.equals(userId, user.userId) && Objects.equals(password, user.password) && Objects.equals(name, user.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email, userId, password, name);
    }
}
