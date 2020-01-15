package code.lib;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

public class ImageUtils {
    public static Image scaleImage(Image toScale, double kx, double ky) {
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

    public static Image resizeImage(Image toResize, int w, int h){
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

    public static Image fitByWidth(Image toFit, int w){
        return resizeImage(toFit, w, (int) ((int)toFit.getHeight()*w/toFit.getWidth()));
    }
    public static Image fitByHeight(Image toFit, int h){
        return resizeImage(toFit, (int) (toFit.getWidth()*h/toFit.getHeight()), h);
    }
}
