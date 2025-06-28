package com.ooadlabexercise.drawingstudio.model;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

public class ReferenceCanvasPanel extends JPanel {
    private class RefImage {
        BufferedImage img;
        double x, y;
        double scale = 1.0;
        double rotation = 0.0;
        boolean flipH = false;
        boolean flipV = false;

        RefImage(BufferedImage img) {
            this.img = img;
            this.x = getWidth()/2.0;
            this.y = getHeight()/2.0;
        }

        boolean contains(Point2D p) {
            try {
                AffineTransform at = new AffineTransform();
                at.translate(x, y);
                at.rotate(rotation);
                at.scale(flipH ? -scale : scale, flipV ? -scale : scale);
                at.translate(-img.getWidth()/2.0, -img.getHeight()/2.0);

                AffineTransform inv = at.createInverse();
                Point2D local = inv.transform(p, null);
                return local.getX() >= 0 && local.getX() <= img.getWidth()
                    && local.getY() >= 0 && local.getY() <= img.getHeight();
            } catch (NoninvertibleTransformException ex) {
                return false;
            }
        }

        void draw(Graphics2D g) {
            AffineTransform at = new AffineTransform();
            at.translate(x, y);
            at.rotate(rotation);
            at.scale(flipH ? -scale : scale, flipV ? -scale : scale);
            at.translate(-img.getWidth()/2.0, -img.getHeight()/2.0);
            g.drawImage(img, at, null);
        }
    }

    private final List<RefImage> images = new ArrayList<>();
    private RefImage selected = null;
    private Point prevMouse = null;

    public ReferenceCanvasPanel() {
        setPreferredSize(new Dimension(800,600));
        setBackground(Color.WHITE);

        addMouseWheelListener(e -> {
            if (selected != null) {
                double delta = e.getPreciseWheelRotation();
                selected.scale *= (1 - delta*0.1);
                selected.scale = Math.max(0.1, selected.scale);
                repaint();
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) {
                Point2D p = e.getPoint();
                if (SwingUtilities.isLeftMouseButton(e)) {
                    selected = null;
                    for (int i = images.size()-1; i >= 0; i--) {
                        if (images.get(i).contains(p)) {
                            selected = images.get(i);
                            prevMouse = e.getPoint();
                            break;
                        }
                    }
                }
            }

            @Override public void mouseReleased(MouseEvent e) {
                // horizontal flip on right-click
                if (SwingUtilities.isRightMouseButton(e)) {
                    Point2D p = e.getPoint();
                    for (int i = images.size()-1; i >= 0; i--) {
                        RefImage ri = images.get(i);
                        if (ri.contains(p)) {
                            ri.flipH = !ri.flipH;
                            repaint();
                            break;
                        }
                    }
                }
            }

            @Override public void mouseClicked(MouseEvent e) {
                // vertical flip on double-click (left)
                if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2) {
                    Point2D p = e.getPoint();
                    for (int i = images.size()-1; i >= 0; i--) {
                        RefImage ri = images.get(i);
                        if (ri.contains(p)) {
                            ri.flipV = !ri.flipV;
                            repaint();
                            break;
                        }
                    }
                }
                super.mouseClicked(e);
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override public void mouseDragged(MouseEvent e) {
                if (selected != null && prevMouse != null) {
                    Point p = e.getPoint();
                    selected.x += p.x - prevMouse.x;
                    selected.y += p.y - prevMouse.y;
                    prevMouse = p;
                    repaint();
                }
            }
        });
    }

    public void addImage(BufferedImage img) {
        images.add(new RefImage(img));
        repaint();
    }

    public void clearAllImages() {
        images.clear();
        selected = null;
        repaint();
    }

    public void clearSelection() {
        selected = null;
    }

    public void rotateSelected(int deg) {
        if (selected != null) {
            selected.rotation = Math.toRadians(deg);
            repaint();
        }
    }

    @Override protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        for (RefImage ri : images) ri.draw(g2);
        g2.dispose();
    }
}
