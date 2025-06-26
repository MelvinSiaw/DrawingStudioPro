package com.ooadlabexercise.drawingstudio.model;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.*;

public class ReferenceCanvasPanel extends JPanel {
    private BufferedImage refImage;
    private double scale = 1.0;
    private int translateX = 0, translateY = 0;
    private double rotation = 0.0; // degrees
    private boolean flipH = false, flipV = false;
    private Point dragStart;

    public ReferenceCanvasPanel() {
        setPreferredSize(new Dimension(400, 600));
        setBackground(Color.LIGHT_GRAY);
        setTransferHandler(new TransferHandler() {
            @Override public boolean canImport(TransferSupport support) {
                return support.isDataFlavorSupported(DataFlavor.javaFileListFlavor);
            }
            @Override
            @SuppressWarnings("unchecked")
            public boolean importData(TransferSupport support) {
                if (!canImport(support)) return false;
                try {
                    List<File> files = (List<File>) support.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                    if (!files.isEmpty()) {
                        BufferedImage img = ImageIO.read(files.get(0));
                        if (img != null) setImage(img);
                        return true;
                    }
                } catch (Exception e) { e.printStackTrace(); }
                return false;
            }
        });

        addMouseListener(new MouseAdapter() {
    @Override public void mousePressed(MouseEvent e) {
        dragStart = e.getPoint();
    }
    @Override public void mouseClicked(MouseEvent e) {
        if (refImage == null) return;
        // Left-click: vertical flip
        if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 1) {
            flipV = !flipV;
            repaint();
        }
        // Right-click: horizontal flip
        else if (SwingUtilities.isRightMouseButton(e) && e.getClickCount() == 1) {
            flipH = !flipH;
            repaint();
        }
    }
});

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override public void mouseDragged(MouseEvent e) {
                if (refImage != null) {
                    Point p = e.getPoint();
                    translateX += p.x - dragStart.x;
                    translateY += p.y - dragStart.y;
                    dragStart = p;
                    repaint();
                }
            }
        });

        addMouseWheelListener(e -> {
            if (refImage != null) {
                double delta = e.getWheelRotation() < 0 ? 1.1 : 0.9;
                scale *= delta;
                repaint();
            }
        });
    }

    public void setImage(BufferedImage img) {
        this.refImage = img;
        scale = 1.0; translateX = 0; translateY = 0; rotation = 0.0; flipH = false; flipV = false;
        repaint();
    }

    public void clearImage() {
        refImage = null;
        repaint();
    }

    public void resetView() {
        scale = 1.0; translateX = 0; translateY = 0; rotation = 0.0; flipH = false; flipV = false;
        repaint();
    }

    public void setRotation(double degrees) {
        rotation = degrees;
        repaint();
    }

    @Override protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (refImage != null) {
            Graphics2D g2 = (Graphics2D) g.create();
            int cx = getWidth()/2, cy = getHeight()/2;
            g2.translate(cx + translateX, cy + translateY);
            g2.scale(scale, scale);
            g2.rotate(Math.toRadians(rotation));
            g2.scale(flipH ? -1 : 1, flipV ? -1 : 1);
            g2.translate(-refImage.getWidth()/2, -refImage.getHeight()/2);
            g2.drawImage(refImage, 0, 0, this);
            g2.dispose();
        }
    }
}