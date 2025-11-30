package flappybirdgame;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;
import javax.sound.sampled.*;
import java.io.*;
import java.sql.*;
public class FlappyBird extends JPanel implements ActionListener, KeyListener {
    int boardWidth = 900;
    int boardHeight = 800;
    private String username;
    
    
    Image backgroundImg;
    Image birdImg;
    Image topPipeImg;
    Image bottomPipeImg;
    Image backgroundImgDay, backgroundImgDark;
    Image birdImgDay, birdImgDark;
    Image topPipeImgDay, bottomPipeImgDay;
    Image topPipeImgDark, bottomPipeImgDark;
    
    
    int birdWidth = 50;
    int birdHeight = 50;
    int birdX = boardWidth / 8;
    int birdY = boardWidth / 2 - birdHeight / 2;

    class Bird {
        int x = birdX;
        int y = birdY;
        int width = birdWidth;
        int height = birdHeight;
        Image img;

        Bird(Image img) {
            this.img = img;
        }

        Rectangle getBounds() {
            return new Rectangle(x, y, width, height);
        }
    }

    int pipeX = boardWidth;
    int pipeY = 0;
    int pipeWidth = 100;
    int pipeHeight = 400;

    class Pipe {
        int x = pipeX;
        int y = pipeY;
        int width = pipeWidth;
        int height = pipeHeight;
        Image img;
        boolean passed = false;
        int velocityY = 0;

        Pipe(Image img) {
            this.img = img;
        }

        Rectangle getBounds() {
            return new Rectangle(x, y, width, height);
        }

        void moveVertically() {
            y += velocityY;
            if (y <= -pipeHeight / 2 || y >= boardHeight - (boardHeight / 4)) {
                velocityY *= -1;
            }
        }
    }

    Bird bird;
    int velocityX = -4;
    int velocityY = 0;
    int gravity = 1;
    ArrayList<Pipe> pipes;
    Random random = new Random();
    Timer gameLoop;
    Timer placePipeTimer;
    boolean gameOver = false;
    double score = 0;
    int highScore = 0;
    int speedIncreaseInterval = 5000; 
    int speedIncreaseAmount = 1; 


    Clip jumpSound;
    Clip collisionSound;
    Clip gameOverSound;
    Clip pointSound;

    public FlappyBird(String mode, String username, int highScore) {
    	
    	 this.username = username;
         this.highScore = highScore;
         
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setFocusable(true);
        addKeyListener(this);

       
        backgroundImgDay = new ImageIcon(getClass().getResource("./Background2.jpg")).getImage();
        birdImgDay = new ImageIcon(getClass().getResource("./Bird211.png")).getImage();
        bottomPipeImg = new ImageIcon(getClass().getResource("./TubeBody12.png")).getImage();
        topPipeImg= new ImageIcon(getClass().getResource("./TubeBody.png")).getImage();
        
   
        backgroundImgDark = new ImageIcon(getClass().getResource("./potterhead.jpg")).getImage();
        birdImgDark = new ImageIcon(getClass().getResource("./harryPotter.png")).getImage();
       // topPipeImgDark = new ImageIcon(getClass().getResource("./harryPipetop.png")).getImage();
      //  bottomPipeImgDark = new ImageIcon(getClass().getResource("./harryPipebottom.png")).getImage();
        

        if ("day".equals(mode)) {
            backgroundImg = backgroundImgDay;
            birdImg = birdImgDay;
            //bottomPipeImg =  bottomPipeImgDay;      
           // topPipeImg = topPipeImgDay;
        } else if ("dark".equals(mode)) {
            backgroundImg = backgroundImgDark;
            birdImg = birdImgDark;
            //bottomPipeImg =  bottomPipeImgDark;      
           // topPipeImg = topPipeImgDark;
        }
        
        bird = new Bird(birdImg);
        pipes = new ArrayList<Pipe>();

        placePipeTimer = new Timer(2000, e -> placePipes());
        placePipeTimer.start();

        gameLoop = new Timer(1000 / 60, this);
        gameLoop.start();
        
        Timer speedIncreaseTimer = new Timer(speedIncreaseInterval, e -> {
            velocityX -= speedIncreaseAmount; 
        });
        speedIncreaseTimer.start();
        
        jumpSound = loadSound("jump.wav");
        collisionSound = loadSound("collison.wav");
        pointSound = loadSound("point.wav");
        gameOverSound = loadSound("gameover.wav");
    }

    private Clip loadSound(String fileName) {
        try {
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(getClass().getResource(fileName));
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            return clip;
        } catch (Exception e) {
            System.err.println("Error loading sound: " + fileName);
            return null;
        }
    }

    private void playSound(Clip clip) {
        if (clip != null) {
            clip.setFramePosition(0); 
            clip.start();
        }
    }    
   
    private Image loadImage(String fileName) {
        try {
            return new ImageIcon(getClass().getResource(fileName)).getImage();
        } catch (Exception e) {
            System.err.println("Error loading image: " + fileName);
            return null;
        }
    }

    void placePipes() {
        int randomPipeY = (int) (pipeY - pipeHeight / 4 - Math.random() * (pipeHeight / 2));
        int openingSpace = boardHeight / 4;

        Pipe topPipe = new Pipe(topPipeImg);
        topPipe.y = randomPipeY;
        pipes.add(topPipe);

        Pipe bottomPipe = new Pipe(bottomPipeImg);
        bottomPipe.y = topPipe.y + pipeHeight + openingSpace;
        pipes.add(bottomPipe);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        g.drawImage(backgroundImg, 0, 0, this.boardWidth, this.boardHeight, null);
        g.drawImage(birdImg, bird.x, bird.y, bird.width, bird.height, null);
        
        for (Pipe pipe : pipes) {
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }

        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.PLAIN, 32));

        if (gameOver) {
            g.drawString("Game Over: " + (int) score, 10, 35);
            g.drawString("High Score: " + highScore, 10, 70);
        }else {
            g.drawString(String.valueOf((int) score), 10, 35);
        }
    }

    public void move() {
        velocityY += gravity;
        bird.y += velocityY;
        bird.y = Math.max(bird.y, 0);

        for (Pipe pipe : pipes) {
            pipe.x += velocityX;

            if (score > 2) {
                pipe.velocityY = 2;
                pipe.moveVertically();
            }

            if (!pipe.passed && bird.x > pipe.x + pipe.width) {
                score += 0.5;
                pipe.passed = true;
                playSound(pointSound);
            }

            if (collision(bird, pipe)) {
            	playSound(collisionSound);
                gameOver = true;
            }
        }

        if (bird.y > boardHeight) {
        	playSound(collisionSound);
            gameOver = true;
        }
    }

    boolean collision(Bird a, Pipe b) {
        return a.x < b.x + b.width && a.x + a.width > b.x && a.y < b.y + b.height && a.y + a.height > b.y;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if (gameOver) {
            placePipeTimer.stop();
            gameLoop.stop();
            
            // Update high score if current score is higher
            if ((int) score > highScore) {
                highScore = (int) score;
                updateHighScore(username, highScore);
            }
            
            playSound(gameOverSound);
          
                                          
            int option = JOptionPane.showConfirmDialog(this, "Game Over! Your score is: " + (int) score + "\nHigh Score: " +  highScore + "\nDo you want to play again?", "Game Over", JOptionPane.YES_NO_OPTION);

            if (option == JOptionPane.YES_OPTION) {
                restartGame();
            } else {
                System.exit(0);
            }
        }
    }
    private void updateHighScore(String username, int newHighScore) {
        String querySelect = "SELECT score FROM users WHERE username = ?";
        String queryUpdate = "UPDATE users SET score = ? WHERE username = ?";

        try (Connection conn = getConnection();
             PreparedStatement selectStmt = conn.prepareStatement(querySelect);
             PreparedStatement updateStmt = conn.prepareStatement(queryUpdate)) {

            // Fetch the current high score
            selectStmt.setString(1, username);
            ResultSet rs = selectStmt.executeQuery();
            if (rs.next()) {
                int currentHighScore = rs.getInt("score");

                // Update the high score if the new score is higher
                if (newHighScore > currentHighScore) {
                    updateStmt.setInt(1, newHighScore);
                    updateStmt.setString(2, username);
                    updateStmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Connection getConnection() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/flappybird";
        String user = "root";
        String password = "root";
        return DriverManager.getConnection(url, user, password);
    }

    void restartGame() {
        bird.y = birdY;
        velocityY = 0;
        pipes.clear();
        gameOver = false;
        score = 0;
        gameLoop.start();
        placePipeTimer.start();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            velocityY = -9;
            playSound(jumpSound);
            if (gameOver) {
                restartGame();
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}
}
