package com.ooadlabexercise.drawingstudio.model;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.Stack;
import javax.swing.*;

public class DrawingCanvasPanel extends JPanel {
    public enum Tool { FREEHAND, LINE, RECTANGLE, OVAL, FILL }
    private Tool currentTool = Tool.FREEHAND;

    // Canvas buffer will now resize dynamically
    private BufferedImage canvas;
    private Graphics2D g2;

    private int startX, startY, currX, currY;
    private boolean previewing = false;
    private Color fillColor = Color.WHITE;

    private final Stack<BufferedImage> undoStack = new Stack<>();
    private final Stack<BufferedImage> redoStack = new Stack<>();

    public DrawingCanvasPanel() {
        setBackground(Color.WHITE);
        // initial tiny buffer
        canvas = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        setupGraphics();

        // Handle resizing: expand buffer to panel size
        addComponentListener(new ComponentAdapter() {
            @Override public void componentResized(ComponentEvent e) {
                int w = getWidth(), h = getHeight();
                if (w <= 0 || h <= 0) return;
                BufferedImage newCanvas = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = newCanvas.createGraphics();
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g.setColor(Color.BLACK);
                // copy old content
                g.drawImage(canvas, 0, 0, null);
                g.dispose();
                canvas = newCanvas;
                setupGraphics();
                // reset undo/redo
                undoStack.clear();
                undoStack.push(copyImage(canvas));
                redoStack.clear();
                repaint();
            }
        });

        // Mouse listeners
        addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) {
                startX = e.getX(); startY = e.getY(); currX = startX; currY = startY;
                if (currentTool == Tool.FILL) {
                    floodFill(startX, startY, fillColor);
                    saveState(); repaint();
                } else if (currentTool != Tool.FREEHAND) {
                    previewing = true;
                }
                // FREEHAND: commit on release
            }
            @Override public void mouseReleased(MouseEvent e) {
                if (previewing) {
                    drawShape(g2, startX, startY, currX, currY);
                    previewing = false; saveState(); repaint();
                } else if (currentTool == Tool.FREEHAND) {
                    saveState();
                }
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override public void mouseDragged(MouseEvent e) {
                if (currentTool == Tool.FREEHAND) {
                    int x = e.getX(), y = e.getY();
                    g2.drawLine(startX, startY, x, y);
                    startX = x; startY = y; repaint();
                } else if (previewing) {
                    currX = e.getX(); currY = e.getY(); repaint();
                }
            }
        });
    }

    private void setupGraphics() {
        g2 = canvas.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.setColor(Color.BLACK);
    }

    private void drawShape(Graphics2D g, int x1, int y1, int x2, int y2) {
        switch (currentTool) {
            case LINE:      g.drawLine(x1,y1,x2,y2); break;
            case RECTANGLE: g.drawRect(Math.min(x1,x2),Math.min(y1,y2),Math.abs(x2-x1),Math.abs(y2-y1)); break;
            case OVAL:      g.drawOval(Math.min(x1,x2),Math.min(y1,y2),Math.abs(x2-x1),Math.abs(y2-y1)); break;
            default: break;
        }
    }

    private void saveState() {
        undoStack.push(copyImage(canvas));
        redoStack.clear();
    }

    private BufferedImage copyImage(BufferedImage src) {
        BufferedImage copy = new BufferedImage(src.getWidth(), src.getHeight(), src.getType());
        copy.getGraphics().drawImage(src,0,0,null);
        return copy;
    }

    public void undo() {
        if (undoStack.size()>1) {
            redoStack.push(undoStack.pop());
            restoreImage(undoStack.peek());
        }
    }

    public void redo() {
        if (!redoStack.isEmpty()) {
            BufferedImage next = redoStack.pop();
            undoStack.push(copyImage(next));
            restoreImage(next);
        }
    }

    private void restoreImage(BufferedImage img) {
        g2.setComposite(AlphaComposite.Clear);
        g2.fillRect(0,0,canvas.getWidth(),canvas.getHeight());
        g2.setComposite(AlphaComposite.SrcOver);
        g2.drawImage(img,0,0,null);
        repaint();
    }

    private void floodFill(int x,int y,Color c) {
        int w = canvas.getWidth(), h = canvas.getHeight();
        int target;
        try { target = canvas.getRGB(x,y); } catch (Exception ex) { return; }
        int replacement = c.getRGB(); if (target==replacement) return;
        java.util.Queue<Point> q = new java.util.LinkedList<>();
        q.add(new Point(x,y));
        while (!q.isEmpty()) {
            Point p = q.remove(); int px = p.x, py = p.y;
            if (px<0||px>=w||py<0||py>=h) continue;
            if (canvas.getRGB(px,py)!=target) continue;
            canvas.setRGB(px, py, replacement);
            q.add(new Point(px+1,py)); q.add(new Point(px-1,py));
            q.add(new Point(px,py+1)); q.add(new Point(px,py-1));
        }
    }

    public void clearCanvas() {
        g2.setComposite(AlphaComposite.Clear);
        g2.fillRect(0,0,canvas.getWidth(),canvas.getHeight());
        g2.setComposite(AlphaComposite.SrcOver);
        saveState(); repaint();
    }

    /** Set drawing tool (FREEHAND, LINE, etc.) */
    public void setTool(Tool tool) { currentTool = tool; }
    /** Change pen color for drawing */
    public void setPenColor(Color color) { g2.setColor(color); }
    /** Change pen thickness */
    public void setPenSize(float size) { g2.setStroke(new BasicStroke(size, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)); }
    /** Change fill bucket color */
    public void setFillColor(Color color) { fillColor = color; }

    /** Expose canvas image for external use */
    public BufferedImage getCanvasImage() { return canvas; }

    @Override protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(canvas,0,0,null);
        if (previewing) {
            Graphics2D tmp = (Graphics2D)g.create();
            tmp.setColor(g2.getColor());
            tmp.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND,1.0f,new float[]{4},0));
            drawShape(tmp,startX,startY,currX,currY);
            tmp.dispose();
        }
    }
}