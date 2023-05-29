package Mocrypto.View;

import Mocrypto.Helper.Config;
import Mocrypto.Helper.CryptocurrencyAPI;
import Mocrypto.Helper.Helper;
import Mocrypto.Helper.SQLConnector;
import Mocrypto.Model.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class MainPage extends JFrame implements IPage{

    private JPanel wrapper;
    private JTabbedPane tab_operator;
    private JPanel pnl_cryptocurrencies;
    private JScrollPane scrl_cryptocurrencies_list;
    private JTable tbl_crypto_list;
    private JPanel pnl_cryptocurrencies_buy;
    private JTextField fld_cryptobuy_name;
    private JTextField fld_cryptobuy_amount;
    private JButton btn_crypto_buy;
    private JPanel pnl_portfolio;
    private JPanel pnl_crypto_sell;
    private JScrollPane scrl_portfolio_list;
    private JButton btn_crypto_sell;
    private JTextField fld_cryptosell_name;
    private JTextField fld_cryptosell_amount;
    private JPanel pnl_transaction;
    private JScrollPane scrl_transaction_list;
    private JTable tbl_transaction_list;
    private JTable tbl_portfolio_list;
    private JLabel lbl_mainpage_welcome;
    private JLabel lbl_mainpage_totalbalance;
    private JButton btn_logout;
    private JButton btn_refresh;

    private DefaultTableModel model_crypto_list;
    private Object[] row_cryptocurrency_list;

    private DefaultTableModel model_portfolio_list;
    private Object[] row_portfolio_list;


    private ArrayList<Cryptocurrency> cryptocurrencyList = new ArrayList<>();
    private static User currentUser;
    private CryptocurrencyAPI cryptocurrencyAPI = new CryptocurrencyAPI();

    public MainPage(User user) {

        currentUser = user;

        currentUser.setPortfolio(new Portfolio());

        currentUser.getPortfolio().setCryptocurrencies(getPortfolio());

        display();

        btn_refresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                for (Cryptocurrency cryptocurrency: cryptocurrencyList) {
                    cryptocurrency.setPrice(cryptocurrencyAPI.getExchangeRate(cryptocurrency));

                    System.out.println(cryptocurrency.getPrice());
                }

                currentUser.setBalance(currentUser.getBalance());

                lbl_mainpage_totalbalance.setText("Your total balance is: " + currentUser.getBalance() + " USD");

                loadCryptocurrencyModel(cryptocurrencyList);


            }

        });

        tbl_crypto_list.getSelectionModel().addListSelectionListener(e -> {
            try{
                String selected_crypto_name = tbl_crypto_list.getValueAt(tbl_crypto_list.getSelectedRow(),1).toString();
                fld_cryptobuy_name.setText(selected_crypto_name);
            }catch (Exception exception){

            }

        });
        btn_logout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                // Adding user's cryptocurrencies to database
                add(currentUser);

                dispose();
                LoginPage loginPage = new LoginPage();
            }
        });
        btn_crypto_buy.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Cryptocurrency selectedCoin = cryptocurrencyList.get(tbl_crypto_list.getSelectedRow());
                Cryptocurrency baseCoin = cryptocurrencyList.get(0); // temporary --<vodka USDT
                Exchange exchange = new Exchange(currentUser,selectedCoin,baseCoin);
                double amount = Double.parseDouble(fld_cryptobuy_amount.getText());
                exchange.buyCryptocurrency(amount,"BUY");
                display();
            }
        });

        tbl_portfolio_list.getSelectionModel().addListSelectionListener(e -> {
            try{
                String selected_crypto_name = tbl_portfolio_list.getValueAt(tbl_portfolio_list.getSelectedRow(),1).toString();
                fld_cryptosell_name.setText(selected_crypto_name);
            }catch (Exception exception){

            }

        });
    }

    @Override
    public void display() {

        add(wrapper);
        setSize(1000,500);
        int x= Helper.screenCenterPoint("x",getSize());
        int y=Helper.screenCenterPoint("y",getSize());
        setLocation(x,y);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setTitle(Config.PROJECT_TITLE);
        setVisible(true);

        cryptocurrencyList = getCryptocurrencyList();


        lbl_mainpage_welcome.setText("Welcome: " + currentUser.getName() + " " + currentUser.getSurname());
        lbl_mainpage_totalbalance.setText("Your total balance is: " + currentUser.getBalance() + " USD");

        model_crypto_list = new DefaultTableModel();
        Object[] col_cryptoList= {"Name","Symbol", "Current Price"};
        model_crypto_list.setColumnIdentifiers(col_cryptoList);
        row_cryptocurrency_list = new Object[col_cryptoList.length];
        loadCryptocurrencyModel(cryptocurrencyList);
        tbl_crypto_list.setModel(model_crypto_list);
        tbl_crypto_list.getColumnModel().getColumn(0).setMaxWidth(75);
        tbl_crypto_list.getTableHeader().setReorderingAllowed(false);


        model_portfolio_list = new DefaultTableModel();
        Object[] col_portfolioList= {"Name","Symbol", "Amount","Current Price"};
        model_portfolio_list.setColumnIdentifiers(col_portfolioList);
        row_portfolio_list = new Object[col_portfolioList.length];
        loadPortfolioModel(currentUser.getPortfolio().getCryptocurrencies());
        tbl_portfolio_list.setModel(model_portfolio_list);
        tbl_portfolio_list.getColumnModel().getColumn(0).setMaxWidth(75);
        tbl_portfolio_list.getTableHeader().setReorderingAllowed(false);
    }


    private void loadCryptocurrencyModel(ArrayList<Cryptocurrency> cryptocurrencyList) {
        DefaultTableModel clearModel=(DefaultTableModel) tbl_crypto_list.getModel();
        clearModel.setRowCount(0);
        int i=0;
        for(Cryptocurrency obj: cryptocurrencyList){
            i=0;
            row_cryptocurrency_list[i++]=obj.getName();
            row_cryptocurrency_list[i++]=obj.getShortname();
            row_cryptocurrency_list[i++]=obj.getPrice();
            model_crypto_list.addRow(row_cryptocurrency_list);
        }
    }

    public static ArrayList<Cryptocurrency> getCryptocurrencyList(){
        ArrayList<Cryptocurrency> cryptocurrencyList =new ArrayList<>();

        Cryptocurrency cryptocurrency;
        try {
            Statement st= SQLConnector.getInstance().createStatement();
            ResultSet rs=st.executeQuery("SELECT * from cryptocurrency"); /* for portfolio WHERE user_id = " + currentUser.getId())*/;
            while (rs.next()){

                String uuid = rs.getString("uuid");
                String name = rs.getString("name");
                String shortName = rs.getString("shortname");
                double price = rs.getDouble("price");
                double volume = rs.getDouble("volume");
                cryptocurrency = new Cryptocurrency(uuid, name, shortName, price, volume);
                cryptocurrencyList.add(cryptocurrency);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cryptocurrencyList;
    }

    public static boolean add(int user_id,String crypto_id,String shortname,double buy_price, double amount){
        String query="INSERT INTO owned_cryptocurrency (user_id,crypto_id,shortname,buy_price,amount) VALUES (?,?,?,?,?,?)";

        try {
            PreparedStatement pr = SQLConnector.getInstance().prepareStatement(query);
            pr.setInt(1,user_id);
            pr.setString(2,crypto_id);
            pr.setString(3,shortname);
            pr.setDouble(4,buy_price);
            pr.setDouble(5,amount);
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

    private void loadPortfolioModel(ArrayList<Cryptocurrency> portfolioCryptocurrencyList) {
        DefaultTableModel clearModel=(DefaultTableModel) tbl_portfolio_list.getModel();
        clearModel.setRowCount(0);
        int i=0;
        for(Cryptocurrency obj: portfolioCryptocurrencyList){
            i=0;
            row_portfolio_list[i++] = obj.getName();
            row_portfolio_list[i++] = obj.getShortname();
            row_portfolio_list[i++] = obj.getAmount();
            row_portfolio_list[i++] = obj.getPrice();
            model_portfolio_list.addRow(row_portfolio_list);
        }
    }

    private static ArrayList<Cryptocurrency> getPortfolio() {

        ArrayList<Cryptocurrency> cryptocurrencyList =new ArrayList<>();

        Cryptocurrency cryptocurrency;
        try {
            Statement st= SQLConnector.getInstance().createStatement();
            ResultSet rs=st.executeQuery("SELECT * from portfolio WHERE user_id = " + currentUser.getId());
            while (rs.next()){

                String uuid = rs.getString("uuid");
                String shortName = rs.getString("short_name");
                String name = rs.getString("name");
                double amount = rs.getDouble("amount");
                cryptocurrency = new Cryptocurrency(uuid, name, shortName, amount);
                cryptocurrencyList.add(cryptocurrency);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cryptocurrencyList;

    }

    // Function for storing user portfolio
    public static boolean add(User user) {

        String query = "DELETE FROM portfolio WHERE user_id = " + currentUser.getId();

        try {
            PreparedStatement pr = SQLConnector.getInstance().prepareStatement(query);
            int response= pr.executeUpdate();

            if(response == -1){
                Helper.showMsg("error");
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        for (Cryptocurrency cryptocurrency : user.getPortfolio().getCryptocurrencies()) {
            System.out.println(cryptocurrency.getShortname());
            query="INSERT INTO portfolio (user_id,uuid,short_name,name,amount) VALUES (?,?,?,?,?)";

            try {
                PreparedStatement pr = SQLConnector.getInstance().prepareStatement(query);
                pr.setInt(1, user.getId());
                pr.setString(2,cryptocurrency.getUuid());
                pr.setString(3,cryptocurrency.getShortname());
                pr.setString(4,cryptocurrency.getName());
                pr.setDouble(5,cryptocurrency.getAmount());
                int response= pr.executeUpdate();

                if(response == -1){
                    Helper.showMsg("error");
                    return false;
                }


            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }


        return true;

    }





    public static void main(String[] args) {
        User user = new User();

        Helper.setLayout();
        MainPage main =new MainPage(user);
    }
}