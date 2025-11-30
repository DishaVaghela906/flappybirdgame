package flappybirdgame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StartFrame extends JFrame implements ActionListener {
   
	private JButton dayModeButton, darkModeButton;
	private  String username;
    private int highScore;

    public StartFrame(String username, int highScore){
    	this.username = username;
        this.highScore = highScore;
        
        setSize(new Dimension(900, 800));

        ImageIcon mainPanel = new ImageIcon("./startPanel.jpg");
        JLabel label = new JLabel();
        label.setIcon(mainPanel);
        label.setLayout(null);
        

     // Day Mode button
        dayModeButton = new JButton("Jungle");
        dayModeButton.setBounds(350, 450, 150, 50);
        dayModeButton.addActionListener(this);
        dayModeButton.setFont(new Font("MV Boli", Font.BOLD, 25));
        dayModeButton.setForeground(Color.red);
        dayModeButton.setBackground(Color.yellow);  
        dayModeButton.setFocusable(false);
        dayModeButton.setBorder(BorderFactory.createLineBorder(Color.black, 5));

        // Dark Mode button
        darkModeButton = new JButton("Harry Potter");
        darkModeButton.setBounds(325, 525, 200, 50);
        darkModeButton.addActionListener(this);
        darkModeButton.setFont(new Font("MV Boli", Font.BOLD, 25));
        darkModeButton.setForeground(Color.white);
        darkModeButton.setBackground(new Color(50, 50, 50));  
        darkModeButton.setFocusable(false);
        darkModeButton.setBorder(BorderFactory.createLineBorder(Color.black, 5));

        label.add(dayModeButton);
        label.add(darkModeButton);
        
        add(label);
        pack();
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
       
    	if (e.getSource() == dayModeButton) {
            startGameWithMode("day");  
        } else if (e.getSource() == darkModeButton) {
            startGameWithMode("dark");  
        }
    }
    

    private void startGameWithMode(String mode) {
        this.dispose();  
        new GameFrame(mode,username,highScore);  
    }

}
