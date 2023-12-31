package Project3_6513122;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

public class SlotFrame extends JFrame {
    private JTextArea betINPUT;
    private JDialog   modalDialog;
    private MainApplication mainFrame;
    private JPanel contentpane;
    private JPanel drawpane;
    private JTextField balanceDISPLAY;
    private JLayeredPane layeredPane;
    private int framewidth = MyConstants.FRAMEWIDTH;
    private int frameheight = MyConstants.FRAMEHEIGHT;
    private int spinCountBtn = 0;
    private ArrayList<SlotLabel> slotty = new ArrayList<SlotLabel>();
    private ArrayList<User> UserList;
    private User user;
    private int index;
    private boolean smallWin;
    private static final CountDownLatch latch = new CountDownLatch(9);
    private boolean check_btn_back = true;

    public SlotFrame(int id, ArrayList<User> ul, MainApplication mf) {
        index = id;
        UserList = ul;
        mainFrame = mf;
        user = UserList.get(index);
        this.setSize(framewidth, frameheight);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(true);
        this.setTitle("SLOTTY69 GAME");

        contentpane = (JPanel) getContentPane();
        contentpane.setLayout(new BorderLayout());

        layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(framewidth, frameheight));

        drawpane = new JPanel();
        drawpane.setBounds(0, 0, framewidth, frameheight);

        MyImageIcon background = new MyImageIcon(MyConstants.SLOT_MAINBG).resize(framewidth, frameheight);
        JLabel backgroundLabel = new JLabel(background);
        backgroundLabel.setBounds(0, 0, framewidth, frameheight);
        layeredPane.add(backgroundLabel, Integer.valueOf(0));


        int x, y = 120;
        for(int i = 0; i < 3; i++) {
            x = 480;
            for(int j = 0; j < 3; j++) {
                SlotLabel s = new SlotLabel(x, y, (int)(Math.random() * 10));
                slotty.add(s);
                layeredPane.add(s, Integer.valueOf(1));
                x += 125;
            }
            y += 135;
        }

        MyImageIcon spinBUTTON = new MyImageIcon(MyConstants.ROLL).resize(173, 168);
        MyImageIcon stopBUTTON = new MyImageIcon(MyConstants.STOP).resize(173, 168);
        ImageIcon[] buttonIcons = {spinBUTTON, stopBUTTON};
        JButton spin_btn = new JButton(buttonIcons[0]);
        spin_btn.setBounds(1000, 500, 173, 168);
        spin_btn.setBorderPainted(false);
        spin_btn.setContentAreaFilled(false);
        spin_btn.setFocusPainted(false);
        spin_btn.setOpaque(false);
        spin_btn.setCursor(Cursor.getPredefinedCursor(HAND_CURSOR));
        layeredPane.add(spin_btn, Integer.valueOf(2));

        MyImageIcon backBUTTON = new MyImageIcon(MyConstants.BACK).resize(169, 74);
        JButton btn_back = new JButton(backBUTTON);
        btn_back.setBounds(30, 630,169 , 74);
        btn_back.setBorderPainted(false);
        btn_back.setContentAreaFilled(false);
        btn_back.setFocusPainted(false);
        btn_back.setOpaque(false);
        btn_back.setCursor(Cursor.getPredefinedCursor(HAND_CURSOR));

        btn_back.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setEnabled(check_btn_back);
                UserFrame userFrame = new UserFrame(mainFrame, index, UserList);
                userFrame.setVisible(true);
                dispose();
            }
        });
        layeredPane.add(btn_back, Integer.valueOf(2));

        MyImageIcon winprizeICON = new MyImageIcon(MyConstants.WINPRIZE).resize(414, 300);
        JLabel winprize = new JLabel(winprizeICON);
        winprize.setBounds(900, 80, 414, 300);
        layeredPane.add(winprize, Integer.valueOf(2));

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    btn_back.doClick();
                }
            }
        });
        setFocusable(true);

        MyImageIcon slotMACHINE = new MyImageIcon(MyConstants.SLOTMACHINE).resize(906, 733);
        JLabel slot = new JLabel(slotMACHINE);
        slot.setBounds(200, 0, 906, 733);
        layeredPane.add(slot, Integer.valueOf(1));

        balanceDISPLAY = new JTextField();
        balanceDISPLAY.setBounds(650, 530, 180, 35);
        balanceDISPLAY.setFont(new Font("SanSerif", Font.BOLD, 30));
        balanceDISPLAY.setVisible(true);
        balanceDISPLAY.setEditable(false);
        balanceDISPLAY.setText(String.valueOf(user.getCredits()));
        layeredPane.add(balanceDISPLAY, Integer.valueOf(2));

        betINPUT = new JTextArea();
        betINPUT.setFont(new Font("SanSerif", Font.BOLD, 30));
        betINPUT.setBounds(650, 575, 180, 35); // Adjust position and size as needed
        betINPUT.setEditable(true);
        betINPUT.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char enteredChar = e.getKeyChar();
                String enteredText = betINPUT.getText() + e.getKeyChar();

                if(!Character.isDigit(enteredChar)) {
                    e.consume(); // prevent event if the key that user has typed is not a number.
                }

                try {
                    int enteredNum = Integer.parseInt(enteredText);
                    if(enteredNum > user.getCredits()) {
                        e.consume();
                    } else {
                        user.setBet(enteredNum);
                        spin_btn.setEnabled(enteredNum != 0);
                    }
                } catch (NumberFormatException err) {
                    e.consume();
                    betINPUT.setText("");
                    user.setBet(0);
                    spin_btn.setEnabled(false);
                }
            }
        });


        spin_btn.setEnabled(!betINPUT.getText().trim().isEmpty() && !betINPUT.getText().trim().equals("0"));
        spin_btn.addActionListener(e -> {
            if(spinCountBtn % 2 == 0) {
                btn_back.setEnabled(false);
                smallWin = false;
                for(SlotLabel s: slotty) {
                    Thread slotThread = new Thread() {
                        public void run() {
                            s.setSpin(true);
                            while (s.isSpin()) s.spin();
                            latch.countDown();
                        }
                    };
                    slotThread.start();
                }
            } else {
                spin_btn.setEnabled(!betINPUT.getText().trim().isEmpty() && !betINPUT.getText().trim().equals("0"));
                btn_back.setEnabled(true);
                for(SlotLabel s: slotty) {
                    s.setSpin(false);
                }
            }

            spinCountBtn++;
            spin_btn.setIcon(buttonIcons[spinCountBtn % 2]);

            if(spinCountBtn != 0 && spinCountBtn % 2 == 0) {
                try {
                    latch.await();
                    check_slot();
                } catch (InterruptedException err) {
                    System.err.println(err);
                }
            } else if(spinCountBtn % 2 != 0) {
                if(user.getBet() <= user.getCredits()) {
                    user.setCredits(user.getCredits() - user.getBet());
                }
                betINPUT.setText(null);
            }

            balanceDISPLAY.setText(String.valueOf(user.getCredits()));
        });

        layeredPane.add(betINPUT, Integer.valueOf(2));
        contentpane.add(layeredPane, BorderLayout.CENTER);
        validate();
        repaint();
    }

    public void check_slot() {
        int credits = user.getCredits();
        int old_credits = user.getCredits();
        int bet = user.getBet();

        boolean bigWin = true;
        for(int i = 1; i < slotty.size(); i++) {
            if (slotty.get(0).getID() != slotty.get(i).getID()) {
                bigWin = false;
                break;
            }
        }

        if(bigWin) {
            user.setCredits(credits + (bet * 100));
            JOptionPane.showMessageDialog(modalDialog, "Win " + (credits - old_credits) + " credits!!!!");
        } else {
            // Diagonal
            if (slotty.get(0).getID() == slotty.get(4).getID() && slotty.get(4).getID() == slotty.get(8).getID()) {
                smallWin = true;
                credits += bet * 3;
            } else if(slotty.get(0).getID() == slotty.get(8).getID()) {
                smallWin = true;
                credits += bet;
            }

            if (slotty.get(2).getID() == slotty.get(4).getID() && slotty.get(4).getID() == slotty.get(6).getID()) {
                smallWin = true;
                credits += bet * 3;
            } else if(slotty.get(2).getID() == slotty.get(6).getID()) {
                smallWin = true;
                credits += bet;
            }

            // Horizontal
            if (slotty.get(0).getID() == slotty.get(1).getID() && slotty.get(1).getID() == slotty.get(2).getID()) {
                smallWin = true;
                credits += bet * 3;
            } else if(slotty.get(0).getID() == slotty.get(2).getID()) {
                smallWin = true;
                credits += bet;
            }

            if (slotty.get(3).getID() == slotty.get(4).getID() && slotty.get(4).getID() == slotty.get(5).getID()) {
                smallWin = true;
                credits += bet * 3;
            } else if(slotty.get(3).getID() == slotty.get(5).getID()) {
                smallWin = true;
                credits += bet;
            }

            if (slotty.get(6).getID() == slotty.get(7).getID() && slotty.get(7).getID() == slotty.get(8).getID()) {
                smallWin = true;
                credits += bet * 3;
            } else if(slotty.get(6).getID() == slotty.get(8).getID()) {
                smallWin = true;
                credits += bet;
            }

            // Vertical
            if (slotty.get(0).getID() == slotty.get(3).getID() && slotty.get(3).getID() == slotty.get(6).getID()) {
                smallWin = true;
                credits += bet * 3;
            } else if (slotty.get(0).getID() == slotty.get(6).getID()) {
                smallWin = true;
                credits += bet;
            }

            if (slotty.get(1).getID() == slotty.get(4).getID() && slotty.get(4).getID() == slotty.get(7).getID()) {
                smallWin = true;
                credits += bet * 3;
            } else if(slotty.get(1).getID() == slotty.get(7).getID()) {
                smallWin = true;
                credits += bet;
            }

            if (slotty.get(2).getID() == slotty.get(5).getID() && slotty.get(5).getID() == slotty.get(8).getID()) {
                smallWin = true;
                credits += bet * 3;
            } else if(slotty.get(2).getID() == slotty.get(8).getID()) {
                smallWin = true;
                credits += bet;
            }
        }

        if(smallWin) {
            JOptionPane.showMessageDialog(modalDialog, "Win " + (credits - old_credits) + " credits!!!!");
        }

        if(!bigWin && !smallWin) {
            JOptionPane.showMessageDialog(modalDialog, "Lost " + bet + " credits");
        }

        user.setCredits(credits);
        user.setBet(0);

        try {
            Files.delete(Paths.get(MyConstants.SHEET));
            PrintWriter write = new PrintWriter(new FileWriter(MyConstants.SHEET, true));
            for (User u : UserList)
                write.println(u.getUsername() + " " + u.getPassword() + " " + u.getMoney() + " " + u.getCredits());
            write.close();
        } catch (Exception er) {
            System.err.println(er);
        }


    }
}

class SlotLabel extends JLabel {
    private int width = 100, height = 100, curX, curY;
    private boolean spin = false;
    private int ID;
    public SlotLabel(int x, int y, int id) {
        curX = x;
        curY = y;
        ID = id; // NO FACE
        MyImageIcon startImg = new MyImageIcon(MyConstants.SLOT_CARD + ID + ".png").resize(width, height);
        setIcon(startImg);
        setBounds(curX, curY, width, height);
        setVisible(true);
        validate();
        repaint();
    }
    public int getID() { return ID; }
    public boolean isSpin() { return spin; }
    public void setSpin(boolean s) { spin = s; }
    public void spin() {
        ID = (int)(Math.random() * 14);
        MyImageIcon img = new MyImageIcon(MyConstants.SLOT_CARD + ID + ".png").resize(width, height);
        setIcon(img);
        repaint();

        try { Thread.sleep(100); }
        catch (InterruptedException e) { e.printStackTrace(); }
    }
}


