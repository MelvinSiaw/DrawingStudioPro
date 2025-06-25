package com.ooadlabexercise.drawingstudio.view;

import java.awt.*;
import javax.swing.*;

/**
 * Main window: toolbar at top, split-pane with two canvases on left,
 * sidebar on right.
 */
public class DrawingFrame extends JFrame {
    private static final String LEFT  = "LEFT";
    private static final String RIGHT = "RIGHT";
    private String current = LEFT;

    private final DrawingPanel leftCanvas;
    private final DrawingPanel rightCanvas;
    private final JPanel centerPanel;
    private final CardLayout cardLayout;

    public DrawingFrame() {
        super("Drawing Studio Pro");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // 1) Top toolbar
        add(new ToolbarPanel(this), BorderLayout.NORTH);

        // 2) Center: two canvases in a CardLayout
        cardLayout  = new CardLayout();
        centerPanel = new JPanel(cardLayout);
        leftCanvas  = new DrawingPanel();
        rightCanvas = new DrawingPanel();
        centerPanel.add(leftCanvas,  LEFT);
        centerPanel.add(rightCanvas, RIGHT);
        cardLayout.show(centerPanel, LEFT);

        // 3) Sidebar
        SidebarPanel sidebar = new SidebarPanel(this);
        // wrap in scrollpane
        JScrollPane sideScroll = new JScrollPane(
          sidebar,
          JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
          JScrollPane.HORIZONTAL_SCROLLBAR_NEVER
        );
        sideScroll.setPreferredSize(new Dimension(SidebarPanel.SIDEBAR_WIDTH, 0));

        // 4) Split: canvas (left) | sidebar (right)
        JSplitPane split = new JSplitPane(
          JSplitPane.HORIZONTAL_SPLIT,
          centerPanel,
          sideScroll
        );
        split.setResizeWeight(1.0);
        split.setDividerLocation(900);
        add(split, BorderLayout.CENTER);

        setSize(1200, 800);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void selectLeftCanvas()  { cardLayout.show(centerPanel, LEFT);  current = LEFT; }
    public void selectRightCanvas() { cardLayout.show(centerPanel, RIGHT); current = RIGHT; }
    public void clearCurrentCanvas() {
        if (LEFT.equals(current)) leftCanvas.clearCanvas();
        else                     rightCanvas.clearCanvas();
    }

    // stubs for toolbar actions—you can implement these as needed:
    public void choosePenColor()    { /* ... */ }
    public void setStrokeWidth()    { /* ... */ }
    public void addAnimal()         { /* ... */ }
    public void addFlower()         { /* ... */ }
    public void saveDrawing()       { /* ... */ }
    public void addToLibrary()      { /* ... */ }
    public void createCustomCanvas(){ /* ... */ }
    public void composeCanvases()   { /* ... */ }
    public void importImage()       { /* ... */ }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(DrawingFrame::new);
    }
}
