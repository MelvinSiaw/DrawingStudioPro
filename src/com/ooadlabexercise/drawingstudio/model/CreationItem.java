package com.ooadlabexercise.drawingstudio.model;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public abstract class CreationItem {
    protected BufferedImage image;
    protected int x, y;
    protected double rotation = 0, scale = 1.0;

    public CreationItem(BufferedImage img, int x, int y) {
        this.image = img; this.x = x; this.y = y;
    }

    public void draw(Graphics2D g) {
        AffineTransform at = g.getTransform();
        g.translate(x, y);
        g.rotate(Math.toRadians(rotation), image.getWidth()/2, image.getHeight()/2);
        g.scale(scale, scale);
        g.drawImage(image, 0, 0, null);
        g.setTransform(at);
    }

    public boolean contains(Point p) {
        AffineTransform at = new AffineTransform();
        at.translate(x, y);
        at.rotate(Math.toRadians(rotation), image.getWidth()/2, image.getHeight()/2);
        at.scale(scale, scale);
        Shape s = at.createTransformedShape(new Rectangle(0, 0, image.getWidth(), image.getHeight()));
        return s.contains(p);
    }

    public void translate(int dx,int dy) { x += dx; y += dy; }
    public void rotate(double d) { rotation += d; }
    public void scale(double f) { scale *= f; }
    public void flip() { scale = -scale; }
}

