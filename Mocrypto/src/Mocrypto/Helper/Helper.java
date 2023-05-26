package Mocrypto.Helper;

import javax.swing.*;
import java.awt.*;

public class Helper {

    // Setting layout to Nimbus design
    public static void setLayout(){
        for(UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()){
            if("Nimbus".equals(info.getName())){
                try {
                    UIManager.setLookAndFeel(info.getClassName());
                } catch (ClassNotFoundException | IllegalAccessException | UnsupportedLookAndFeelException | InstantiationException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    // Getting screen's center point to create responsive design
    public static int screenCenterPoint(String dimension, Dimension size){
        int point;
        switch (dimension){
            case "x":
                point=(Toolkit.getDefaultToolkit().getScreenSize().width-size.width)/2;
                break;
            case "y":
                point=(Toolkit.getDefaultToolkit().getScreenSize().height-size.height)/2;
                break;
            default:
                point=0;
        }
        return point;
    }

    // Checking is the field empty
    public static boolean isFieldEmpty(JTextField field){
        return field.getText().trim().isEmpty();
    }

    // Printing error with respect to given parameter
    public static void showMsg(String str){
        optionPageTR();
        String msg;
        String title;
        switch (str){
            case "fill":
                msg="Please fill the blanks";
                title="Error!";
                break;
            case "done":
                msg="Success";
                title="Result";
                break;
            case "error":
                msg="An error occurred";
                title="ERROR";
            default:
                msg=str;
                title="Message";

        }

        JOptionPane.showMessageDialog(null,msg,title,JOptionPane.INFORMATION_MESSAGE);
    }

    // Adjusting button's text
    public static void optionPageTR(){
        UIManager.put("OptionPane.okButtonText" , "Okay");
        UIManager.put("OptionPane.yesButtonText" , "Yes");
        UIManager.put("OptionPane.noButtonText" , "No");
    }
}
