package com.analysis;
import java.util.Arrays;

public class TerminalBuffer {
    // the buffer
    private final Cell[][] buffer;

    // shell configuration
    private int width;
    private int height;
    private int totalCapacity;
    private int maxScrollback;

    private int startPtr = 0;
    private int totalLines = 0;

    private int cursorCol = 0;
    private int cursorRow = 0;

    private int currentFg = 0; // 0 = Default
    private int currentBg = 0;
    private boolean bold, italic, underline;

    public TerminalBuffer(int width, int height, int maxScrollback) {
        this.width = width;
        this.height = height;
        this.maxScrollback = maxScrollback;
        this.totalCapacity = height + maxScrollback;
        this.buffer = new Cell[totalCapacity][width];

        for (int r = 0; r < totalCapacity; r++) {
            for (int c = 0; c < width; c++) {
                buffer[r][c] = new Cell();
            }
        }

        this.totalLines = height;
    }

    public void drawToSystemConsole() {
        System.out.print("\033[H");

        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                Cell cell = getCellAtScreenPos(c, r);

                if (cell.bold) System.out.print("\033[1m");

                System.out.print(cell.character);

                System.out.print("\033[0m");
            }
            System.out.println();
        }
    }
}
