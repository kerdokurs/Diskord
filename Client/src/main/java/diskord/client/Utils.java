package diskord.client;

import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

import java.io.*;
import java.util.Arrays;

public class Utils {
    /**
     * Utility method that writes xml file from resources to destination
     * @param resourceName The name of resource
     * @param dest File destination
     */
    public static void writeXmlFromResourcesToFile(String resourceName, File dest){
        try (InputStream fis = Utils.class.getClassLoader().getResourceAsStream(resourceName);
             FileOutputStream fos = new FileOutputStream(dest)) {
            int len;
            byte[] buffer = new byte[4096];
            while ((len = fis.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Creates single color image from parameters
     * https://stackoverflow.com/questions/60532986/javafx-generate-blank-single-color-image
     * @param width requested width
     * @param height requested height
     * @param red requested red value
     * @param green requested green value
     * @param blue requested blue value
     * @param opacity requested opacity
     * @return Image object that is created from parameters
     */
    public static Image generateImage(int width, int height, double red, double green, double blue, double opacity) {
        WritableImage img = new WritableImage(width, height);
        PixelWriter pw = img.getPixelWriter();
        int alpha = (int) (opacity * 255) ;
        int r = (int) (red * 255) ;
        int g = (int) (green * 255) ;
        int b = (int) (blue * 255) ;
        int pixel = (alpha << 24) | (r << 16) | (g << 8) | b ;
        int[] pixels = new int[width * height];
        Arrays.fill(pixels, pixel);
        pw.setPixels(0, 0, width, height, PixelFormat.getIntArgbInstance(), pixels, 0, width);
        return img ;
    }
}
