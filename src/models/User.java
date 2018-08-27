package models;

import java.io.Serializable;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author AAGOOGLE
 */
public class User extends Entity implements Serializable {

    public static enum Action {
        LOGIN, LOGOUT
    }

    private static User staticInstance;

    static {
        staticInstance = new User();
    }

    {
        setTableName("users");
    }

    public User() {
        super();
    }

    public User(Integer id) {
        super(id);
    }

    public User(String name) {
        super(name);
    }

    public User(Integer id, String name) {
        super(id, name);
    }

    public static User getStaticInstance() {
        return staticInstance;
    }
}
