package Mocrypto.View;

import Mocrypto.Helper.Config;
import Mocrypto.Helper.Helper;
import Mocrypto.Helper.SQLConnector;
import Mocrypto.Model.Account;
import Mocrypto.Model.User;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RegisterPage extends JFrame implements IPage{
    private JPanel pnl_user_register;
    private JPanel pnl_user_form;
    private JButton btn_user_register;
    private JTextField fld_user_name;
    private JTextField fld_user_username;
    private JTextField fld_user_password;
    private JTextField fld_user_surname;
    private JPanel wrapper;

    public RegisterPage() {

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

        btn_user_register.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if(Helper.isFieldEmpty(fld_user_name)|| Helper.isFieldEmpty(fld_user_surname) || Helper.isFieldEmpty(fld_user_username)
                        ||Helper.isFieldEmpty(fld_user_password)){
                    Helper.showMsg("fill");
                }else{
                    String name=fld_user_name.getText();
                    String surname=fld_user_surname.getText();
                    String username=fld_user_username.getText();
                    String pass=fld_user_password.getText();
                    if(add(name,surname,username,pass)){
                        Helper.showMsg("done");
                        dispose();
                        LoginPage loginPage = new LoginPage();

                    }
                }

            }
        });

    }

    public static boolean add(String name,String surname,String username,String password){
        String query="INSERT INTO account (name,surname,username,password,type) VALUES (?,?,?,?,10000,'user')";
        Account findUser = RegisterPage.fetchAccount(username);
        if(findUser!=null){
            Helper.showMsg("This username is taken, please choose another username");
            return false;
        }
        try {
            PreparedStatement pr=SQLConnector.getInstance().prepareStatement(query);
            pr.setString(1,name);
            pr.setString(2,surname);
            pr.setString(3,username);
            pr.setString(4,password);
            int response= pr.executeUpdate();

            if(response == -1){
                Helper.showMsg("error");
            }
            return response != -1;

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return true;
    }

    public static Account fetchAccount(String username){
        Account obj=null;
        String query="SELECT * FROM account WHERE username =?";

        try {
            PreparedStatement pr = SQLConnector.getInstance().prepareStatement(query);
            pr.setString(1,username);
            ResultSet rs=pr.executeQuery();
            if (rs.next()){
                obj=new User();
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

    public static void main(String[] args) {
        RegisterPage asd = new RegisterPage();
    }
}
