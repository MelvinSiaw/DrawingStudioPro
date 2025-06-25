package com.ooadlabexercise.drawingstudio.view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

/**
 * A base panel that holds a buffered image for free-form drawing
 * and supports dropping in external images.
 */
public class CanvasPanel extends JPanel {
    protected BufferedImage canvasImage;
    protected java.awt.Graphics2D g2;

    public CanvasPanel() {
        setBackground(Color.WHITE);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (canvasImage == null
         || canvasImage.getWidth()  != getWidth()
         || canvasImage.getHeight() != getHeight()) {
            initCanvas();
        }
        g.drawImage(canvasImage, 0, 0, null);
    }

    private void initCanvas() {
        canvasImage = new BufferedImage(getWidth(), getHeight(),
                                        BufferedImage.TYPE_INT_ARGB);
        g2 = canvasImage.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
        clearCanvas();
    }

    /** Erase everything back to white. */
    public void clearCanvas() {
        g2.setPaint(Color.WHITE);
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.setPaint(Color.BLACK);
        repaint();
    }

    /**
     * Load an external image file and draw it at (0,0).
     * @param type  arbitrary tag (unused in stub)
     * @param path  full filesystem path to image
     */
    public void addImage(String type, String path) {
        try {
            BufferedImage img = ImageIO.read(new File(path));
            g2.drawImage(img, 0, 0, this);
            repaint();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
