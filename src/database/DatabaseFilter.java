/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import java.io.Serializable;

/**
 *
 * @author AAGOOGLE
 */
public class DatabaseFilter implements Serializable{

    private final String key;
    private final Object value;

    public DatabaseFilter(String key, Object value) {
        this.key = key;
        if (value instanceof Boolean) {
            value = (Boolean) value == false ? "0" : "1";
        }
        this.value = String.valueOf(value);
    }

    public String getKey() {
        return this.key;
    }

    public Object getValue() {
        return this.value;
    }
}
