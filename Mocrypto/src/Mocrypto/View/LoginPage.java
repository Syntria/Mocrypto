package Mocrypto.View;

import Mocrypto.Helper.Config;
import Mocrypto.Helper.Helper;
import Mocrypto.Helper.SQLConnector;
import Mocrypto.Model.Account;
import Mocrypto.Model.Admin;
import Mocrypto.Model.User;

import javax.swing.*;
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

    public LoginPage(){

        display();
    }

    @Override
    public void display() {
        add(wrapper);
        setSize(400,500);
        setLocation(Helper.screenCenterPoint("x",getSize()),Helper.screenCenterPoint("y",getSize()));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setTitle(Config.PROJECT_TITLE);
        setResizable(false);
        setVisible(true);

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
                            AdminPage adminPage=new AdminPage((Admin) account);
                            break;
                        case "user" :
                            MainPage edGUI=new MainPage(/*(User) account*/);
                            break;

                    }
                    dispose();
                }
            }
        });
    }

    public static Account fetchAccount(String uname,String pass){
        Account obj=null;
        String query="SELECT * FROM account WHERE uname =? AND pass=?";

        try {
            PreparedStatement pr = SQLConnector.getInstance().prepareStatement(query);
            pr.setString(1,uname);
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
                obj.setUsername(rs.getString("username"));
                obj.setPassword(rs.getString("password"));
                obj.setType(rs.getString("type"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return obj;
    }

    // Below code block will be used in registration to check username is taken or not
    /*public static Account fetchAccount(String uname){
        Account obj=null;
        String query="SELECT * FROM account WHERE uname =?";

        try {
            PreparedStatement pr = SQLConnector.getInstance().prepareStatement(query);
            pr.setString(1,uname);
            ResultSet rs=pr.executeQuery();
            if (rs.next()){
                obj=new Account();
                obj.setId(rs.getInt("id"));
                obj.setName(rs.getString("name"));
                obj.setUsername(rs.getString("username"));
                obj.setPassword(rs.getString("password"));
                obj.setType(rs.getString("type"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return obj;
    }*/

    public static void main(String[] args) {
        Helper.setLayout();
        LoginPage login=new LoginPage();
    }
}
