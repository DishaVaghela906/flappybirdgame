package flappybirdgame;

import java.awt.Dimension;

import javax.swing.*;

public class GameFrame extends JFrame {
    public GameFrame(String mode, String username, int highScore) {
        setTitle("Flappy Bird");
        setSize(new Dimension(900, 800));
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        FlappyBird flappyBird = new FlappyBird(mode,username,highScore);
        add(flappyBird);
        pack();
        flappyBird.requestFocus();
        setVisible(true);
    }
}
