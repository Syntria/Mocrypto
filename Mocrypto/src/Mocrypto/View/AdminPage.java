package Mocrypto.View;

import Mocrypto.Helper.Config;
import Mocrypto.Helper.CryptocurrencyAPI;
import Mocrypto.Helper.Helper;
import Mocrypto.Helper.SQLConnector;
import Mocrypto.Model.Admin;
import Mocrypto.Model.Cryptocurrency;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class AdminPage extends JFrame implements IPage{

    private JTabbedPane admin_tab_opertor;
    private JPanel panel_admin_crypto_list;
    private JScrollPane scroll_crypto_list;
    private JTable table_crypto_list;
    private JTextField field_crypto_name;
    private JButton button_crypto_add;
    private JPanel wrapper;
    private JPanel panel_admin_systemcrypto_lis;
    private JScrollPane scroll_systemcrypto_list;
    private JTable table_systemcrypto_list;
    private JButton button_crypto_remove;
    private JTextField field_removecrypto_name;
    private JButton button_admin_quit;

    private Object[] col_cryptoList= {"ID","Name","Symbol"};

    private DefaultTableModel model_allcrypto_list;
    private Object[] row_allcryptocurrency_list;

    private DefaultTableModel model_systemcrypto_list;
    private Object[] row_systemcryptocurrency_list;

    private int selectedCryptocurrencyIndex;
    private Cryptocurrency selectedCryptocurrency;
    private ArrayList<Cryptocurrency> systemCryptocurrencyList = getSystemCryptocurrencies();
    private CryptocurrencyAPI cryptocurrencyAPI = new CryptocurrencyAPI();
    private ArrayList<Cryptocurrency> allCryptocurrencyList = cryptocurrencyAPI.getCryptocurrencyList();


    public AdminPage(Admin admin) {

        // Deleting system's cryptocurrencies from all cryptocurrencies
        for (int i = 0; i < systemCryptocurrencyList.size(); i++) {

            for (int j = 0; j < allCryptocurrencyList.size(); j++) {

                if ((systemCryptocurrencyList.get(i).getUuid().equals(allCryptocurrencyList.get(j).getUuid()))) {

                    allCryptocurrencyList.remove(j);
                }
            }
        }
        display();

        button_admin_quit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                LoginPage loginPage = new LoginPage();
            }
        });
    }

    public void display() {
		this.setIconImage((new ImageIcon(this.getClass().getResource("/image/app_icon.png"))).getImage());
        // Arranging page display properties
        add(wrapper);
        setSize(1000,500);
        int x= Helper.screenCenterPoint("x",getSize());
        int y=Helper.screenCenterPoint("y",getSize());
        setLocation(x,y);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setTitle(Config.PROJECT_TITLE);
        setVisible(true);

        table_systemcrypto_list.getSelectionModel().addListSelectionListener(e -> {
            try{
                String selected_crypto_name = table_systemcrypto_list.getValueAt(table_systemcrypto_list.getSelectedRow(),1).toString();
                field_removecrypto_name.setText(selected_crypto_name);
            }catch (Exception exception){

            }

        });

        table_crypto_list.getSelectionModel().addListSelectionListener(e -> {
            try{
                String selected_crypto_name = table_crypto_list.getValueAt(table_crypto_list.getSelectedRow(),1).toString();
                field_crypto_name.setText(selected_crypto_name);
            }catch (Exception exception){

            }

        });

        // Creating all cryptocurrency table
        model_allcrypto_list = new DefaultTableModel();
        model_allcrypto_list.setColumnIdentifiers(col_cryptoList);
        row_allcryptocurrency_list = new Object[col_cryptoList.length];
        loadAllCryptocurrencyModel(allCryptocurrencyList);
        table_crypto_list.setModel(model_allcrypto_list);
        table_crypto_list.getColumnModel().getColumn(0).setMaxWidth(75);
        table_crypto_list.getTableHeader().setReorderingAllowed(false);


        // Creating system's cryptocurrency table
        model_systemcrypto_list = new DefaultTableModel();
        model_systemcrypto_list.setColumnIdentifiers(col_cryptoList);
        row_systemcryptocurrency_list = new Object[col_cryptoList.length];
        loadSystemCryptocurrencyModel(systemCryptocurrencyList);
        table_systemcrypto_list.setModel(model_systemcrypto_list);
        table_systemcrypto_list.getColumnModel().getColumn(0).setMaxWidth(75);
        table_systemcrypto_list.getTableHeader().setReorderingAllowed(false);

        // Adding eventlistener to add button
        button_crypto_add.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                // Storing selected cryptocurrency and cryptocurrency index
                selectedCryptocurrencyIndex = table_crypto_list.getSelectedRow();
                selectedCryptocurrency = allCryptocurrencyList.get(selectedCryptocurrencyIndex);

                // Adding selected cryptocurrency to system
                if(add(selectedCryptocurrency.getUuid(),selectedCryptocurrency.getName(),selectedCryptocurrency.getShortname(),
                        selectedCryptocurrency.getPrice(), selectedCryptocurrency.getVolume())) {
                    Helper.showMsg("done");
                }

                // Updating cryptocurrency lists
                allCryptocurrencyList.remove(selectedCryptocurrencyIndex);
                systemCryptocurrencyList = getSystemCryptocurrencies();

                // Updating cryptocurrency tables
                loadAllCryptocurrencyModel(allCryptocurrencyList);
                loadSystemCryptocurrencyModel(systemCryptocurrencyList);

            }
        });

        // Adding eventlistener to remove button
        button_crypto_remove.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                // Storing selected cryptocurrency and cryptocurrency index
                selectedCryptocurrencyIndex = table_systemcrypto_list.getSelectedRow();
                selectedCryptocurrency = systemCryptocurrencyList.get(selectedCryptocurrencyIndex);

                // Removing selected cryptocurrency from system
                if(delete(selectedCryptocurrency.getUuid())) {
                    Helper.showMsg("done");
                }

                // Updating cryptocurrency lists
                allCryptocurrencyList.add(selectedCryptocurrency);
                systemCryptocurrencyList = getSystemCryptocurrencies();

                // Updating cryptocurrency tables
                loadAllCryptocurrencyModel(allCryptocurrencyList);
                loadSystemCryptocurrencyModel(systemCryptocurrencyList);

            }
        });
    }

    // Function for displaying all cryptocurrencies
    private void loadAllCryptocurrencyModel(ArrayList<Cryptocurrency> cryptocurrencyList) {
        DefaultTableModel clearModel=(DefaultTableModel) table_crypto_list.getModel();
        clearModel.setRowCount(0);
        int i=0;
        for(Cryptocurrency obj: cryptocurrencyList){
            i=0;
            row_allcryptocurrency_list[i++]=obj.getUuid();
            row_allcryptocurrency_list[i++]=obj.getName();
            row_allcryptocurrency_list[i++]=obj.getShortname();
            model_allcrypto_list.addRow(row_allcryptocurrency_list);
        }
    }

    // Function for displaying system's cryptocurrencies
    private void loadSystemCryptocurrencyModel(ArrayList<Cryptocurrency> systemCryptocurrencyList) {

        DefaultTableModel clearModel=(DefaultTableModel) table_systemcrypto_list.getModel();
        clearModel.setRowCount(0);
        int i=0;
        for(Cryptocurrency obj: systemCryptocurrencyList){
            i=0;
            row_systemcryptocurrency_list[i++]=obj.getUuid();
            row_systemcryptocurrency_list[i++]=obj.getName();
            row_systemcryptocurrency_list[i++]=obj.getShortname();
            model_systemcrypto_list.addRow(row_systemcryptocurrency_list);
        }
    }



    // Function for adding cryptocurrency data to system database
    private static boolean add(String uuid,String name,String shortname, double price, double volume){
        String query="INSERT INTO cryptocurrency (uuid,name,shortname,price,volume) VALUES (?,?,?,?,? )";

        try {
            PreparedStatement pr= SQLConnector.getInstance().prepareStatement(query);
            pr.setString(1,uuid);
            pr.setString(2,name);
            pr.setString(3,shortname);
            pr.setDouble(4,price);
            pr.setDouble(5,volume);
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

    // Function for removing a cryptocurrency from system
    private static boolean delete(String id){
        String query="DELETE FROM cryptocurrency WHERE uuid=?";

        try {
            PreparedStatement pr=SQLConnector.getInstance().prepareStatement(query);
            pr.setString(1,id);
            return pr.executeUpdate() != -1;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    // Function for storing a cryptocurrency from system
    private static ArrayList<Cryptocurrency> getSystemCryptocurrencies(){
        ArrayList<Cryptocurrency> systemCryptoList =new ArrayList<>();
        Cryptocurrency obj;
        try {
            Statement st= SQLConnector.getInstance().createStatement();
            ResultSet rs=st.executeQuery("SELECT * FROM cryptocurrency");
            while (rs.next()){
                String uuid = rs.getString("uuid");
                String name = rs.getString("name");
                String shortname = rs.getString("shortname");
                double price = rs.getDouble("price");
                double volume = rs.getDouble("volume");
                obj=new Cryptocurrency(uuid,name,shortname,price,volume);
                systemCryptoList.add(obj);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return systemCryptoList;
    }


}