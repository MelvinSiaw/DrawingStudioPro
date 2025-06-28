package com.ooadlabexercise.drawingstudio.model;

import javax.swing.*;
import java.awt.*;

public class DrawingStudioPro {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Drawing Studio Pro");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new BorderLayout());

            ReferenceCanvasPanel refCanvas = new ReferenceCanvasPanel();
            DrawingCanvasPanel drawCanvas = new DrawingCanvasPanel();
            StudioToolbar toolbar = new StudioToolbar(refCanvas, drawCanvas);

            frame.add(toolbar, BorderLayout.WEST);
            JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, refCanvas, drawCanvas);
            split.setDividerLocation(400);
            frame.add(split, BorderLayout.CENTER);

            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}