import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class FlappyBird extends JFrame {
    FlappyBird() {
        setTitle("Flappy Bird");
        setSize(400, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        add(new GamePanel());

    }

    class GamePanel extends JPanel {
        int x = 100;
        int y = 100;

        double velocity = 0;
        double gravity = 0.5;

        Timer timer;

        int pipeX = 300;
        int gapY = 200;
        int gapHeight = 150;

        boolean isStarted = false;
        boolean passedPipe = false;
        int score = 0;

        Clip hitsound;
        Clip dieSound;

        Image birdImage;
        Image pipeUpImage;
        Image pipeDownImage;
        Image backgroundImage;

        GamePanel() {
            setFocusable(true); // Make the panel focusable to receive key events
            birdImage = new ImageIcon(getClass().getResource("image/birdgame.png")).getImage();
            pipeUpImage = new ImageIcon(getClass().getResource("image/pipeupimage.png")).getImage();
            pipeDownImage = new ImageIcon(getClass().getResource("image/pipedownimage.png")).getImage();
            backgroundImage = new ImageIcon(getClass().getResource("image/background.png")).getImage();
            addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    // Handle bird movement here
                    if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                        if (!isStarted) {
                            isStarted = true;
                        } else {
                            velocity = -7; // Move the bird up when space is pressed
                            sounds("sounds/flap.wav"); // Play flap sound (make sure to have the sound file in the
                                                       // correct path)
                        }
                    }
                }
            });

            timer = new Timer(25, e -> {
                if (isStarted) { // Start the game loop only after the first space press
                    velocity += gravity; // Apply gravity
                    y += velocity; // Update bird's position
                    pipeX -= 5; // Move pipes to the left
                }

                if (velocity > 10) {
                    velocity = 10; // Limit the maximum falling speed
                }

                if (pipeX < -50) {
                    pipeX = getWidth();
                    gapY = (int) (Math.random() * (getHeight() - gapHeight - 100) + 50);
                }

                if (pipeX + 50 < x && !passedPipe) {
                    sounds("sounds/point.wav"); // Play point sound (make sure to have the sound file in the
                                                // correct path)
                    score++; // Increment score when the bird successfully passes a pipe
                    passedPipe = true;
                }

                if (pipeX >= x) {
                    passedPipe = false; // Reset passedPipe for the next pipe
                }

                Rectangle bird = new Rectangle(x, y, 30, 30);
                Rectangle topPipe = new Rectangle(pipeX, 0, 50, gapY);
                Rectangle bottomPipe = new Rectangle(pipeX, gapY + gapHeight, 50, getHeight());

                if (bird.intersects(topPipe) || bird.intersects(bottomPipe)) {
                    timer.stop(); // Stop the game if the bird collides with a pipe

                    dieSound("diesounds/hitsound.wav");
                    JOptionPane.showMessageDialog(this, "Game Over!");
                    System.exit(0);
                }

                if (x < 0 || y < 0 || y + 30 > getHeight()) {
                    timer.stop(); // Stop the game if the bird goes out of bounds
                    dieSound("diesonds/die.wav");
                    dieSound("diesounds/hitsound.wav");
                    JOptionPane.showMessageDialog(this, "Game Over!");
                    System.exit(0);
                }

                repaint(); // Repaint the panel
            });
            timer.start();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            // Draw background
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);

            // Draw bird
            g.drawImage(birdImage, x, y, 30, 30, this);

            // Draw pipes
            g.drawImage(pipeUpImage, pipeX, 0, 50, gapY, this); // Top pipe
            g.drawImage(pipeDownImage, pipeX, gapY + gapHeight, 50, getHeight() - gapY - gapHeight, this); // Bottom
                                                                                                           // pipe

            // Draw start message
            if (!isStarted) {
                g.setColor(Color.BLACK);
                g.setFont(new Font("Arial", Font.BOLD, 24));
                g.drawString("Press SPACE to Start", 80, getHeight() / 2);
            }
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.BOLD, 18));
            g.drawString("Score: " + score, 10, 30);
        }

        // Method to play sounds
        public void sounds(String sounds) {
            // try to play the sound file
            try {
                File audioFile = new File(sounds); // Replace with the path to your sound file
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile); // Get audio input stream
                                                                                           // from the file

                Clip clip = AudioSystem.getClip(); // Get a sound clip resource
                clip.open(audioStream); // Open the audio clip and load samples from the audio input stream
                clip.start(); // Play the audio clip
                // catch any exceptions that may occur
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }

        // Method to play die sound
        public void dieSound(String diesounds) {
            try {

                dieSound = AudioSystem.getClip();
                dieSound.open(AudioSystem.getAudioInputStream(new File("diesounds/die.wav")));

                // Create a timer to delay the playback of the die sound by 300 milliseconds
                Timer t = new Timer(300, e -> {
                    dieSound.setFramePosition(0);
                    dieSound.start();
                });

                // Set the timer to repeat only once (play the sound once)
                t.setRepeats(false);
                t.start();

                hitsound = AudioSystem.getClip();
                hitsound.open(AudioSystem.getAudioInputStream(new File("diesounds/hitsound.wav")));
                hitsound.start();

            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }
    }

    public static void main(String[] args) {
        new FlappyBird().setVisible(true);
    }
}