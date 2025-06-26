package com.ooadlabexercise.drawingstudio.model;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class StudioToolbar extends JToolBar {
    private final ReferenceCanvasPanel refCanvas;
    private final DrawingCanvasPanel drawCanvas;
    private Color lastPenColor = Color.BLACK;
    private Color lastFillColor = Color.WHITE;

    public StudioToolbar(ReferenceCanvasPanel refCanvas, DrawingCanvasPanel drawCanvas) {
        this.refCanvas = refCanvas;
        this.drawCanvas = drawCanvas;

        setOrientation(VERTICAL);
        setFloatable(false);
        setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // Undo/Redo
        addButton("Undo",  e -> drawCanvas.undo());
        addButton("Redo",  e -> drawCanvas.redo());
        addSpacing();

        // Reference controls
        addButton("Open Reference...", e -> loadReference());
        addButton("Clear Reference",    e -> refCanvas.clearImage());
        addButton("Add Animal Ref",     e -> refCanvas.setImage(loadImage("animal.jpg")));
        addButton("Add Flower Ref",     e -> refCanvas.setImage(loadImage("flower.jpg")));
        addButton("Reset Ref View",     e -> refCanvas.resetView());
        addSpacing();

        // Rotation slider (0–360°)
        JLabel rotLabel = new JLabel("Rotate Ref:");
        rotLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(rotLabel);
        JSlider rotSlider = new JSlider(0, 360, 0);
        rotSlider.setMaximumSize(new Dimension(140, 40));
        rotSlider.setAlignmentX(Component.CENTER_ALIGNMENT);
        rotSlider.addChangeListener(e -> refCanvas.setRotation(rotSlider.getValue()));
        add(rotSlider);
        addSpacing();

        // Drawing controls
        addButton("New Drawing", e -> drawCanvas.clearCanvas());
        addSpacing();
        addDrawingToolButtons();
        addSpacing();
        addButton("Pen Color", e -> choosePenColor());
        addSpacing();
        addPenSizeSlider();
        addSpacing();
        addButton("Bucket Fill", e -> chooseFill());
        addSpacing();
        addButton("Save Drawing", e -> saveDrawing());
        addGlue();
    }

    private BufferedImage loadImage(String path) {
        try {
            return ImageIO.read(new File(path));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Failed to load " + path);
            return null;
        }
    }

    private void loadReference() {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                refCanvas.setImage(ImageIO.read(chooser.getSelectedFile()));
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Failed to load reference image");
            }
        }
    }

    private void setToolCursor(DrawingCanvasPanel.Tool tool, int cursorType) {
        drawCanvas.setTool(tool);
        drawCanvas.setCursor(Cursor.getPredefinedCursor(cursorType));
    }

    private void choosePenColor() {
        Color chosen = JColorChooser.showDialog(this, "Pen Color", lastPenColor);
        if (chosen != null) {
            lastPenColor = chosen;
            drawCanvas.setPenColor(chosen);
        }
    }

    private void addPenSizeSlider() {
        JLabel lbl = new JLabel("Pen Size");
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        add(lbl);
        JSlider slider = new JSlider(1, 50, 2);
        slider.setMaximumSize(new Dimension(120, 40));
        slider.setAlignmentX(Component.CENTER_ALIGNMENT);
        slider.addChangeListener((ChangeListener) e -> drawCanvas.setPenSize(slider.getValue()));
        add(slider);
    }

    private void chooseFill() {
        Color chosen = JColorChooser.showDialog(this, "Fill Color", lastFillColor);
        if (chosen != null) {
            lastFillColor = chosen;
            drawCanvas.setFillColor(chosen);
            drawCanvas.setTool(DrawingCanvasPanel.Tool.FILL);
            drawCanvas.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }
    }

    private void addDrawingToolButtons() {
        addButton("Freehand",  e -> setToolCursor(DrawingCanvasPanel.Tool.FREEHAND, Cursor.DEFAULT_CURSOR));
        addButton("Line",      e -> setToolCursor(DrawingCanvasPanel.Tool.LINE, Cursor.CROSSHAIR_CURSOR));
        addButton("Rectangle", e -> setToolCursor(DrawingCanvasPanel.Tool.RECTANGLE, Cursor.CROSSHAIR_CURSOR));
        addButton("Oval",      e -> setToolCursor(DrawingCanvasPanel.Tool.OVAL, Cursor.CROSSHAIR_CURSOR));
    }

    private void addButton(String title, ActionListener listener) {
        JButton b = new JButton(title);
        b.setAlignmentX(Component.CENTER_ALIGNMENT);
        b.setMaximumSize(new Dimension(140, 30));
        b.addActionListener(listener);
        add(b);
    }

    private void addSpacing() {
        add(Box.createRigidArea(new Dimension(0, 5)));
    }

    private void addGlue() {
        add(Box.createVerticalGlue());
    }

    private void saveDrawing() {
        try {
            BufferedImage raw = drawCanvas.getCanvasImage();
            int w = raw.getWidth(), h = raw.getHeight();
            BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = out.createGraphics();
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, w, h);
            g.drawImage(raw, 0, 0, null);
            g.dispose();
            ImageIO.write(out, "PNG", new File("drawing.png"));
            JOptionPane.showMessageDialog(this, "Drawing saved to drawing.png");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Save failed");
        }
    }
}