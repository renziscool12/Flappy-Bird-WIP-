import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;

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
        int x = 100; // Horizontal position of the bird
        int y = 100; // Vertical position of the bird

        double velocity = 0; // Vertical velocity of the bird
        double gravity = 0.5; // Gravity effect on the bird

        Timer timer;
        int gapHeight = 100; // Height of the gap between pipes

        boolean isStarted = false;
        boolean passedPipe = false;
        int score = 0;

        Clip hitsound;
        Clip dieSound;

        Image birdImage;
        Image pipeUpImage;
        Image pipeDownImage;
        Image backgroundImage;

        ArrayList<Pipe> pipes = new ArrayList<>();

        GamePanel() {
            setFocusable(true); // Make the panel focusable to receive key events
            birdImage = new ImageIcon(getClass().getResource("image/birdgame.png")).getImage();
            pipeUpImage = new ImageIcon(getClass().getResource("image/pipeupimage.png")).getImage();
            pipeDownImage = new ImageIcon(getClass().getResource("image/pipedownimage.png")).getImage();
            backgroundImage = new ImageIcon(getClass().getResource("image/background.png")).getImage();

            pipes.add(new Pipe(400, 200)); // Add an initial pipe to the game

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
                if (!isStarted) { // Start the game loop only after the first space press
                    repaint();
                    return;
                }

                velocity += gravity; // Apply gravity
                y += velocity; // Update bird's position

                // Limit the falling speed of the bird
                if (velocity > 10) {
                    velocity = 10; // Limit the maximum falling speed
                }

                for (Pipe p : pipes) {
                    p.x -= 5; // Move each pipe to the left
                }
                if (!pipes.isEmpty() && pipes.get(pipes.size() - 1).x < 200) { // Check if the pipe is close to the bird
                    int gapY = (int) (Math.random() * 200) + 100; // Randomize the gap's vertical position
                    pipes.add(new Pipe(400, gapY)); // Add a new pipe when the last one is close to the left edge

                }

                pipes.removeIf(p -> p.x < -50); // Remove pipes that have moved off the screen

                // Check if the bird has passed a pipe and update the score
                for (Pipe p : pipes) {
                    if (!p.isPassed && p.x + 50 < x) { // Check if the pipe has been passed
                        score++; // Increment score
                        p.isPassed = true; // Mark the pipe as passed
                        sounds("sounds/point.wav");
                    }
                }

                Rectangle bird = new Rectangle(x, y, 30, 30);
                // Check for collisions with pipes
                for (Pipe p : pipes) {
                    Rectangle topPipe = new Rectangle(p.x, 0, 50, p.gapY);
                    Rectangle bottomPipe = new Rectangle(p.x, p.gapY + gapHeight, 50, getHeight());

                    if (bird.intersects(topPipe) || bird.intersects(bottomPipe)) {
                        timer.stop(); // Stop the game if the bird collides with a pipe

                        dieSound("diesounds/hitsound.wav");
                        JOptionPane.showMessageDialog(this, "Game Over!");
                        System.exit(0);
                    }
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

            // For loop to draw pipes
            for (Pipe p : pipes) {
                g.drawImage(pipeUpImage, p.x, 0, 50, p.gapY, this);
                g.drawImage(pipeDownImage, p.x, p.gapY + gapHeight, 50, getHeight(), this);
            }

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