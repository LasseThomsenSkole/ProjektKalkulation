package projectmanament.model;

public class User {
    private int id;
    private String name;
    private String password;

    private boolean isAdmin;

    public User(){} //thymeleaf

    public User(int id, String name, boolean isAdmin, String password) {
        this.id = id;
        this.name = name;
        this.isAdmin = isAdmin;
        this.password = password;

    }

    public int getId() {
        return id;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }
}
