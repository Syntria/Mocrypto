package Mocrypto.Model;

public class Admin extends Account{

    public Admin() {
    }

    public Admin(int id, String name, String surname, String username, String password, String type) {
        super(id, name, surname,username, password, type);
    }
}
