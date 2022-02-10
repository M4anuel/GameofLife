package com.gol.GameOfLife;

import javafx.application.Application;

import java.awt.*;
import java.util.Arrays;

public class GOLcore {

    /**
     * This class introduces an independent framework engine of Conway's Game of Life. Input and output format for the
     * tiles are two-dimensional boolean arrays. It comes bundled with an optional thread which is started up upon
     * constructor call.
     *
     * Further functionality which might be added includes a recursive version of the nextGeneration() method such that
     * one can calculate several generations into the future, as well as an external state save construct presumably
     * using XStream to enable limited "backwards iteration" and preset features for example.
     */

    public Dimension size = new Dimension(20, 20); //Default grid size

    public boolean running = false;
    public int simSpeed = 1500; //Set default, minimum and maximum simulation speed in milliseconds
    public int minSimSpeed = 1000; //Minimum simulation speed shown in UI
    public int maxSimSpeed = 2000;
    public int minSimDelay = 10; //Actual maximum simulation speed (lower = quicker), values too low will cause crashes

    public boolean[][] state;

    public RunningThread thread;

    public GOLcore(boolean enableThread) {
        state = new boolean[size.height][size.width];
        if (enableThread) {
            thread = new RunningThread(this);
            thread.start();
        }
    }

    public boolean[][] nextGeneration(boolean[][] currentGen) {
        boolean[][] nextGen = new boolean[currentGen.length][currentGen[0].length];

        for (int i = 0; i < currentGen.length; i++) {
            for (int j = 0; j < currentGen[0].length; j++) {
                int nbrAlive;

                if (currentGen[i][j]) {
                    nbrAlive = -1;
                } else {
                    nbrAlive = 0;
                }

                for (int k = -1; k <= 1; k++) {
                    for (int l = -1; l <= 1; l++) {
                        try {
                            if (currentGen[i + k][j + l]) { //assert a horse cock to your ass jetbrains TODO find that horse cock
                                nbrAlive++;
                            }
                        } catch (Exception ignored) {}
                    }
                }

                switch (nbrAlive) {
                    case 2 -> nextGen[i][j] = currentGen[i][j];
                    case 3 -> nextGen[i][j] = true;
                    default -> nextGen[i][j] = false;
                }
            }
        }

        return nextGen;
    }

    public boolean[][] nextGeneration(boolean[][] currentGen, int gens) {
        for (int i = 0; i < gens; i++) {
            currentGen = nextGeneration(currentGen);
        }

        return currentGen;
    }

    //TODO add pattern center and align methods

}
