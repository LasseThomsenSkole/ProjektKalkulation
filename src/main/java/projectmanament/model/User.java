package projectmanament.model;

public class User {
    private int id;
    private String name;
    private String password;
    private boolean isAdmin;

    public User(){} //thymeleaf

    public User(int id, String name, String password, boolean isAdmin) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.isAdmin = isAdmin;
    }

}
