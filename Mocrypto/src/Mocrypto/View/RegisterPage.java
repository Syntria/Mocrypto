package Mocrypto.View;

import Mocrypto.Helper.Config;
import Mocrypto.Helper.Helper;
import Mocrypto.Helper.SQLConnector;
import Mocrypto.Model.Account;
import Mocrypto.Model.Cryptocurrency;
import Mocrypto.Model.User;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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

        // Arranging page display properties
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
                        System.out.println("searched username : " + username);
                        add(username); // Initialize the user portfolio (added to the database)
                        Helper.showMsg("done");
                        dispose();
                        LoginPage loginPage = new LoginPage();
                    }
                }

            }
        });

    }

    // Function for adding user to database
    public static boolean add(String name,String surname,String username,String password){
        String query="INSERT INTO account (name,surname,username,password,balance,type) VALUES (?,?,?,?,10000,'user')";
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



    // Function for adding default cryptocurrency to user portfolio
    public boolean add(String searchedUsername){

         String query="INSERT INTO portfolio (user_id,uuid,short_name,name,amount) VALUES (?,?,?,?,?)";
         String query2="SELECT * FROM account WHERE username =?";

         try {
             Cryptocurrency cryptocurrency = new Cryptocurrency("HIVsRcGKkPFtW","Tether USD","USDT",1.0,18942563028.0);
             cryptocurrency.setAmount(10000);

             PreparedStatement pr2 = SQLConnector.getInstance().prepareStatement(query2);

             pr2.setString(1,searchedUsername);


             ResultSet rs=pr2.executeQuery();

             int userID = -1;

             if (rs.next()) {
                 if (rs.getString("username").equals(searchedUsername)) {
                     userID = rs.getInt("id");
                 }
             }
             PreparedStatement pr = SQLConnector.getInstance().prepareStatement(query);

             pr.setInt(1, userID);
             pr.setString(2,cryptocurrency.getUuid());
             pr.setString(3,cryptocurrency.getShortname());
             pr.setString(4,cryptocurrency.getName());
             pr.setDouble(5,cryptocurrency.getAmount());
             int response= pr.executeUpdate();

             if(response == -1){
                 Helper.showMsg("error");
             }
             else
                 Helper.showMsg("Added to the database");
             return response != -1;

         } catch (SQLException e) {
             System.out.println(e.getMessage());
             Helper.showMsg(e.getMessage());
         }

        return true;
    }


    // Function for retrieving specified accont from database
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
