import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;

public class Main {
    private JFrame mainFrame;
    private GamePanel gamePanel;
    private JPanel startPanel;
    private JButton startButton, restartButton;
    private JSlider difficultySlider;

    public Main() {
        mainFrame = new JFrame("Bubble Game");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setLayout(new CardLayout());

        setupStartPanel();
        gamePanel = new GamePanel(this);

        mainFrame.add(startPanel, "Start");
        mainFrame.add(gamePanel, "Game");

        mainFrame.pack();
        mainFrame.setVisible(true);
    }

    private void setupStartPanel() {
        startPanel = new JPanel();
        startButton = new JButton("Start");
        restartButton = new JButton("Restart");

        difficultySlider = new JSlider(JSlider.HORIZONTAL, 4, 6, 5);
        difficultySlider.setMajorTickSpacing(1);
        difficultySlider.setPaintTicks(true);
        difficultySlider.setPaintLabels(true);

        Hashtable<Integer, JLabel> labelTable = new Hashtable<>();
        labelTable.put(4, new JLabel("Easy"));
        labelTable.put(5, new JLabel("Medium"));
        labelTable.put(6, new JLabel("Hard"));
        difficultySlider.setLabelTable(labelTable);

        startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                switchToGamePanel();
            }
        });

        restartButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                gamePanel.resetGame();
                switchToStartPanel();
            }
        });

        startPanel.add(startButton);
        startPanel.add(restartButton);
        startPanel.add(difficultySlider);
    }

    private void switchToGamePanel() {
        gamePanel.startGame(difficultySlider.getValue());
        CardLayout cardLayout = (CardLayout) (mainFrame.getContentPane().getLayout());
        cardLayout.show(mainFrame.getContentPane(), "Game");
    }

    void switchToStartPanel() {
        CardLayout cardLayout = (CardLayout) (mainFrame.getContentPane().getLayout());
        cardLayout.show(mainFrame.getContentPane(), "Start");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Main();
            }
        });
    }
}
