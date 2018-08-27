package models;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.Serializable;

/**
 *
 * @author AAGOOGLE
 */
public class Todo extends Entity implements Serializable {

    private static Todo staticInstance;

    static {
        staticInstance = new Todo();
    }

    {
        setTableName("todos");
    }
    private Integer userId;
    private Boolean completed;
    private Boolean isPrivate;
    private String user;

    public Todo() {
        super();
    }

    public Todo(Integer id) {
        super(id);
    }

    public Todo(String name, Integer userId, Boolean completed, Boolean isPrivate) {
        super(name);
        this.userId = userId;
        this.completed = completed;
        this.isPrivate = isPrivate;
    }

    public Todo(Integer id, String name, Integer userId, Boolean completed, Boolean isPrivate) {
        super(id, name);

        this.userId = userId;
        this.completed = completed;
        this.isPrivate = isPrivate;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public void setIsPrivate(Boolean isPrivate) {
        this.isPrivate = isPrivate;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getUser() {
        return this.user;
    }

    public Integer getUserId() {
        return this.userId;
    }

    public Boolean isCompleted() {
        return this.completed;
    }

    public Boolean getIsPrivate() {
        return this.isPrivate;
    }

    public static Todo getStaticInstance() {
        return staticInstance;
    }

}
