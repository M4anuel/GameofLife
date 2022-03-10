package com.gol.GameOfLife;

import java.util.concurrent.TimeUnit;

import static com.gol.GameOfLife.FxWindow.refreshMainTiles;

public class RunningThread extends Thread {

    GOLcore core;

    public RunningThread(GOLcore core) {
        this.core = core;
    }

    @Override
    public void run() {
        while (true) {
            if (core.isRunning()) {
                core.advanceGeneration();
                refreshMainTiles();
            }

            try {
                TimeUnit.MILLISECONDS.sleep(Math.max(core.getMinSimDelay(), core.getMaxSimSpeed() - core.getSimSpeed()));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void destroy() throws InterruptedException {
        this.join(1);
    }

}
