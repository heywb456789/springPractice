package org.minjae.mvc.model;

/**
 * @author : MinjaeKim
 * @packageName : org.minjae.mvc.model
 * @fileName : User
 * @date : 2025-02-06
 * @description : ===========================================================
 * @DATE @AUTHOR       @NOTE ----------------------------------------------------------- 2025-02-06
 * MinjaeKim       최초 생성
 */
public class User {
    private final String name;
    private final String userId;

    public User(String name, String userId) {
        this.name = name;
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }
}
