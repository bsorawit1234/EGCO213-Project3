package Project3_6513122;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class WithdrawFrame extends DepositFrame {
    public WithdrawFrame(MainApplication pf, int id, ArrayList<User> ul, String which) {
        super(pf, id, ul, which);
    }
    public void actionPerformed(ActionEvent e) {
        // Add your submit button logic here
        if(e.getSource() == btn_submit) {
            if (checkBox.isSelected()) {
                UserList.get(index).setCredits(UserList.get(index).getCredits() - Integer.parseInt(amountField.getText()));
                UserList.get(index).setMoney(UserList.get(index).getMoney() + Integer.parseInt(amountField.getText()));

                try {
                    Files.delete(Paths.get(MyConstants.SHEET));
                    PrintWriter write = new PrintWriter(new FileWriter(MyConstants.SHEET, true));
                    for (User u : UserList)
                        write.println(u.getUsername() + " " + u.getPassword() + " " + u.getMoney() + " " + u.getCredits());
                    write.close();
                } catch (Exception er) {
                    System.err.println(er);
                }

                JOptionPane.showMessageDialog(modalDialog, "Withdraw" + " Success!!");

                UserFrame userFrame = new UserFrame(mainFrame, index, UserList);
                userFrame.setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(modalDialog, "You have to accept to proceed");
            }
        } else if(e.getSource() == btn_proceed) {
            try {
                if(amountField.getText().isBlank()) throw new MyException("Please enter something.");
                showTableModalDialog(this, Integer.parseInt(amountField.getText()));
            } catch (NumberFormatException err) {
                JOptionPane.showMessageDialog(this, "Please enter number only");
                amountField.setText("");
            } catch (MyException err) {
                JOptionPane.showMessageDialog(getParent(),err.getMessage());
                amountField.setText("");
            }
            catch (Exception err) {
                System.err.println(err);
            }
        }
    }
}
