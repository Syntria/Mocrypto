package Mocrypto.View;

import Mocrypto.Helper.Config;
import Mocrypto.Helper.Helper;
import Mocrypto.Helper.SQLConnector;
import Mocrypto.Model.Cryptocurrency;
import Mocrypto.Model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
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

    private DefaultTableModel model_course_list;
    private Object[] row_cryptocurrency_list;


    private ArrayList<Cryptocurrency> cryptocurrencyList;
    private static User currentUser;

    public MainPage(/*User user*/) {

        display();

    }

    @Override
    public void display() {

        System.out.println("asd");
        add(wrapper);
        setSize(1000,500);
        int x= Helper.screenCenterPoint("x",getSize());
        int y=Helper.screenCenterPoint("y",getSize());
        setLocation(x,y);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setTitle(Config.PROJECT_TITLE);
        setVisible(true);

        model_course_list= new DefaultTableModel();
        Object[] col_courseList= {"Name","Short", "Current Price"};
        model_course_list.setColumnIdentifiers(col_courseList);
        row_cryptocurrency_list = new Object[col_courseList.length];
        loadCryptocurrencyModel(cryptocurrencyList);
        tbl_crypto_list.setModel(model_course_list);
        tbl_crypto_list.getColumnModel().getColumn(0).setMaxWidth(75);
        tbl_crypto_list.getTableHeader().setReorderingAllowed(false);
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
            model_course_list.addRow(row_cryptocurrency_list);
        }
    }

    public static ArrayList<Cryptocurrency> getCryptocurrencyList(){
        ArrayList<Cryptocurrency> cryptocurrencyList =new ArrayList<>();

        Cryptocurrency cryptocurrency;
        try {
            Statement st= SQLConnector.getInstance().createStatement();
            ResultSet rs=st.executeQuery("SELECT * from cryptocurrency"); /* for portfolio WHERE user_id = " + currentUser.getId())*/;
            while (rs.next()){

                String name = rs.getString("name");
                String shortName = rs.getString("short_name");
                int price = rs.getInt("price");
                cryptocurrency = new Cryptocurrency(name, shortName, price);
                cryptocurrencyList.add(cryptocurrency);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cryptocurrencyList;
    }



    public static void main(String[] args) {
        Helper.setLayout();
        MainPage main =new MainPage();
    }
}
