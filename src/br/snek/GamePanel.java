package br.snek;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {

    static final int SCREEN_WIDTH = 600;
    static final int SCREEN_HEIGHT = 600;
    static final int UNIT_SIZE = 25;
    static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / UNIT_SIZE;
    static int DELAY = 120;
    final int x[] = new int[GAME_UNITS];
    final int y[] = new int[GAME_UNITS];
    int bodyParts = 2;
    int fudEaten;
    int fudX;
    int fudY;
    char direction = 'R';
    boolean running = false;
    Timer timer;
    Random random;
    //BufferedImage snekHead, snekFud;

    GamePanel() {
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.black);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        startGame();
    }

    public void startGame() {
        newFud();
        running = true;
        timer = new Timer(DELAY, this);
        timer.start();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        try {
            draw(g);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void draw(Graphics g) throws IOException {
        if (running) {
            /* Grid lines
            for (int i = 0; i < SCREEN_HEIGHT / UNIT_SIZE; i++) {
                g.drawLine(i * UNIT_SIZE, 0, i * UNIT_SIZE, SCREEN_HEIGHT);
                g.drawLine(0, i * UNIT_SIZE, SCREEN_WIDTH, i * UNIT_SIZE);
            }
            */
            g.setColor(Color.red);
            g.fillOval(fudX, fudY, UNIT_SIZE, UNIT_SIZE);
            /* Use a .png as a snake food
            snekFud = ImageIO.read(new FileInputStream("res/snekFud.png"));
            g.drawImage(snekFud, fudX, fudY, null);
             */
            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) {
                    g.setColor(Color.green.darker());
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                    /* Use a .png as a snake head
                    snekHead = ImageIO.read(new FileInputStream("res/snekHead.png"));
                    g.drawImage(snekHead, x[i], y[i], null);
                    */

                } else {
                    g.setColor(Color.green);
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                }
            }
            g.setColor(Color.white);
            g.setFont(new Font("Arial", Font.PLAIN, 20));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("Score: " + fudEaten, (SCREEN_WIDTH - metrics.stringWidth("Score: " + fudEaten)) / 2, g.getFont().getSize());
        } else {
            gameOver(g);
        }
    }

    public void newFud() {
        fudX = random.nextInt(SCREEN_WIDTH / UNIT_SIZE) * UNIT_SIZE;
        fudY = random.nextInt(SCREEN_HEIGHT / UNIT_SIZE) * UNIT_SIZE;
    }

    public void move() {
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }
        switch (direction) {
            case 'U' -> y[0] = y[0] - UNIT_SIZE;
            case 'D' -> y[0] = y[0] + UNIT_SIZE;
            case 'R' -> x[0] = x[0] + UNIT_SIZE;
            case 'L' -> x[0] = x[0] - UNIT_SIZE;
        }
    }

    public void checkFud() {
        if ((x[0] == fudX) && (y[0] == fudY)) {
            bodyParts++;
            fudEaten++;
            DELAY -= 5;
            newFud();
        }
    }

    public void checkCollisions() {
        //checks if head collides with body
        for (int i = bodyParts; i > 0; i--) {
            if ((x[0] == x[i]) && (y[0] == y[i])) {
                running = false;
            }
        }
        //checks if head touches left
        if (x[0] < 0) {
            running = false;
        }
        //checks if head touches right
        if (x[0] == SCREEN_WIDTH) {
            running = false;
        }
        //checks if head touches top
        if (y[0] < 0) {
            running = false;
        }
        //checks if head touches bottom
        if (y[0] == SCREEN_HEIGHT) {
            running = false;
        }

        if (!running) {
            timer.stop();
        }
    }

    public void gameOver(Graphics g) {
        //GameOver text
        g.setColor(Color.white);
        g.setFont(new Font("Arial Black", Font.PLAIN, 75));
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.drawString("Game Over", (SCREEN_WIDTH - metrics.stringWidth("Game Over")) / 2, SCREEN_HEIGHT / 2);
        //Score
        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.PLAIN, 20));
        FontMetrics metrics2 = getFontMetrics(g.getFont());
        g.drawString("Score: " + fudEaten, (SCREEN_WIDTH - metrics2.stringWidth("Score: " + fudEaten)) / 2, g.getFont().getSize());

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkFud();
            checkCollisions();
        }
        repaint();
    }

    public class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            int keyCode = e.getKeyCode();
            if (keyCode == KeyEvent.VK_LEFT || keyCode == KeyEvent.VK_A) {
                if (direction != 'R') {
                    direction = 'L';
                }
            } else if (keyCode == KeyEvent.VK_RIGHT || keyCode == KeyEvent.VK_D) {
                if (direction != 'L') {
                    direction = 'R';
                }
            } else if (keyCode == KeyEvent.VK_UP || keyCode == KeyEvent.VK_W) {
                if (direction != 'D') {
                    direction = 'U';
                }
            } else if (keyCode == KeyEvent.VK_DOWN || keyCode == KeyEvent.VK_S) {
                if (direction != 'U') {
                    direction = 'D';
                }
            }
        }

    }
}
