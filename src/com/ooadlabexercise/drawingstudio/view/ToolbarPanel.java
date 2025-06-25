package com.ooadlabexercise.drawingstudio.view;

import java.awt.event.ActionListener;
import javax.swing.*;

/** Top toolbar—text-only buttons wired into your DrawingFrame. */
public class ToolbarPanel extends JToolBar {
    public ToolbarPanel(DrawingFrame frame) {
        super(HORIZONTAL);
        setFloatable(false);

        addButton("New Left",  e -> { frame.selectLeftCanvas();  frame.clearCurrentCanvas(); });
        addButton("New Right", e -> { frame.selectRightCanvas(); frame.clearCurrentCanvas(); });
        addSeparator();

        addButton("Pen Color", e -> frame.choosePenColor());
        addButton("Stroke",    e -> frame.setStrokeWidth());
        addButton("Add Animal",e -> frame.addAnimal());
        addButton("Add Flower",e -> frame.addFlower());
        addSeparator();

        addButton("Save Draw",     e -> frame.saveDrawing());
        addButton("Add to Lib",    e -> frame.addToLibrary());
        addButton("Custom Canvas", e -> frame.createCustomCanvas());
        addButton("Compose",       e -> frame.composeCanvases());
        addButton("Import",        e -> frame.importImage());
    }

    private void addButton(String label, ActionListener al) {
        JButton b = new JButton(label);
        b.setFocusable(false);
        b.addActionListener(al);
        add(b);
    }
}
