package ru.yanchikdev.lib;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

public class ImageUtils {
    public static Image scaleImageFX(Image toScale, double kx, double ky) {
        BufferedImage toScaleAwp = SwingFXUtils.fromFXImage(toScale, null);
        int w = toScaleAwp.getWidth();
        int h = toScaleAwp.getHeight();
        BufferedImage result = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        AffineTransform at = new AffineTransform();
        at.scale(kx, ky);
        AffineTransformOp scaleOp =
                new AffineTransformOp(at, AffineTransformOp.TYPE_BICUBIC);
        result = scaleOp.filter(toScaleAwp, result);
        return SwingFXUtils.toFXImage(result, null);
    }

    public static Image resizeImageFX(Image toResize, int w, int h){
        BufferedImage toResizeAwp = SwingFXUtils.fromFXImage(toResize, null);
        BufferedImage resized = new BufferedImage(w, h, toResizeAwp.getType());
        Graphics2D g = resized.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.drawImage(toResizeAwp, 0, 0, w, h, 0, 0, toResizeAwp.getWidth(),
                toResizeAwp.getHeight(), null);
        g.dispose();
        return SwingFXUtils.toFXImage(resized, null);
    }

    public static Image fitByWidthFX(Image toFit, int w){
        return resizeImageFX(toFit, w, (int) ((int)toFit.getHeight()*w/toFit.getWidth()));
    }
    public static Image fitByHeightFX(Image toFit, int h){
        return resizeImageFX(toFit, (int) (toFit.getWidth()*h/toFit.getHeight()), h);
    }

    public static BufferedImage scaleImage(BufferedImage toScale, double kx, double ky) {
        int w = toScale.getWidth();
        int h = toScale.getHeight();
        BufferedImage result = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        AffineTransform at = new AffineTransform();
        at.scale(kx, ky);
        AffineTransformOp scaleOp =
                new AffineTransformOp(at, AffineTransformOp.TYPE_BICUBIC);
        result = scaleOp.filter(toScale, result);
        return result;
    }

    public static BufferedImage resizeImage(BufferedImage toResize, int w, int h){
        BufferedImage resized = new BufferedImage(w, h, toResize.getType());
        Graphics2D g = resized.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.drawImage(toResize, 0, 0, w, h, 0, 0, toResize.getWidth(),
                toResize.getHeight(), null);
        g.dispose();
        return resized;
    }

    public static BufferedImage fitByWidth(BufferedImage toFit, int w){
        return resizeImage(toFit, w, (int) ((int)toFit.getHeight()*w/toFit.getWidth()));
    }
    public static BufferedImage fitByHeight(BufferedImage toFit, int h){
        return resizeImage(toFit, (int) (toFit.getWidth()*h/toFit.getHeight()), h);
    }


    public static BufferedImage rotateImageByDegrees(BufferedImage img, double angle) {

        double rads = Math.toRadians(angle);
        double sin = Math.abs(Math.sin(rads)), cos = Math.abs(Math.cos(rads));
        int w = img.getWidth();
        int h = img.getHeight();
        int newWidth = (int) Math.floor(w * cos + h * sin);
        int newHeight = (int) Math.floor(h * cos + w * sin);

        BufferedImage rotated = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = rotated.createGraphics();
        AffineTransform at = new AffineTransform();
        at.translate((newWidth - w) / 2, (newHeight - h) / 2);

        int x = w / 2;
        int y = h / 2;

        at.rotate(rads, x, y);
        g2d.setTransform(at);
        g2d.drawImage(img, 0, 0, null);
        g2d.dispose();

        return rotated;
    }
}
