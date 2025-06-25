package com.ooadlabexercise.drawingstudio.view;

import java.awt.*;
import java.net.URL;
import javax.swing.*;
import javax.swing.border.TitledBorder;

/** The right-hand palette with canvas toggles and tool groups. */
public class SidebarPanel extends JPanel {
    public static final int SIDEBAR_WIDTH = 200;

    public SidebarPanel(DrawingFrame frame) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        setPreferredSize(new Dimension(SIDEBAR_WIDTH, Integer.MAX_VALUE));
        setMinimumSize(new Dimension(SIDEBAR_WIDTH, 0));

        // Canvas selector
        ButtonGroup g = new ButtonGroup();
        JToggleButton leftBtn  = new JToggleButton("Left");
        JToggleButton rightBtn = new JToggleButton("Right");
        leftBtn.setFocusable(false);
        rightBtn.setFocusable(false);
        leftBtn.addActionListener(e -> frame.selectLeftCanvas());
        rightBtn.addActionListener(e -> frame.selectRightCanvas());
        leftBtn.setSelected(true);
        g.add(leftBtn); g.add(rightBtn);
        add(createGroup("Canvas", leftBtn, rightBtn));
        add(Box.createVerticalStrut(10));

        // Image group
        add(createGroup("Image",
            makeButton("…"),
            makeColorPreview()
        ));
        add(Box.createVerticalStrut(10));

        // Tools group
        add(createGroup("Tools",
            makeToggle("pencil.png"),
            makeToggle("eraser.png"),
            makeToggle("brush.png"),
            makeToggle("line.png"),
            makeToggle("fill.png")
        ));
        add(Box.createVerticalStrut(10));

        // Brushes
        add(createGroup("Brushes",
            makeButton("…"),
            makeButton("…"),
            makeButton("…")
        ));
        add(Box.createVerticalStrut(10));

        // Shapes
        add(createGroup("Shapes",
            makeToggle("rect-outline.png"),
            makeToggle("rect-fill.png"),
            makeToggle("diag.png")
        ));
        add(Box.createVerticalStrut(10));

        // Colors
        add(createGroup("Colors",
            makeButton("…"),
            makeButton("…")
        ));
        add(Box.createVerticalGlue());
    }

    private JButton makeButton(String text) {
        JButton b = new JButton(text);
        b.setFocusable(false);
        b.setPreferredSize(new Dimension(30,30));
        return b;
    }

    private JToggleButton makeToggle(String iconName) {
        JToggleButton t = new JToggleButton();
        t.setFocusable(false);
        t.setPreferredSize(new Dimension(30,30));
        URL url = getClass().getClassLoader().getResource("images/icons/" + iconName);
        if (url != null) t.setIcon(new ImageIcon(url));
        return t;
    }

    private JButton makeColorPreview() {
        JButton b = new JButton();
        b.setFocusable(false);
        b.setPreferredSize(new Dimension(30,30));
        b.setBackground(Color.WHITE);
        b.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        return b;
    }

    private JPanel createGroup(String title, JComponent... comps) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT,5,5));
        p.setBackground(Color.WHITE);
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.setMaximumSize(new Dimension(SIDEBAR_WIDTH, Integer.MAX_VALUE));
        p.setBorder(BorderFactory.createTitledBorder(
          BorderFactory.createLineBorder(Color.GRAY),
          title, TitledBorder.LEFT, TitledBorder.TOP
        ));
        for (JComponent c : comps) p.add(c);
        return p;
    }
}
