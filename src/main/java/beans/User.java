package beans;

public class User {
    private String login;

    public String getLogin() {
        return login;
    }

    private String password;

    public Role getRole() {
        return role;
    }

    private Role role;

    public User(String login, String password) {
        this.login = login;
        this.password = password;
        this.role = Role.USER;
    };

    public User(String login, String password, Role role) {
        this.login = login;
        this.password = password;
        this.role = role;
    }
}
