package com.ooadlabexercise.drawingstudio.view;

import java.awt.*;
import java.awt.event.MouseEvent;
import javax.swing.event.MouseInputAdapter;

/** Extends CanvasPanel with pencil, eraser, rectangle, and oval tools. */
public class DrawingPanel extends CanvasPanel {
    public enum Tool { PENCIL, ERASER, RECTANGLE, OVAL, SELECT }

    private Tool currentTool = Tool.PENCIL;
    private int startX, startY;
    private Color penColor = Color.BLACK;

    public DrawingPanel() {
        MouseInputAdapter mia = new MouseInputAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                startX = e.getX();
                startY = e.getY();
                applyTool(startX, startY);
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (currentTool == Tool.PENCIL || currentTool == Tool.ERASER) {
                    applyTool(e.getX(), e.getY());
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (currentTool == Tool.RECTANGLE || currentTool == Tool.OVAL) {
                    drawShape(startX, startY, e.getX(), e.getY());
                    repaint();
                }
            }
        };
        addMouseListener(mia);
        addMouseMotionListener(mia);
    }

    private void applyTool(int x, int y) {
        if (currentTool == Tool.PENCIL) {
            g2.setPaint(penColor);
            g2.drawLine(startX, startY, x, y);
            startX = x;
            startY = y;
        } else if (currentTool == Tool.ERASER) {
            g2.clearRect(x - 5, y - 5, 10, 10);
        }
        repaint();
    }

    private void drawShape(int x0, int y0, int x1, int y1) {
        int x = Math.min(x0, x1), y = Math.min(y0, y1);
        int w = Math.abs(x0 - x1), h = Math.abs(y0 - y1);
        if (currentTool == Tool.RECTANGLE)      g2.drawRect(x, y, w, h);
        else if (currentTool == Tool.OVAL)      g2.drawOval(x, y, w, h);
    }

    /** Change the pen color for pencil drawing. */
    public void setPenColor(Color c) {
        this.penColor = c;
    }

    /** Switch between PENCIL, ERASER, RECTANGLE, OVAL, or SELECT. */
    public void setCurrentTool(Tool t) {
        this.currentTool = t;
    }
}
