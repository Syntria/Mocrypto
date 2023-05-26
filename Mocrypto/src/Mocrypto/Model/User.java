package Mocrypto.Model;

public class User extends Account{

    public User() {
    }

    public User(int id, String name, String username, String password, String type) {

        super(id, name, username, password, type);
    }
}
