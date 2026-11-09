package bank.model;

// A bank staff member who can log in. The role decides what they may do
public class User {
    private String username;
    private String password;
    private Role role;

    public User(String username, String password, Role role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public boolean passwordMatches(String attempt) {
        return password.equals(attempt);
    }

    public boolean isManager() {
        return role == Role.MANAGER;
    }

    public String getUsername() { return username; }
    public Role getRole() { return role; }
}
