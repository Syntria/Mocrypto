package Mocrypto.View;

import Mocrypto.Helper.Config;
import Mocrypto.Helper.Helper;
import Mocrypto.Helper.SQLConnector;
import Mocrypto.Model.Account;
import Mocrypto.Model.Admin;
import Mocrypto.Model.User;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginPage extends JFrame implements IPage{
    private JPanel wrapper;
    private JPanel wtop;
    private JPanel wbottom;
    private JTextField fld_user_uname;
    private JPasswordField fld_user_pass;
    private JButton btn_login;
    private JButton btn_register;

    Account account;
    public LoginPage(){

        display();
        btn_register.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                dispose();
                RegisterPage registerPage = new RegisterPage();
            }
        });
    }

    @Override
    public void display() {

        // Arranging page display properties
        add(wrapper);
        setSize(400,500);
        setLocation(Helper.screenCenterPoint("x",getSize()),Helper.screenCenterPoint("y",getSize()));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setTitle(Config.PROJECT_TITLE);
        setResizable(false);
        setVisible(true);

        // Opening page corresponding to account type
        btn_login.addActionListener(e -> {
            if(Helper.isFieldEmpty(fld_user_uname) || Helper.isFieldEmpty(fld_user_pass)){
                Helper.showMsg("fill");
            }else {
                Account account = fetchAccount(fld_user_uname.getText(),fld_user_pass.getText());
                if(account == null){
                    Helper.showMsg("Account not found.");
                }else{
                    switch (account.getType()){
                        case "admin" :
                            AdminPage adminPage = new AdminPage((Admin) account);
                            break;
                        case "user" :
                            MainPage mainPage = new MainPage((User) account);
                            break;

                    }
                    dispose();
                }
            }
        });
    }

    // Retrieving account from database with respect to specified username and password
    public static Account fetchAccount(String username,String pass){
        Account obj=null;
        String query="SELECT * FROM account WHERE username=? AND password=?";

        try {
            PreparedStatement pr = SQLConnector.getInstance().prepareStatement(query);
            pr.setString(1,username);
            pr.setString(2,pass);
            ResultSet rs=pr.executeQuery();
            if (rs.next()){
                switch (rs.getString("type")){
                    case "user":
                        obj=new User();
                        break;
                    case "admin":
                        obj=new Admin();
                        break;
                    default:
                        Helper.showMsg("error");
                }

                obj.setId(rs.getInt("id"));
                obj.setName(rs.getString("name"));
                obj.setSurname(rs.getString("surname"));
                obj.setUsername(rs.getString("username"));
                obj.setPassword(rs.getString("password"));
                obj.setType(rs.getString("type"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return obj;
    }

    // Creating necessary tables in MySQL database
    private static void initializeDatabase () throws SQLException {

        String query = "DROP TABLE `account`;";

        PreparedStatement pr = SQLConnector.getInstance().prepareStatement(query);;


        pr.execute();

        query = "DROP TABLE `cryptocurrency`;";

        pr = SQLConnector.getInstance().prepareStatement(query);
        pr.execute();

         query="CREATE TABLE `account` (\n" +
                "\t`id` INT NOT NULL AUTO_INCREMENT,\n" +
                "\t`name` VARCHAR(255) NOT NULL,\n" +
                "\t`surname` VARCHAR(255) NOT NULL,\n" +
                "\t`username` VARCHAR(255) NOT NULL,\n" +
                "\t`password` VARCHAR(255) NOT NULL,\n" +
                "\t`balance` DOUBLE NOT NULL,\n" +
                "\t`type` VARCHAR(255) NOT NULL,\n" +
                "\tPRIMARY KEY (`id`)\n" +
                ");";

        pr = SQLConnector.getInstance().prepareStatement(query);
        pr.execute();


        query="CREATE TABLE `cryptocurrency` (\n" +
                "\t`uuid` VARCHAR(255) NOT NULL,\n" +
                "\t`name` VARCHAR(255) NOT NULL,\n" +
                "\t`shortname` VARCHAR(255) NOT NULL,\n" +
                "\t`price` DOUBLE NOT NULL,\n" +
                "\t`volume` DOUBLE,\n" +
                "\tPRIMARY KEY (`uuid`)\n" +
                ");";

        pr = SQLConnector.getInstance().prepareStatement(query);
        pr.execute();



        query="CREATE TABLE `transaction` (\n" +
                "\t`id` INT NOT NULL AUTO_INCREMENT,\n" +
                "\t`type` VARCHAR(4) NOT NULL,\n" +
                "\t`amount` DOUBLE NOT NULL,\n" +
                "\t`bought_coin` VARCHAR(255) NOT NULL,\n" +
                "\t`sold_coin` VARCHAR(255) NOT NULL,\n" +
                "\t`time_stamp` DOUBLE NOT NULL,\n" +
                "\t`recipient_address` VARCHAR(255) NOT NULL,\n" +
                "\tPRIMARY KEY (`id`)\n" +
                ");";

        pr = SQLConnector.getInstance().prepareStatement(query);
        pr.execute();


        query = "CREATE TABLE transaction (user_id INT NOT NULL, type VARCHAR(255) NOT NULL, amount DOUBLE NOT NULL, base_crypto VARCHAR(255) NOT NULL, target_crypto VARCHAR(255) NOT NULL, time_stamp VARCHAR(255) NOT NULL);";

        pr = SQLConnector.getInstance().prepareStatement(query);
        pr.execute();

    }




    public static void main(String[] args) throws SQLException {

        //initializeDatabase();

        Helper.setLayout();
        LoginPage login=new LoginPage();
    }
}
