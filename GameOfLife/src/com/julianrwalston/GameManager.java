package com.julianrwalston;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.TimeUnit;

public class GameManager {

    public static int CELL_SIZE = 1000;

    private int[][] startState;
    private int[][] boardState;
    private int[][] nextGen;
    private final int height;
    private final int width;

    private JFrame frame;
    private JPanel cellsPanel;
    private JPanel menuPanel;
    private JPanel[][] cells;
    private JButton startButton;
    private JButton resetButton;
    private JButton randomBoard;

    private boolean running = false;

    GameManager(int width, int height) {
        this.height = height;
        this.width = width;
        boardState = new int[height][width];
    }

    public void setUpGame() {
        boardState = randomState();
        startState = boardState;
        setUpGUI();
        display(boardState);
    }

    private void setUpGUI() {

        frame = new JFrame("Game of Life");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        cellsPanel = new JPanel();
        menuPanel = new JPanel();
        cells = new JPanel[height][width];
        startButton = new JButton("Start");
        resetButton = new JButton("Reset");
        randomBoard = new JButton("Random Board");

        cellsPanel.setLayout(new GridLayout(height, width));

        for (int col = 0; col < height; col++) {
            for (int row = 0; row < width; row++) {
                JPanel currentCell = new JPanel();
                currentCell.setSize(CELL_SIZE,CELL_SIZE);

                if (boardState[col][row] == 1) {
                    currentCell.setBackground(Color.BLACK);
                } else {
                    currentCell.setBackground(Color.WHITE);
                }
                cells[col][row] = currentCell;
                cellsPanel.add(cells[col][row]);
            }
        }

        // This button sets the game running when pressed.
        startButton.addActionListener(e -> {
            running = true;
            Thread newThread = new Thread(() -> {
                while (running) {
                    try {
                        TimeUnit.MILLISECONDS.sleep(100);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                    nextGeneration();
                }
            });
            newThread.start();
        });
        menuPanel.add(startButton);

        resetButton.addActionListener(e -> {
            running = false;
            boardState = startState;
            display(boardState);
        });
        menuPanel.add(resetButton);

        randomBoard.addActionListener(e -> {
            running = false;
            boardState = randomState();
            startState = boardState;
            display(boardState);
        });
        menuPanel.add(randomBoard);


        frame.setLayout(new BorderLayout());
        frame.add(cellsPanel, BorderLayout.CENTER);
        frame.add(menuPanel, BorderLayout.SOUTH);
        frame.pack();
        frame.setVisible(true);
    }

    public void nextGeneration() {
        nextGen = new int[height][width];

        for(int row = 0; row < width; row++) {
            for(int col = 0; col < height; col++) {

                int state = boardState[col][row];

                int neighbors = countNeighbors(boardState, row, col);

                // Based on rules of life
                if (state == 0 && neighbors == 3) {
                    nextGen[col][row] = 1;
                } else if (state == 1 && (neighbors < 2 || neighbors > 3)) {
                    nextGen[col][row] = 0;
                } else {
                    nextGen[col][row] = state;
                }
            }
        }
        int[][] temp = boardState;
        boardState = nextGen;
        nextGen = temp;

        display(boardState);
    }

    private void display(int[][] boardState) {
        for (int col = 0; col < height; col++) {
            for (int row = 0; row < width; row++) {
                if (boardState[col][row] == 1) {
                    cells[col][row].setBackground(Color.BLACK);
                } else {
                    cells[col][row].setBackground(Color.WHITE);
                }
                cells[col][row].repaint();
            }
        }
        frame.repaint();
    }

    private int countNeighbors(int[][] boardState, int row, int col) {
        int neighbors = 0;
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {

                if (boardState[(col + i + height) % height][(row + j + width) % width] == 1) {
                    if (i != 0 || j != 0) {
                        neighbors++;
                    }
                }
            }
        }
        return neighbors;
    }

    private int[][] randomState() {
        int[][] randState = new int[height][width];
        for (int row = 0; row < width; row++) {
            for (int col = 0; col < height; col++) {
                randState[col][row] = (int) Math.round(Math.random());
            }
        }
        return randState;
    }

    public void setBoardState(int[][] boardState) {
        this.boardState = boardState;
    }
}
