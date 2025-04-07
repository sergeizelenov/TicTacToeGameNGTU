package ru.ngtu.zelenov.tictactoe;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


//Класс графического интерфейса
public class TicTacToeGame extends JFrame implements ActionListener {

    private TicTacToeGameLogic gameLogic;
    private JButton[][] buttons;
    private JLabel statusLabel;

    // Цвета для X и O
    private static final Color COLOR_X = Color.RED;
    private static final Color COLOR_O = Color.BLUE;

    /**
     * Конструктор класса GUI.
     */
    public TicTacToeGame() {
        gameLogic = new TicTacToeGameLogic();
        initializeGUI();
        gameLogic.startGame();
        updateBoardView();
    }

    /**
     * Настраивает окно и создает все графические элементы.
     */
    private void initializeGUI() {
        setTitle("Крестики-Нолики (Аттестационная работа Зеленов С.А. НГТУ)");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JPanel boardPanel = new JPanel();
        boardPanel.setLayout(new GridLayout(3, 3, 5, 5));
        buttons = new JButton[3][3];
        Font buttonFont = new Font("Arial", Font.BOLD, 100);
        // Оставляем предпочтительный размер для кнопок
        Dimension buttonSize = new Dimension(80, 80);

        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                buttons[x][y] = new JButton();
                buttons[x][y].setFont(buttonFont);
                buttons[x][y].setPreferredSize(buttonSize); // Используем предпочтительный размер
                buttons[x][y].setFocusable(false);
                buttons[x][y].addActionListener(this);
                buttons[x][y].putClientProperty("x", x);
                buttons[x][y].putClientProperty("y", y);
                boardPanel.add(buttons[x][y]);
            }
        }

        statusLabel = new JLabel(" ", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 16));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        add(boardPanel, BorderLayout.CENTER);
        add(statusLabel, BorderLayout.SOUTH);

        setSize(600, 700); // Устанавливаем точный размер окна 600x700

        setResizable(false); // Оставляем окно нерастягиваемым
        setLocationRelativeTo(null);
        // setVisible(true) вызывается в main
    }

    /**
     * Обновляет текст, цвет и активность кнопок на поле.
     */
    private void updateBoardView() {
        int[][] model = gameLogic.getModel();
        boolean isGameActive = !gameLogic.isGameStopped(); // Проверяем, активна ли игра

        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                String symbol = gameLogic.getSymbol(model[x][y]);

                buttons[x][y].setText(symbol);

                // Устанавливаем цвет текста
                if (symbol.equals("X")) {
                    buttons[x][y].setForeground(COLOR_X); // Красный для X
                } else if (symbol.equals("O")) {
                    buttons[x][y].setForeground(COLOR_O); // Синий для O
                } else {
                    // Для пустых используем цвет по умолчанию
                    buttons[x][y].setForeground(null);
                }

                // Кнопка активна, если игра не остановлена.
                buttons[x][y].setEnabled(isGameActive);
            }
        }

        // Обновляем текст в метке статуса
        if (!isGameActive) { // Если игра остановлена
            if (gameLogic.checkBoardWin(1)) {
                statusLabel.setText("Вы победили (X)!");
            } else if (gameLogic.checkBoardWin(2)) {
                statusLabel.setText("Компьютер победил (O)!");
            } else {
                statusLabel.setText("Ничья!");
            }
        } else { // Если игра активна
            statusLabel.setText(gameLogic.getCurrentPlayer() == 1 ? "Ваш ход (X)" : "Ход компьютера (O)");
        }
    }

    /**
     * Обработчик нажатия на кнопки поля.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameLogic.isGameStopped() || gameLogic.getCurrentPlayer() != 1) {
            return;
        }

        JButton clickedButton = (JButton) e.getSource();
        int x = (int) clickedButton.getClientProperty("x");
        int y = (int) clickedButton.getClientProperty("y");

        boolean playerMoveEndedGame = gameLogic.processMove(x, y);

        if (playerMoveEndedGame || gameLogic.getCurrentPlayer() == 2) {
            updateBoardView();

            if (playerMoveEndedGame) {
                handleGameEnd();
            } else {
                statusLabel.setText("Ход компьютера (O)...");
                Timer timer = new Timer(500, event -> {
                    int[] computerCoords = gameLogic.computerTurn();
                    if (computerCoords != null) {
                        boolean computerMoveEndedGame = gameLogic.processMove(computerCoords[0], computerCoords[1]);
                        updateBoardView();
                        if (computerMoveEndedGame) {
                            handleGameEnd();
                        }
                    }
                    ((Timer)event.getSource()).stop();
                });
                timer.setRepeats(false);
                timer.start();
            }
        }
    }

    /**
     * Обрабатывает завершение игры.
     */
    private void handleGameEnd() {
        String finalMessage = statusLabel.getText();
        int choice = JOptionPane.showConfirmDialog(
                this,
                finalMessage + "\nСыграть еще раз?",
                "Игра окончена",
                JOptionPane.YES_NO_OPTION
        );

        if (choice == JOptionPane.YES_OPTION) {
            gameLogic.startGame();
            updateBoardView();
        } else {
            // Кнопки уже неактивны, если игра остановлена
        }
    }

    /**
     * Главный метод - точка входа в программу.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                TicTacToeGame game = new TicTacToeGame();
                game.setVisible(true);
            }
        });
    }

}




