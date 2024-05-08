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

}
