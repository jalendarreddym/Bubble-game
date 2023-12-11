import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Random;

public class GamePanel extends JPanel {
    private static final Random random = new Random();
    private Main mainFrameReference;
    private int difficultyLevel;
    private ArrayList<Bubble> bubbles;
    private Timer gameTimer;
    private int remainingTime;
    private int round;
    private int score;
    private boolean isGameOver;

    private JButton resetButton, homeButton;
    private JLabel scoreLabel, timeLabel, roundLabel;
    private JPanel controlPanel;

    public GamePanel(Main mainFrame) {
        this.mainFrameReference = mainFrame;
        setPreferredSize(new Dimension(800, 600));
        setBackground(Color.WHITE);
        setLayout(new BorderLayout());

        initializeGameState();
        createControlPanel();
        setupMouseInteractions();
        add(controlPanel, BorderLayout.NORTH);
    }

    private void initializeGameState() {
        bubbles = new ArrayList<>();
        round = 1;
        score = 0;
        remainingTime = 15;
        isGameOver = false;

        initializeGameTimer();
    }

    private void initializeGameTimer() {
        gameTimer = new Timer(1000 - (round - 1) * 100, e -> updateGameEachSecond());
    }

    private void createControlPanel() {
        controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controlPanel.setPreferredSize(new Dimension(getWidth(), 35));
        controlPanel.setBackground(Color.LIGHT_GRAY);

        resetButton = new JButton("Reset");
        homeButton = new JButton("Home");

        scoreLabel = new JLabel("Score: " + score);
        timeLabel = new JLabel("Time: " + remainingTime);
        roundLabel = new JLabel("Round: " + round);

        resetButton.addActionListener(e -> resetGame());
        homeButton.addActionListener(e -> mainFrameReference.switchToStartPanel());

        controlPanel.add(resetButton);
        controlPanel.add(homeButton);
        controlPanel.add(scoreLabel);
        controlPanel.add(timeLabel);
        controlPanel.add(roundLabel);
    }

    private void setupMouseInteractions() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (isGameOver) {
                    return;
                }


                if (!gameTimer.isRunning() && bubbles.size() < difficultyLevel) {
                    Bubble newBubble = new Bubble(e.getX(), e.getY());
                    if (isColliding(newBubble)) {
                        JOptionPane.showMessageDialog(GamePanel.this, "Selected bubble is at the same position, try to select another position", "Position Overlap", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    bubbles.add(newBubble);
                    repaint();

                    if (bubbles.size() == difficultyLevel) {
                        startGame(difficultyLevel);
                    }

                    return;
                }


                if (gameTimer.isRunning()) {
                    boolean bubbleBurst = false;
                    for (int i = 0; i < bubbles.size(); i++) {
                        if (bubbles.get(i).checkCollision(e.getX(), e.getY())) {
                            bubbles.remove(i);
                            score++;
                            updateGameLabels();
                            bubbleBurst = true;
                            break;
                        }
                    }


                    if (bubbles.isEmpty()) {
                        startNewRound();
                    }
                }
            }
        });
    }



    protected boolean isClickOnControl(Point point) {
        return controlPanel.getBounds().contains(point);
    }

    private void updateGameEachSecond() {
        if (!isGameOver) {
            remainingTime--;
            timeLabel.setText("Time: " + remainingTime);
            moveBubbles();

            if (remainingTime <= 0) {
                endGame();
            }
            repaint();
        }
    }


    public void resetGame() {
        round = 1;
        score = 0;
        remainingTime = 15;
        isGameOver = false;
        bubbles.clear();
        updateGameLabels();


        if (gameTimer != null) {
            gameTimer.stop();
        }
        gameTimer = new Timer(1000 - (round - 1) * 100, e -> updateGameEachSecond());

        repaint(); 
    }


    public void startGame(int difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
        if (bubbles.size() == difficultyLevel) {
            gameTimer.start();
        }
    }

    private void startNewRound() {
        if (round < 10) {
            round++;
            remainingTime = 15 - round;
            bubbles.clear();
            repositionGlobal();  
            updateGameLabels();
            initializeGameTimer();
            gameTimer.start();  
        } else {
            endGame();
        }
    }


    private void updateGameLabels() {
        scoreLabel.setText("Score: " + score);
        timeLabel.setText("Time: " + remainingTime);
        roundLabel.setText("Round: " + round);
    }

    private void moveBubbles() {
        if (gameTimer.isRunning()) {
            for (Bubble bubble : bubbles) {
                bubble.move(getWidth(), getHeight(), controlPanel.getHeight(), round);
            }
        }
    }


    private void endGame() {
        isGameOver = true;
        gameTimer.stop();
        String message = "Game Over. You reached round " + round + " with a score of " + score + "!";
        JOptionPane.showMessageDialog(this, message, "Game Over", JOptionPane.INFORMATION_MESSAGE);
    }

    private void repositionGlobal() {
        bubbles.clear();
        for (int i = 0; i < difficultyLevel; i++) {
            int x = random.nextInt(getWidth() - Bubble.FIXED_RADIUS);
            int y = random.nextInt(getHeight() - Bubble.FIXED_RADIUS - controlPanel.getHeight()) + controlPanel.getHeight();
            bubbles.add(new Bubble(x, y));
        }
    }

    private boolean isColliding(Bubble newBubble) {
        for (Bubble bubble : bubbles) {
            if (newBubble.checkCollision(bubble.x, bubble.y)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (Bubble bubble : bubbles) {
            bubble.draw(g);
        }
    }

    public static class Bubble {
        int x, y;
        static final int FIXED_RADIUS = 30;
        int xDirection = 1;
        int yDirection = 1;

        public Bubble(int x, int y) {
            this.x = x;
            this.y = y;
            if (random.nextBoolean()) xDirection = -1;
            if (random.nextBoolean()) yDirection = -1;
        }

        public void draw(Graphics g) {
            g.setColor(Color.BLUE);
            g.fillOval(x, y, FIXED_RADIUS, FIXED_RADIUS);
        }

        public void move(int panelWidth, int panelHeight, int controlPanelHeight, int round) {

            int movementSpeed = 4;

            x += (random.nextInt(movementSpeed * 2) - movementSpeed);
            y += (random.nextInt(movementSpeed * 2) - movementSpeed);


            x = Math.max(x, 0);
            y = Math.max(y, controlPanelHeight);
            x = Math.min(x, panelWidth - FIXED_RADIUS);
            y = Math.min(y, panelHeight - FIXED_RADIUS);
        }

        public boolean checkCollision(int bubbleX, int bubbleY) {
            double distanceX = this.x + FIXED_RADIUS / 2 - (bubbleX + FIXED_RADIUS / 2);
            double distanceY = this.y + FIXED_RADIUS / 2 - (bubbleY + FIXED_RADIUS / 2);
            double radiusSum = FIXED_RADIUS;
            return Math.sqrt(distanceX * distanceX + distanceY * distanceY) < radiusSum;
        }
    }
}
