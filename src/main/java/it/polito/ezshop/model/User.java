package it.polito.ezshop.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "users")
public class User {

    public enum RoleEnum {Administrator, Cashier, ShopManager}

    @DatabaseField(generatedId = true)
    private Integer id;

    @DatabaseField(unique = true, canBeNull = false)
    private String username;

    @DatabaseField(canBeNull = false)
    private String password;

    @DatabaseField(canBeNull = false)
    private RoleEnum role;

    User() { }

    public User(String username, String password, RoleEnum role) {
        this.setUsername(username);
        this.setPassword(password);
        this.setRole(role);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public RoleEnum getRole() {
        return role;
    }

    public void setRole(RoleEnum role) {
        this.role = role;
    }

}
