package it.polito.ezshop.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "users")
public class User implements it.polito.ezshop.data.User {

    public enum RoleEnum {Administrator, Cashier, ShopManager}

    @DatabaseField(generatedId = true)
    private Integer id;

    @DatabaseField(unique = true, canBeNull = false)
    private String username;

    @DatabaseField(canBeNull = false)
    private String password;

    @DatabaseField(canBeNull = false)
    private String role;

    User() { }

    public User(String username, String password, RoleEnum role) {
        this.setUsername(username);
        this.setPassword(password);
        this.setRole(role);
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String getRole() {
        return role;
    }

    @Override
    public void setRole(String role) {
        this.role = role;
    }

    public void setRole(RoleEnum role) {
        this.role = role.toString();
    }

}
