package com.ooadlabexercise.drawingstudio.model;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import javax.imageio.ImageIO;

public class ResourceLoader {
    private static final String ANIM_DIR = "resources/images/animals";
    private static final String FLOW_DIR = "resources/images/flowers";
    private static final String CUST_DIR = "resources/images/custom";

    private static BufferedImage loadRandom(String dir) {
        File[] fs = new File(dir).listFiles();
        if (fs == null || fs.length == 0) return null;
        try { return ImageIO.read(fs[new Random().nextInt(fs.length)]); }
        catch (IOException e) { e.printStackTrace(); return null; }
    }

    private static BufferedImage loadLatest(String dir) {
        File[] fs = new File(dir).listFiles();
        if (fs == null || fs.length == 0) return null;
        try { return ImageIO.read(fs[fs.length-1]); }
        catch (IOException e) { e.printStackTrace(); return null; }
    }

    public static AnimalItem loadAnimalItem() {
        BufferedImage img = loadRandom(ANIM_DIR);
        return img != null ? new AnimalItem(img,50,50) : null;
    }

    public static FlowerItem loadFlowerItem() {
        BufferedImage img = loadRandom(FLOW_DIR);
        return img != null ? new FlowerItem(img,100,100) : null;
    }

    public static CustomImageItem loadCustomItem() {
        BufferedImage img = loadLatest(CUST_DIR);
        return img != null ? new CustomImageItem(img,150,150) : null;
    }

    public static void saveCustomDrawing(BufferedImage img) throws IOException {
        File d = new File(CUST_DIR);
        if (!d.exists()) d.mkdirs();
        File f = new File(d, "custom_" + System.currentTimeMillis() + ".png");
        ImageIO.write(img, "PNG", f);
    }

    public static CustomImageItem loadFromFile(File f) {
        try {
            BufferedImage img = ImageIO.read(f);
            return new CustomImageItem(img,50,50);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}