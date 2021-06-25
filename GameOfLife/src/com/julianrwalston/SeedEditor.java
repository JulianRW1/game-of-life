package com.julianrwalston;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.*;

/**
 * Created by Julian Walston on 6/16/2021
 */

/**
 * This class provides a gui that the user can use to create and edit seeds
 */
public class SeedEditor {
    private static String DEFAULT_DIRECTORY =
            "C:\\Users\\Julian Walston\\Documents\\IdeaProjects\\game-of-life\\GameOfLife\\savedGames";

    private int[][] boardState;
    int width;
    int height;

    JFrame frame;
    JPanel mainPanel;
    JCheckBox[][] cells;

    JMenuBar menu;
    JMenu fileMenu;
    JMenuItem saveMenuItem;
    JMenuItem loadMenuItem;

    SeedEditor() {
        frame = new JFrame("Seed Editor");
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());
    }

    public void open(int[][] state) {
        // Initialize values
        boardState = state;
        width = boardState[0].length;
        height = boardState.length;

        initGUI();

        setUpSeed();

        createActionListeners();

        addComponentsToFrame();
    }

    private void initGUI() {
        // Initialize GUI components
        cells = new JCheckBox[height][width];
        menu = new JMenuBar();
        fileMenu = new JMenu("File");
        saveMenuItem = new JMenuItem("Save Seed");
        loadMenuItem = new JMenuItem("Load Seed");
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(width, height));
    }

    private void setUpSeed() {
        // Create checkboxes
        for (int col = 0; col < height; col++) {
            for (int row = 0; row < width; row++) {
                JCheckBox currentCell = new JCheckBox();
                if (boardState[col][row] == 1) {
                    currentCell.setSelected(true);
                } else {
                    currentCell.setSelected(false);
                }
                cells[col][row] = currentCell;
                mainPanel.add(currentCell);
            }
        }
    }

    private void createActionListeners() {

        saveMenuItem.addActionListener(e -> {
            try {
                int[][] newState = new int[height][width];

                for (int col = 0; col < height; col++) {
                    for (int row = 0; row < width; row++) {
                        newState[col][row] = cells[col][row].isSelected() ?  1 : 0;
                    }
                }

                JFileChooser fc = new JFileChooser(defaultDirectory);
                fc.showSaveDialog(frame);

                if (fc.getSelectedFile() != null) {
                    //Saving of object in a file
                    FileOutputStream file = new FileOutputStream(fc.getSelectedFile().getAbsolutePath() + ".gol");
                    ObjectOutputStream os = new ObjectOutputStream(file);

                    // Method for serialization of object
                    os.writeObject(newState);

                    os.close();
                    file.close();
                }
            }
            catch(IOException ex) {ex.printStackTrace();}
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
    }

    private void addComponentsToFrame() {
        fileMenu.add(saveMenuItem);
        fileMenu.add(loadMenuItem);
        menu.add(fileMenu);

        frame.add(mainPanel, BorderLayout.CENTER);
        frame.add(menu, BorderLayout.NORTH);
        frame.pack();
        frame.setVisible(true);
    }
}
