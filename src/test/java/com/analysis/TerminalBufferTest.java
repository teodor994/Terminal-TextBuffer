package com.analysis;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Terminal Buffer Comprehensive Suite")
class TerminalBufferTest {
    private TerminalBuffer buffer;
    private final int W = 80;
    private final int H = 10;
    private final int S = 10;

    @BeforeEach
    void setUp() {
        buffer = new TerminalBuffer(W, H, S);
    }

    @Test
    @DisplayName("Cursor should not move outside screen bounds")
    void testCursorBounds() {
        buffer.setCursor(-10, -10);
        assertEquals(0, buffer.getCursorCol());
        assertEquals(0, buffer.getCursorRow());

        buffer.setCursor(W + 100, H + 100);
        assertEquals(W - 1, buffer.getCursorCol());
        assertEquals(H - 1, buffer.getCursorRow());
    }

    @Test
    @DisplayName("MoveCursor should apply relative movement correctly")
    void testMoveCursor() {
        buffer.setCursor(5, 5);
        buffer.moveCursor(2, -1);
        assertEquals(7, buffer.getCursorCol());
        assertEquals(4, buffer.getCursorRow());
    }

    @Test
    @DisplayName("WriteText should handle newline character correctly")
    void testNewlineCharacter() {
        buffer.writeText("Line1\nLine2");
        assertEquals(1, buffer.getCursorRow());
        assertEquals(5, buffer.getCursorCol());
        assertEquals('L', buffer.getCell(0, 0, false).character);
        assertEquals('L', buffer.getCell(0, 1, false).character);
    }

    @Test
    @DisplayName("Text should wrap and scroll when screen height is exceeded")
    void testAutoScroll() {
        for (int i = 0; i < 11; i++) {
            buffer.writeText("L" + i + "\n");
        }
        assertEquals(11, buffer.getCursorRow());
        assertTrue(buffer.getLineContent(0).contains("L0"));
    }

    @Test
    @DisplayName("FillCurrentLine should populate entire row with character")
    void testFillLine() {
        buffer.setCursor(0, 2);
        buffer.setAttributes(31, 0, true, false, false);
        buffer.fillCurrentLine('#');

        for (int c = 0; c < W; c++) {
            Cell cell = buffer.getCell(c, 2, false);
            assertEquals('#', cell.character);
            assertEquals(31, cell.fgColor);
            assertTrue(cell.bold);
        }
    }

    @Test
    @DisplayName("InsertText should shift existing characters to the right")
    void testInsertMode() {
        buffer.writeText("HelloWorld");
        buffer.setCursor(5, 0);
        buffer.insertText(" ");

        String line = buffer.getLineContent(0).trim();
        assertEquals("Hello World", line);
    }

    @Test
    @DisplayName("Backspace should skip trailing spaces when wrapping up")
    void testSmartBackspaceNavigation() {
        buffer.writeText("Text");
        buffer.writeText("\n");
        buffer.writeText("Y");

        buffer.backspace();
        buffer.backspace();

        assertEquals(0, buffer.getCursorRow());
        assertEquals(4, buffer.getCursorCol());
    }

    @Test
    @DisplayName("Circular buffer should overwrite oldest lines when capacity is exceeded")
    void testMemoryRecycling() {
        for (int i = 0; i < 22; i++) {
            buffer.writeText("Line" + i + "\n");
        }

        String firstLine = buffer.getLineContent(0).trim();
        assertEquals("Line2", firstLine);
    }

    @Test
    @DisplayName("ClearAll should reset everything including scrollback")
    void testClearAll() {
        buffer.writeText("Some Data\nMore Data");
        buffer.clearAll();

        assertEquals(0, buffer.getCursorCol());
        assertEquals(0, buffer.getCursorRow());
        assertEquals(' ', buffer.getCell(0, 0, false).character);
    }

    @Test
    @DisplayName("GetScreenAsString should return only visible lines")
    void testScreenExport() {
        for (int i = 0; i < 15; i++) buffer.writeText(i + "\n");
        String screen = buffer.getScreenAsString();
        String[] lines = screen.split("\n");
        assertEquals(H, lines.length);
        assertTrue(lines[lines.length - 1].contains("14"));
    }
}