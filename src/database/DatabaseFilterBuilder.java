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
public class DatabaseFilterBuilder implements Serializable {

    StringBuilder databaseFilterBuilder = new StringBuilder();

    public DatabaseFilterBuilder where(DatabaseFilter databaseFilter) {
        databaseFilterBuilder
                .append(" WHERE ")
                .append(databaseFilter.getKey())
                .append("=")
                .append("'").append(databaseFilter.getValue()).append("'");

        return this;
    }

    public DatabaseFilterBuilder and(DatabaseFilter databaseFilter) {
        databaseFilterBuilder
                .append(" AND ")
                .append(databaseFilter.getKey())
                .append("=")
                .append("'").append(databaseFilter.getValue()).append("'");

        return this;

    }

    public DatabaseFilterBuilder or(DatabaseFilter databaseFilter) {
        databaseFilterBuilder
                .append(" OR ")
                .append(databaseFilter.getKey())
                .append("=")
                .append("'").append(databaseFilter.getValue()).append("'");

        return this;
    }

    @Override
    public String toString() {
        return databaseFilterBuilder.toString();
    }
}
