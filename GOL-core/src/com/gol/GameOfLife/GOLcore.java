package com.gol.GameOfLife;

import java.awt.*;

/**
 * Standalone API for the Game of Life with several utility methods and an optional thread.
 * <p>
 * This class introduces an independent framework engine of Conway's Game of Life. Input and output format for the
 * tiles are two-dimensional boolean arrays. It comes bundled with an optional thread which is started up upon
 * constructor call.
 * <p>
 * Further functionality which might be added includes a recursive version of the {@code nextGeneration()} method such
 * that one can iterate several generations into the future, but imagine actually coding recursive functionality, not
 * only is it inefficient in most cases, but it is also cringe, and this was therefore resolved via an iterative
 * approach. An external state save construct presumably using XStream may be introduced in order to enable limited
 * "backwards iteration" and preset features.
 *
 * @author Etienne Bratschi etienne.bratschi@gymburgdorf.ch
 * @author Manuel Flückiger manuel.flueckiger@gymburgdorf.ch
 * @version 1.8
 * @since 1.1
 */

public class GOLcore {

    protected RunningThread thread;

    /**
     * @deprecated Size of the grid, can be inferred from state
     */
    protected Dimension size = new Dimension(20, 20); //Default grid size

    private boolean running = false;

    /**
     * Simulation speed in milliseconds
     * @see com.gol.GameOfLife.GOLcore#minSimSpeed
     * @see com.gol.GameOfLife.GOLcore#maxSimSpeed
     * @see com.gol.GameOfLife.GOLcore#minSimDelay
     */
    private int simSpeed = 1500; //Default sim speed
    private int minSimSpeed = 1000; //Minimum simulation speed shown in UI
    private int maxSimSpeed = 2000;
    private int minSimDelay = 10; //Actual maximum simulation speed (lower = quicker), values too low will cause crashes

    private boolean[][] state;

    FxWindow window;

    /**
     * Constructs a {@code GOLcore} object with default parameters as specified in GOLcore.
     */
    GOLcore() {
        state = new boolean[20][20];
    }

    /**
     * Constructs a {@code GOLcore} object with default parameters and
     * @param enableThread whether a thread should be initiated
     */
    GOLcore(FxWindow window, boolean enableThread) {
        this.window = window;
        state = new boolean[20][20];
        if (enableThread) {
            thread = new RunningThread(this);
            thread.start();
        }
    }

    public int getWidth() {
        return state[0].length;
    }

    public int getHeight() {
        return state.length;
    }

    public Dimension getSize() {
        return new Dimension(getWidth(), getHeight());
    }

    public boolean[][] getState() {
        return state;
    }

    public void setState(boolean[][] state) {
        this.state = state;
    }

    /**
     * @return simulation speed
     */
    public int getSimSpeed() {
        return simSpeed;
    }

    public void setSimSpeed(int simSpeed) {
        this.simSpeed = simSpeed;
    }

    public int getMinSimSpeed() {
        return minSimSpeed;
    }

    public void setMinSimSpeed(int minSimSpeed) {
        this.minSimSpeed = minSimSpeed;
    }

    public int getMaxSimSpeed() {
        return maxSimSpeed;
    }

    public void setMaxSimSpeed(int maxSimSpeed) {
        this.maxSimSpeed = maxSimSpeed;
    }

    public int getMinSimDelay() {
        return minSimDelay;
    }

    public boolean setMinSimDelay(int minSimDelay) {
        if (minSimDelay >= 5) {
            this.minSimDelay = minSimDelay;
            return true;
        }
        return false;
    }

    public boolean isRunning() {
        return this.running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    /**
     * Overrides the current {@code state} with an empty array of the specified width and height.
     * @param width  width of the overriding array
     * @param height height of the overriding array
     */
    public void generateEmptyState(int width, int height) {
        this.state = new boolean[width][height];
    }

    /**
     * Overrides the current {@code state} with an empty array of the specified width and height.
     * @param size size of the overriding array
     */
    public void generateEmptyState(Dimension size) {
        this.state = new boolean[size.width][size.height];
    }

    /**
     * Advances the input population by one generation.
     * @param currentGen the current population
     * @return           the population after one iteration
     */
    boolean[][] nextGeneration(boolean[][] currentGen) {
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

    /**
     * Advances the population by {@code gens} many iterations.
     * @param currentGen the current population
     * @param gens       number of generations to advance
     * @return           the population after {@code gens} many iterations
     */
    boolean[][] nextGeneration(boolean[][] currentGen, int gens) {
        for (int i = 0; i < gens; i++) {
            currentGen = nextGeneration(currentGen);
        }

        return currentGen;
    }

    /**
     * Advances the state by one generation via a neat internal cycle.
     */
    void advanceGeneration() {
        this.state = nextGeneration(this.state);
    }

    /**
     * Advances the state by {@code gens} generations via a neat internal cycle.
     * @param gens number of generations to advance
     */
    void advanceGeneration(int gens) {
        this.state = nextGeneration(this.state, gens);
    }

    //TODO add pattern center and align methods

}
