package Mocrypto.View;

import Mocrypto.Helper.Config;
import Mocrypto.Helper.CryptocurrencyAPI;
import Mocrypto.Helper.Helper;
import Mocrypto.Helper.SQLConnector;
import Mocrypto.Model.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
    private JComboBox combo_box_currencies;
    private JComboBox combo_box_portfolio;
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

    private DefaultComboBoxModel comboBoxModelForCoinList;
    private DefaultComboBoxModel comboBoxModelForPortfolio;

    private DefaultTableModel model_crypto_list;
    private Object[] row_cryptocurrency_list;

    private DefaultTableModel model_portfolio_list;
    private Object[] row_portfolio_list;

    private DefaultTableModel model_transaction_list;
    private Object[] row_transaction_list;

    private ArrayList<Cryptocurrency> cryptocurrencyList = new ArrayList<>();
    private static User currentUser;
    private static CryptocurrencyAPI cryptocurrencyAPI = new CryptocurrencyAPI();

    public MainPage(User user) {

        currentUser = user;

        // Retrieving user portfolio from database
        currentUser.setPortfolio(new Portfolio());
        currentUser.getPortfolio().setCryptocurrencies(getPortfolio());

        display();

        // Updating cryptocurrencies prices and user's balance
        btn_refresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                for (Cryptocurrency cryptocurrency: cryptocurrencyList) {
                    cryptocurrency.setPrice(cryptocurrencyAPI.getExchangeRate(cryptocurrency));

                    System.out.println(cryptocurrency.getPrice());
                }

                for (Cryptocurrency cryptocurrency: currentUser.getPortfolio().getCryptocurrencies()) {
                    cryptocurrency.setPrice(cryptocurrencyAPI.getExchangeRate(cryptocurrency));

                    System.out.println(cryptocurrency.getPrice());
                }

                currentUser.setBalance(currentUser.getBalance());

                lbl_mainpage_totalbalance.setText("Your total balance is: " + currentUser.getPortfolio().getCurrentValue() + " USDT");

                loadCryptocurrencyModel(cryptocurrencyList);
                loadPortfolioModel(currentUser.getPortfolio().getCryptocurrencies());
            }

        });

        // Displaying selected cryptocurrency
        tbl_crypto_list.getSelectionModel().addListSelectionListener(e -> {
            try{
                String selected_crypto_name = tbl_crypto_list.getValueAt(tbl_crypto_list.getSelectedRow(),1).toString();
                fld_cryptobuy_name.setText(selected_crypto_name);
            }catch (Exception exception){

            }

        });
        // Going back to login page when user click quit
        btn_logout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                // Adding user's cryptocurrencies to database
                addDatabase(currentUser);

                dispose();
                LoginPage loginPage = new LoginPage();
            }
        });

        // Adding bought cryptocurrency to user portfolio and transaction
        btn_crypto_buy.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Cryptocurrency selectedCoin = cryptocurrencyList.get(tbl_crypto_list.getSelectedRow());
                String shortName = (String) combo_box_currencies.getSelectedItem();
                int index = Exchange.getIndexOfCurrency(currentUser.getPortfolio().getCryptocurrencies(),shortName);
                Cryptocurrency baseCoin = cryptocurrencyList.get(index);
                Exchange exchange = new Exchange(currentUser,selectedCoin,baseCoin);
                double amount = Double.parseDouble(fld_cryptobuy_amount.getText());
                addDatabase(exchange.buyCryptocurrency(amount,"BUY"));
                display();
            }
        });

        // Displaying selected cryptocurrency
        tbl_portfolio_list.getSelectionModel().addListSelectionListener(e -> {
            try{
                String selected_crypto_name = tbl_portfolio_list.getValueAt(tbl_portfolio_list.getSelectedRow(),1).toString();
                fld_cryptosell_name.setText(selected_crypto_name);
            }catch (Exception exception){

            }

        });

        // Selling user selected cryptocurrency from user portfolio and storing transaction
        btn_crypto_sell.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Cryptocurrency selectedCoin = currentUser.getPortfolio().getCryptocurrencies().get(tbl_portfolio_list.getSelectedRow());
                String shortName = (String) combo_box_portfolio.getSelectedItem();
                int index = Exchange.getIndexOfCurrency(cryptocurrencyList,shortName);
                Cryptocurrency baseCoin = cryptocurrencyList.get(index);
                Exchange exchange = new Exchange(currentUser,baseCoin,selectedCoin);
                double amount = Double.parseDouble(fld_cryptosell_amount.getText());
                addDatabase(exchange.buyCryptocurrency(amount,"SELL"));
                display();
            }
        });

    }

    @Override
    public void display() {

        // Arranging page display properties
        add(wrapper);
        setSize(1000,500);
        int x= Helper.screenCenterPoint("x",getSize());
        int y=Helper.screenCenterPoint("y",getSize());
        setLocation(x,y);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setTitle(Config.PROJECT_TITLE);
        setVisible(true);

        // Getting system's cryptocurrencies
        cryptocurrencyList = getCryptocurrencyList();


        lbl_mainpage_welcome.setText("Welcome: " + currentUser.getName() + " " + currentUser.getSurname());
        lbl_mainpage_totalbalance.setText("Your total balance is: " + currentUser.getPortfolio().getCurrentValue() + " USDT");


        comboBoxModelForCoinList = new DefaultComboBoxModel<>();
        combo_box_currencies.setModel(comboBoxModelForCoinList);
        loadBaseCoinListModel(currentUser.getPortfolio().getCryptocurrencies(),combo_box_currencies);

        comboBoxModelForPortfolio = new DefaultComboBoxModel<>();
        combo_box_portfolio.setModel(comboBoxModelForPortfolio);
        loadBaseCoinListModel(cryptocurrencyList,combo_box_portfolio);


        // Creating cryptocurrencies table
        model_crypto_list = new DefaultTableModel();
        Object[] col_cryptoList= {"Name","Symbol", "Current Price"};
        model_crypto_list.setColumnIdentifiers(col_cryptoList);
        row_cryptocurrency_list = new Object[col_cryptoList.length];
        loadCryptocurrencyModel(cryptocurrencyList);
        tbl_crypto_list.setModel(model_crypto_list);
        tbl_crypto_list.getColumnModel().getColumn(0).setMaxWidth(75);
        tbl_crypto_list.getTableHeader().setReorderingAllowed(false);

        // Creating portfolio table
        model_portfolio_list = new DefaultTableModel();
        Object[] col_portfolioList= {"Name","Symbol", "Amount","Current Price", "USDT Value"};
        model_portfolio_list.setColumnIdentifiers(col_portfolioList);
        row_portfolio_list = new Object[col_portfolioList.length];
        loadPortfolioModel(currentUser.getPortfolio().getCryptocurrencies());
        tbl_portfolio_list.setModel(model_portfolio_list);
        tbl_portfolio_list.getColumnModel().getColumn(0).setMaxWidth(75);
        tbl_portfolio_list.getTableHeader().setReorderingAllowed(false);

        // Creating transactions table
        model_transaction_list = new DefaultTableModel();
        Object[] col_transactionList= {"Type","Base", "Target","Amount", "Date"};
        model_transaction_list.setColumnIdentifiers(col_transactionList);
        row_transaction_list = new Object[col_transactionList.length];
        loadTransactionModel(getTransactions());
        tbl_transaction_list.setModel(model_transaction_list);
        tbl_transaction_list.getColumnModel().getColumn(0).setMaxWidth(75);
        tbl_transaction_list.getTableHeader().setReorderingAllowed(false);
    }

    // Function for updating system's cryptocurrencies
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


    private void loadBaseCoinListModel(ArrayList<Cryptocurrency> cryptocurrencyList, JComboBox comboBox) {
        int i=0;
        for(Cryptocurrency obj: cryptocurrencyList){
            i=0;
            String shortName = obj.getShortname();
            comboBox.addItem(shortName);
        }
    }

    // Function for retrieving system's cryptocurrencies from database
    private static ArrayList<Cryptocurrency> getCryptocurrencyList(){
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

    // Function for updating user portfolio
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
            row_portfolio_list[i++] = obj.getAmount() * obj.getPrice();
            model_portfolio_list.addRow(row_portfolio_list);
        }
    }

    // Function for retrieving user portfolio from database
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

                cryptocurrency.setPrice(cryptocurrencyAPI.getExchangeRate(cryptocurrency));
                cryptocurrencyList.add(cryptocurrency);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cryptocurrencyList;

    }

    // Function for storing user portfolio
    private static boolean addDatabase(User user) {

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

    // Function for updating user transactions
    private void loadTransactionModel(ArrayList<Transaction> transactionList) {
        DefaultTableModel clearModel=(DefaultTableModel) tbl_transaction_list.getModel();
        clearModel.setRowCount(0);
        int i=0;
        for(Transaction obj: transactionList){
            i=0;
            row_transaction_list[i++] = obj.getType();
            row_transaction_list[i++] = obj.getBaseCryptocurrency();
            row_transaction_list[i++] = obj.getTargetCryptocurrency();
            row_transaction_list[i++] = obj.getAmount();
            row_transaction_list[i++] = obj.getTimestamp()

            ;
            model_transaction_list.addRow(row_transaction_list);
        }
    }

    // Function for retrieving user transactions from database
    private static ArrayList<Transaction> getTransactions() {

        ArrayList<Transaction> transactionsList =new ArrayList<>();

        Transaction transaction;
        try {
            Statement st= SQLConnector.getInstance().createStatement();
            ResultSet rs=st.executeQuery("SELECT * from transaction WHERE user_id = " + currentUser.getId());
            while (rs.next()){

                String type = rs.getString("type");
                double amount = rs.getDouble("amount");
                String base_crypto = rs.getString("base_crypto");
                String target_crypto = rs.getString("target_crypto");
                String time_stamp = rs.getString("time_stamp");

                transaction = new Transaction(type, amount, target_crypto, base_crypto,time_stamp);

                transactionsList.add(transaction);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactionsList;

    }

    // Function for storing user transactions
    private static boolean addDatabase(Transaction transaction){
        String query="INSERT INTO transaction (user_id,type,amount,base_crypto,target_crypto,time_stamp) VALUES (?,?,?,?,?,?)";

        try {
            PreparedStatement pr = SQLConnector.getInstance().prepareStatement(query);
            pr.setInt(1,currentUser.getId());
            pr.setString(2,transaction.getType());
            pr.setDouble(3,transaction.getAmount());
            pr.setString(4,transaction.getBaseCryptocurrency());
            pr.setString(5,transaction.getTargetCryptocurrency());
            pr.setString(6,transaction.getTimestamp());
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






    public static void main(String[] args) {
        User user = new User();

        Helper.setLayout();
        MainPage main =new MainPage(user);
    }
}