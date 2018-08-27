/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

import database.DatabaseFilter;
import database.DatabaseFilterBuilder;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.rmi.RemoteException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import rmi.RemoteEntityImpl;

/**
 *
 * @author AAGOOGLE
 */
public abstract class Entity implements Serializable {

    public static enum Action {
        CREATE, READ, UPDATE, DELETE
    }

    private Integer id;
    private String name;
    public String tableName;
    private List<String> fields = null;

    private String[] ignoreFieldsArray = {
        "ignoreFieldsArray",
        "ignoreGettersArray",
        "staticInstance",
        "id",
        "user",
        "tableName",
        "fields",
        "todos"
    };

    public Entity() {
    }

    public Entity(Integer id) {
        this.id = id;
    }

    public Entity(String name) {
        this.name = name;
    }

    public Entity(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public void setValue(String fieldName, Object value) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, ClassNotFoundException {
        Method setMethod = this.getSetterMethods().stream()
                .filter(method -> method.getName().toLowerCase().substring(3).matches(fieldName.toLowerCase()))
                .findFirst()
                .orElse(null);
        if (setMethod != null && value != null) {
            setMethod.invoke(this, value);
        }
    }

    public Integer getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getTableName() {
        return this.tableName;
    }

    public Field getField(Object fieldName) {
        return Stream.concat(Arrays.stream(this.getClass().getSuperclass().getDeclaredFields()),
                Arrays.stream(this.getClass().getDeclaredFields()))
                .filter(field -> field.getName().toLowerCase().matches(String.valueOf(fieldName).toLowerCase()))
                .findFirst()
                .orElse(null);
    }

    public List<String> getFieldsExcept(String... ignoreFieldsArray) {
        try {
            if (ignoreFieldsArray == null || ignoreFieldsArray.length <= 0) {
                ignoreFieldsArray = this.ignoreFieldsArray;
            }
            String ignoreFieldsNames = String.join("|", ignoreFieldsArray);

            fields = Stream.concat(Arrays.stream(this.getClass().getSuperclass().getDeclaredFields())
                    .filter(field -> !(field.getName().matches("(?i)" + ignoreFieldsNames))),
                    Arrays.stream(this.getClass().getDeclaredFields())
                            .filter(field -> !(field.getName().matches("(?i)" + ignoreFieldsNames))))
                    .map(Field::getName)
                    .collect(Collectors.toList());
        } catch (SecurityException | IllegalArgumentException ex) {
            System.err.println(this.getClass().getName() + " " + ex);
            ex.printStackTrace(System.err);
        }
        return fields;
    }

    public Object[] getValuesExcept(String... ignoreValuesArray) {
        if (ignoreValuesArray == null || ignoreValuesArray.length <= 0) {
            ignoreValuesArray = this.ignoreFieldsArray;
        }
        List<String> fields = getFieldsExcept(ignoreValuesArray);
        List<Method> methods = this.getGetterMethods();
        List<Object> values = new ArrayList<>(4);
        for (String field : this.fields) {
            for (Method method : methods) {
                String methodName = method.getName().startsWith("get")
                        ? method.getName().toLowerCase().substring(3) // startsWith("get")
                        : method.getName().toLowerCase().substring(2); // startsWith("is")

                if (methodName.toLowerCase().matches(field.toLowerCase())) {
                    try {
                        Object value = method.invoke(this);
                        values.add(value);
                    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                        System.err.println(this.getClass().getName() + " " + ex);
                        ex.printStackTrace(System.err);
                    }
                }
            }
        }
        return values.toArray();
    }

    public Object getValue(String fieldName) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Method getMethod = this.getGetterMethods()
                .stream()
                .filter((Method method) -> {
                    String methodName = method.getName().startsWith("get")
                            ? method.getName().toLowerCase().substring(3) // startsWith("get")
                            : method.getName().toLowerCase().substring(2); // startsWith("is")
                    return methodName.toLowerCase().matches(fieldName.toLowerCase());
                })
                .findFirst()
                .orElse(null);
        return getMethod.invoke(this);
    }

    public String joinFields(String Separator) {
        return String.join(Separator, getFieldsExcept());
    }

    public List<Method> getSetterMethods(String... ignoreSettersArray) {
        ignoreSettersArray = (ignoreSettersArray != null || ignoreSettersArray.length > 0) ? ignoreSettersArray : new String[]{"setTableName"};
        String ignoreSettersNames = String.join("|", ignoreSettersArray);
        List<Method> setterMethods = Arrays.stream(this.getClass().getMethods()).filter(method -> {
            return (Modifier.isPublic(method.getModifiers())
                    && method.getParameterTypes().length != 0
                    && method.getReturnType() == void.class
                    && ((method.getName().startsWith("set") && !method.getName().matches("(?i)" + ignoreSettersNames))));
        }).collect(Collectors.toList());

        return setterMethods;
    }

    public List<Method> getGetterMethods(String... ignoreGettersArray) {
        String ignoreGettersNames = String.join("|", ignoreGettersArray);

        List<Method> getterMethods = Arrays.stream(this.getClass().getMethods()).filter((Method method) -> {
            return (Modifier.isPublic(method.getModifiers())
                    && method.getParameterTypes().length == 0
                    && method.getReturnType() != void.class
                    && ((method.getName().startsWith("get") || method.getName().startsWith("is"))
                    && !method.getName().matches("(?i)" + ignoreGettersNames)));
        }).collect(Collectors.toList());

        return getterMethods;
    }

    public void fillFields(ResultSet rs) throws SQLException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, RemoteException {
        List<Method> setterMethods = this.getSetterMethods();
        ResultSetMetaData rsmd = rs.getMetaData();
        int columnCount = rsmd.getColumnCount();

        for (int i = 1; i <= columnCount; i++) {
            String fieldName = rsmd.getColumnName(i).toLowerCase();
            for (Method method : setterMethods) {
                String methodName = method.getName().toLowerCase().substring(3);
                if (methodName.matches(fieldName)) {
                    method.invoke(this, rs.getObject(fieldName));
                }
            }
        }

        if (this instanceof Todo) {
            ((Todo) this).setUser(new RemoteEntityImpl().read(User.class, new DatabaseFilterBuilder().where(new DatabaseFilter("id", rs.getInt("userId"))).toString()).getName());
        }
    }

    @Override
    public String toString() {
        return Arrays.stream(getValuesExcept()).map(value -> {
            if (value instanceof Boolean) {
                value = value.equals(Boolean.TRUE) ? "1" : "0";
            }
            return String.valueOf(value);
        }).collect(Collectors.joining("','", "'", "'"));
    }

}
