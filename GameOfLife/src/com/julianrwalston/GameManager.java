package com.julianrwalston;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by Julian Walston on 6/15/2021
 */

/*
TODO: Implement way to change width/height in-game
 */

public class GameManager implements Serializable {

    private String defaultDirectory =
            "C:\\Users\\Julian Walston\\Documents\\IdeaProjects\\game-of-life\\GameOfLife\\savedGames";

    private int[][] seed;
    private int[][] boardState;
    private int[][] nextGen;
    private int height;
    private int width;

    private JFrame frame;
    private JPanel mainPanel;
    private JPanel cellsPanel;
    private JPanel menuPanel;
    private JPanel[][] cells;
    private JMenuBar menu;
    private JMenu fileMenu;
    private JMenuItem saveMenuItem;
    private JMenuItem loadMenuItem;
    private JMenu editMenu;
    private JMenuItem editSeedMenuItem;
    private JMenuItem createNewSeedMenuItem;
    private JButton playPauseButton;
    private JButton stepButton;
    private JButton resetButton;
    private JButton randomBoardButton;

    private boolean running = false;

    GameManager(int width, int height) {
        this.height = height;
        this.width = width;
        boardState = new int[height][width];
    }

    public void setUpGame() {
        boardState = randomState();
        seed = boardState;

        frame = new JFrame("Game of Life");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);

        setUpGUI(width, height);
        display(boardState);
    }

    private void setUpGUI(int width, int height) {
        this.width = width;
        this.height = height;

        initializeComponents();

        createBoard();

        setListeners();

        addComponentsToFrame();
    }

    /**
     * Helper method for setUpGUI
     * creates GUI components
     */
    private void initializeComponents() {
        mainPanel = new JPanel();
        cellsPanel = new JPanel();
        menuPanel = new JPanel();
        cells = new JPanel[height][width];

        menu = new JMenuBar();
        fileMenu = new JMenu("File");
        saveMenuItem = new JMenuItem("Save Seed");
        loadMenuItem = new JMenuItem("Load Seed");
        editMenu = new JMenu("Edit");
        editSeedMenuItem = new JMenuItem("Edit Seed");
        createNewSeedMenuItem = new JMenuItem("Create New Seed");

        playPauseButton = new JButton("Play/Pause");
        stepButton = new JButton("Step");
        resetButton = new JButton("Reset");
        randomBoardButton = new JButton("Random Seed");
    }

    /**
     * Helper method for setUpGUI
     * creates a cells to form the board based on the current board state
     */
    private void createBoard() {
        cellsPanel.setLayout(new GridLayout(height, width));

        for (int col = 0; col < height; col++) {
            for (int row = 0; row < width; row++) {
                JPanel currentCell = new JPanel();

                if (boardState[col][row] == 1) {
                    currentCell.setBackground(Color.BLACK);
                } else {
                    currentCell.setBackground(Color.WHITE);
                }
                cells[col][row] = currentCell;
                cellsPanel.add(cells[col][row]);
            }
        }
    }

    /**
     * Helper method for setUpGUI
     * creates actionListeners for the buttons and menuItems
     */
    private void setListeners() {
        // This button sets the game running when pressed.
        playPauseButton.addActionListener(e -> {
            if (running) {
                running = false;
            } else {
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
            }
        });

        stepButton.addActionListener(e -> {
            if (!running) {
                nextGeneration();
            }
        });

        resetButton.addActionListener(e -> {
            running = false;
            boardState = seed;
            display(boardState);
        });

        randomBoardButton.addActionListener(e -> {
            running = false;
            boardState = randomState();
            seed = boardState;
            display(boardState);
        });

        saveMenuItem.addActionListener(e -> {
            try {
                JFileChooser fc = new JFileChooser(defaultDirectory);
                fc.showSaveDialog(frame);

                if (fc.getSelectedFile() != null) {

                    //Saving of object in a file
                    FileOutputStream file = new FileOutputStream(fc.getSelectedFile().getAbsolutePath() + ".gol");
                    ObjectOutputStream os = new ObjectOutputStream(file);

                    // Method for serialization of object
                    os.writeObject(seed);

                    os.close();
                    file.close();
                }
            }
            catch(IOException ex) {
                System.out.println("IOException is caught");
                ex.printStackTrace();
            }
        });

        loadMenuItem.addActionListener(e -> {
            try {
                JFileChooser fc = new JFileChooser(defaultDirectory);
                FileFilter filter =
                        new FileNameExtensionFilter(".gol Files", "gol");
                fc.setFileFilter(filter);
                fc.showOpenDialog(frame);
                //Saving of object in a file
                if (fc.getSelectedFile() != null) {
                    FileInputStream file = new FileInputStream(fc.getSelectedFile().getAbsolutePath());
                    ObjectInputStream is = new ObjectInputStream(file);

                    // Method for serialization of object
                    boardState = (int[][]) is.readObject();

                    // Destroy current GUI (keeping frame)
                    mainPanel.setVisible(false);

                    // Reset GUI with new width/height
                    setUpGUI(boardState[0].length, boardState.length);
                    seed = boardState;
                    display(boardState);

                    is.close();
                    file.close();
                }
            }
            catch(IOException io) {
                System.out.println("IOException");
                io.printStackTrace();
            } catch (ClassNotFoundException cnf) {
                System.out.println("Class not found");
                cnf.printStackTrace();
            }
        });

        editSeedMenuItem.addActionListener(e -> {
            SeedEditor editor = new SeedEditor();
            editor.open(boardState);
        });

        createNewSeedMenuItem.addActionListener(e -> {
            int[][] emptyBoard = new int[height][width];
            for (int col = 0; col < height; col++) {
                for (int row = 0; row < width; row++) {
                    emptyBoard[col][row] = 0;
                }
            }
            SeedEditor editor = new SeedEditor();
            editor.open(emptyBoard);
        });
    }

    /**
     * Helper method for setUpGUI
     * adds all components to the frame
     */
    private void addComponentsToFrame() {
        menuPanel.add(playPauseButton);
        menuPanel.add(stepButton);
        menuPanel.add(resetButton);
        menuPanel.add(randomBoardButton);

        fileMenu.add(saveMenuItem);
        fileMenu.add(loadMenuItem);
        editMenu.add(editSeedMenuItem);
        editMenu.add(createNewSeedMenuItem);
        menu.add(fileMenu);
        menu.add(editMenu);

        frame.setLayout(new BorderLayout());
        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(cellsPanel, BorderLayout.CENTER);
        mainPanel.add(menuPanel, BorderLayout.SOUTH);
        frame.add(mainPanel, BorderLayout.CENTER);
        frame.add(menu, BorderLayout.NORTH);
        frame.pack();
    }

    private void nextGeneration() {
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

    private int[][] randomState() {
        int[][] randState = new int[height][width];
        for (int row = 0; row < width; row++) {
            for (int col = 0; col < height; col++) {
                randState[col][row] = (int) Math.round(Math.random());
            }
        }
        return randState;
    }
}
