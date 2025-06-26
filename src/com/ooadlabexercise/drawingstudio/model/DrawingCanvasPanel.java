package com.ooadlabexercise.drawingstudio.model;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;
import javax.swing.*;

public class DrawingCanvasPanel extends JPanel {
    public enum Tool { FREEHAND, LINE, RECTANGLE, OVAL, FILL }

    private Tool currentTool = Tool.FREEHAND;
    private final BufferedImage canvas;
    private Graphics2D g2;

    private int startX, startY, currX, currY;
    private boolean previewing = false;
    private Color fillColor = Color.WHITE;

    // Undo/Redo stacks
    private final Stack<BufferedImage> undoStack = new Stack<>();
    private final Stack<BufferedImage> redoStack = new Stack<>();

    public DrawingCanvasPanel() {
        setPreferredSize(new Dimension(800, 600));
        setBackground(Color.WHITE);
        canvas = new BufferedImage(800, 600, BufferedImage.TYPE_INT_ARGB);
        initGraphics();
        saveState();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                startX = e.getX();
                startY = e.getY();
                currX = startX;
                currY = startY;

                if (currentTool == Tool.FREEHAND) {
                    saveState();
                } else if (currentTool == Tool.FILL) {
                    saveState();
                    floodFill(startX, startY, fillColor);
                    repaint();
                } else {
                    previewing = true;
                    saveState();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (previewing) {
                    drawShape(g2, startX, startY, currX, currY);
                    previewing = false;
                    repaint();
                }
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (currentTool == Tool.FREEHAND) {
                    int x = e.getX(), y = e.getY();
                    g2.drawLine(startX, startY, x, y);
                    startX = x;
                    startY = y;
                    repaint();
                } else if (previewing) {
                    currX = e.getX();
                    currY = e.getY();
                    repaint();
                }
            }
        });
    }

    private void initGraphics() {
        g2 = canvas.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.setColor(Color.BLACK);
    }

    private void drawShape(Graphics2D g, int x1, int y1, int x2, int y2) {
        switch (currentTool) {
            case LINE:
                g.drawLine(x1, y1, x2, y2);
                break;
            case RECTANGLE:
                g.drawRect(Math.min(x1, x2), Math.min(y1, y2),
                           Math.abs(x2 - x1), Math.abs(y2 - y1));
                break;
            case OVAL:
                g.drawOval(Math.min(x1, x2), Math.min(y1, y2),
                           Math.abs(x2 - x1), Math.abs(y2 - y1));
                break;
            default:
                break;
        }
    }

    private void saveState() {
        BufferedImage copy = new BufferedImage(
            canvas.getWidth(), canvas.getHeight(),
            canvas.getType()
        );
        copy.getGraphics().drawImage(canvas, 0, 0, null);
        undoStack.push(copy);
        redoStack.clear();
    }

    public void undo() {
        if (undoStack.size() > 1) {
            redoStack.push(undoStack.pop());
            BufferedImage prev = undoStack.peek();
            g2.drawImage(prev, 0, 0, null);
            repaint();
        }
    }

    public void redo() {
        if (!redoStack.isEmpty()) {
            BufferedImage next = redoStack.pop();
            undoStack.push(next);
            g2.drawImage(next, 0, 0, null);
            repaint();
        }
    }

    private void floodFill(int x, int y, Color newColor) {
        int width = canvas.getWidth(), height = canvas.getHeight();
        int target = canvas.getRGB(x, y), replacement = newColor.getRGB();
        if (target == replacement) return;

        Queue<Point> queue = new LinkedList<>();
        queue.add(new Point(x, y));
        while (!queue.isEmpty()) {
            Point p = queue.remove();
            int px = p.x, py = p.y;
            if (px < 0 || px >= width || py < 0 || py >= height) continue;
            if (canvas.getRGB(px, py) != target) continue;
            canvas.setRGB(px, py, replacement);
            queue.add(new Point(px + 1, py));
            queue.add(new Point(px - 1, py));
            queue.add(new Point(px, py + 1));
            queue.add(new Point(px, py - 1));
        }
    }

    public void clearCanvas() {
        saveState();
        g2.setComposite(AlphaComposite.Clear);
        g2.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        g2.setComposite(AlphaComposite.SrcOver);
        repaint();
    }

    public void setPenColor(Color color) {
        g2.setColor(color);
    }

    public void setPenSize(float size) {
        g2.setStroke(new BasicStroke(size, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
    }

    public void setTool(Tool tool) {
        currentTool = tool;
    }

    public void setFillColor(Color color) {
        fillColor = color;
    }

    public BufferedImage getCanvasImage() {
        return canvas;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(canvas, 0, 0, null);
        if (previewing) {
            Graphics2D gTemp = (Graphics2D) g.create();
            gTemp.setColor(g2.getColor());
            gTemp.setStroke(new BasicStroke(
                2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                1.0f, new float[]{4.0f}, 0.0f
            ));
            drawShape(gTemp, startX, startY, currX, currY);
            gTemp.dispose();
        }
    }
}
