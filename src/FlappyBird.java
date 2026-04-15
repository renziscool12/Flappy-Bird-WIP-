import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

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

        int velocity = 0;
        int gravity = 1;

        Timer timer;

        int pipeX = 300;
        int gapY = 200;
        int gapHeight = 150;

        boolean isStarted = false;
        boolean passedPipe = false;
        int score = 0;

        GamePanel() {
            setFocusable(true); // Make the panel focusable to receive key events

            addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    // Handle bird movement here
                    if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                        if (!isStarted) {
                            isStarted = true;
                        } else {
                            velocity = -12; // Move the bird up when space is pressed
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

                if (pipeX < -50) {
                    pipeX = getWidth();
                    gapY = (int) (Math.random() * (getHeight() - gapHeight - 100) + 50);
                }

                if (pipeX + 50 < x && !passedPipe) {
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
                    JOptionPane.showMessageDialog(this, "Game Over!");
                    System.exit(0); // Exit the game
                }

                if (x < 0 || y < 0 || y + 30 > getHeight()) {
                    timer.stop(); // Stop the game if the bird goes out of bounds
                    JOptionPane.showMessageDialog(this, "Game Over!");
                    System.exit(0); // Exit the game
                }

                repaint(); // Repaint the panel
            });
            timer.start();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            // Draw background
            g.setColor(Color.CYAN);
            g.fillRect(0, 0, getWidth(), getHeight());

            // Draw bird
            g.setColor(Color.YELLOW);
            g.fillOval(x, y, 30, 30);

            // Draw pipes
            g.setColor(Color.GREEN);
            g.fillRect(pipeX, 0, 50, gapY); // Top pipe
            g.fillRect(pipeX, gapY + gapHeight, 50, getHeight()); // Bottom pipe

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
    }

    public static void main(String[] args) {
        new FlappyBird().setVisible(true);
    }
}